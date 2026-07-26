package cn.bitoffer.xtimer.service.trigger;

import cn.bitoffer.xtimer.common.conf.TriggerAppConf;
import cn.bitoffer.xtimer.enums.TaskStatus;
import cn.bitoffer.xtimer.enums.TimerStatus;
import cn.bitoffer.xtimer.mapper.TaskMapper;
import cn.bitoffer.xtimer.model.TaskModel;
import cn.bitoffer.xtimer.redis.TaskCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Slf4j
public class TriggerTimerTask extends TimerTask {

    /**
     * 触发任务
     */
    TriggerAppConf triggerAppConf;

    /**
     * 触发任务池
     */
    TriggerPoolTask triggerPoolTask;

    /**
     * 任务缓存
     */
    TaskCache taskCache;

    /**
     * 任务Mapper
     */
    TaskMapper taskMapper;

    /**
     * 锁
     */
    private CountDownLatch latch;
    /**
     * 锁计数器
     */
    private Long count = 0L;

    /**
     * 触发时间
     */
    private Date startTime;

    /**
     * 触发结束时间
     */
    private Date endTime;

    /**
     * 分钟缓存key
     */
    private String minuteBucketKey;

    public TriggerTimerTask(TriggerAppConf triggerAppConf, TriggerPoolTask triggerPoolTask,
                            TaskCache taskCache, TaskMapper taskMapper, CountDownLatch latch,
                            Date startTime, Date endTime, String minuteBucketKey) {
        this.triggerAppConf = triggerAppConf;
        this.triggerPoolTask = triggerPoolTask;
        this.taskCache = taskCache;
        this.taskMapper = taskMapper;
        this.latch = latch;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minuteBucketKey = minuteBucketKey;
    }

    @Override
    public void run() {
        Date tStart = new Date(startTime.getTime() + count * triggerAppConf.getZrangeGapSeconds() * 1000L);
        if (tStart.compareTo(endTime) > 0) {
            latch.countDown();
            return;
        }
        // 处理1秒任务: 【tStart+1秒】这个范围的任务。例如 3秒-4秒
        try{
            handleBatch(tStart, new Date(tStart.getTime() + triggerAppConf.getZrangeGapSeconds()*1000L));
        }catch (Exception e){
            log.error("处理任务异常：{}",e.getMessage());
        }
        count++;
    }

    private void handleBatch(Date start, Date end){
        //获取待触发的任务
        List<TaskModel> tasks = getTasksByTime(start,end);
        if (CollectionUtils.isEmpty(tasks)){
            return;
        }
        // 从ZSET捞到的一批触发任务，现在需要遍历挨个执行
        for (TaskModel task :tasks) {
            try {
                if(task == null){
                    continue;
                }
                // 调用【执行模块Executor】，执行任务；
                triggerPoolTask.runExecutor(task);
            }catch (Exception e){
                log.error("执行任务异常：{}",e.getMessage());
            }
        }
    }

    private List<TaskModel> getTasksByTime(Date start ,Date end) {
        List<TaskModel> tasks = new ArrayList<>();

        try {
            tasks = taskCache.getTasksFromCache(minuteBucketKey ,start.getTime(),end.getTime());
        } catch (Exception e) {
            log.error("获取任务缓存异常：{}",e.getMessage());
            // 从数据库获取
            try {
                tasks = taskMapper.getTasksByTimeRange(start.getTime(),end.getTime() -1 , TaskStatus.NotRun.getStatus());
            } catch (Exception e1) {
                log.error("获取任务异常：{}",e1.getMessage());
            }
        }
        return tasks;
    }
}
