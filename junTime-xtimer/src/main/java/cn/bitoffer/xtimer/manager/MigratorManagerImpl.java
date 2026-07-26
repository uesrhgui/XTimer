package cn.bitoffer.xtimer.manager;

import cn.bitoffer.common.redis.ReentrantDistributeLock;
import cn.bitoffer.xtimer.common.conf.MigratorAppConf;
import cn.bitoffer.xtimer.enums.TaskStatus;
import cn.bitoffer.xtimer.enums.TimerStatus;
import cn.bitoffer.xtimer.exception.BusinessException;
import cn.bitoffer.xtimer.exception.ErrorCode;
import cn.bitoffer.xtimer.mapper.TaskMapper;
import cn.bitoffer.xtimer.mapper.TimerMapper;
import cn.bitoffer.xtimer.model.TaskModel;
import cn.bitoffer.xtimer.model.TimerModel;
import cn.bitoffer.xtimer.redis.TaskCache;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author JunTroy
 * &#064;email  1437743263@qq.com
 */
@Service
@Slf4j
public class MigratorManagerImpl implements MigratorManager{

    @Autowired
    private TimerMapper timerMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    private MigratorAppConf migratorAppConf;

    @Autowired
    private TaskCache taskCache;

    @Override
    public void migrateTimer(TimerModel timerModel) {
        // 1. 校验状态
        if (timerModel.getStatus() != TimerStatus.Enable.getStatus()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "定时任务状态异常，迁移失败");
        }
        // 2. 获取批量的执行时机
        CronExpression cronExpression;
        try {
            // 解析cron
            cronExpression = new CronExpression(timerModel.getCron());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "定时任务cron表达式异常，迁移失败");
        }

        Date now = new Date();
        Date end = TimerUtils.GetForwardTwoMigrateStepEnd(now, migratorAppConf.getMigrateStepMinutes()); // 两个小时

        // 获取在 now ~ end 的所有执行时机
        List<Long> executeTimes = TimerUtils.GetCronNextsBetween(cronExpression, now, end);
        if (CollectionUtils.isEmpty(executeTimes)){
            log.warn("定时任务{}没有可迁移的时机" , timerModel.getTimerId());
            return;
        }

        // 加入数据库
        List<TaskModel> taskList = batchTasksFromTimer(timerModel, executeTimes);
        taskMapper.batchSave(taskList);

        // 缓存
        boolean b = taskCache.cacheSaveTasks(taskList);
        if (!b){
            log.warn("定时任务{}缓存失败" , timerModel.getTimerId());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "定时任务缓存失败,ID：" + timerModel.getTimerId());
        }
    }

    /**
     * 从定时任务中批量生成任务
     * @param timerModel 定时任务
     * @param executeTimes 执行时机
     * @return 任务列表
     */
    private List<TaskModel> batchTasksFromTimer(TimerModel timerModel, List<Long> executeTimes){
        if(timerModel == null || CollectionUtils.isEmpty(executeTimes)){
            return null;
        }

        List<TaskModel> taskList = new ArrayList<>();
        // 生成任务
        for (Long runTimer:executeTimes) {
            TaskModel task = new TaskModel();
            task.setApp(timerModel.getApp());
            task.setTimerId(timerModel.getTimerId());
            task.setRunTimer(runTimer);
            task.setStatus(TaskStatus.NotRun.getStatus());
            taskList.add(task);
        }
        return taskList;
    }
}
