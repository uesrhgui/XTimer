package cn.bitoffer.xtimer.redis;

import cn.bitoffer.xtimer.common.conf.SchedulerAppConf;
import cn.bitoffer.xtimer.exception.BusinessException;
import cn.bitoffer.xtimer.exception.ErrorCode;
import cn.bitoffer.xtimer.model.TaskModel;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 任务缓存
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Component
@Slf4j
public class TaskCache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    SchedulerAppConf schedulerAppConf;

    /**
     * 获取任务表名
     * @param taskModel
     * @return
     */
    public String GetTableName(TaskModel taskModel){
        // 分桶数量
        int maxBucket = schedulerAppConf.getBucketsNum();

        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = sdf.format(new Date(taskModel.getRunTimer()));
        long index = taskModel.getTimerId()%maxBucket;
        // 构建缓存key
        return sb.append(timeStr).append("_").append(index).toString();
    }

    /**
     * 缓存任务
     * @param taskList 任务列表
     * @return 缓存结果
     */
    public boolean cacheSaveTasks(List<TaskModel> taskList){

        try {
            // 保证原子性
            SessionCallback sessionCallback = new SessionCallback() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    redisOperations.multi();
                    for (TaskModel task : taskList) {
                        long unix = task.getRunTimer();
                        String tableName = GetTableName(task);
                        // 缓存任务
                        redisTemplate.opsForZSet().add(
                                tableName,
                                TimerUtils.UnionTimerIDUnix(task.getTimerId(), unix),
                                unix);
                    }
                    return redisOperations.exec(); //2025-11-06 21:54_1
                }
            };
            // 执行缓存任务
            redisTemplate.execute(sessionCallback);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从缓存中获取任务
     * @param key 缓存key
     * @param start 开始时间
     * @param end 结束时间
     * @return 任务列表
     */
    public List<TaskModel> getTasksFromCache(String key,long start, long end){
        List<TaskModel> tasks = new ArrayList<>();

        // 从ZSET 获取1秒范围的任务；
        Set<Object> timerIDUnixs = redisTemplate.opsForZSet().rangeByScore(key,start,end-1);
        if(CollectionUtils.isEmpty(timerIDUnixs)){
            return tasks;
        }

        for (Object timerIDUnixObj:timerIDUnixs) {
            TaskModel task = new TaskModel();
            String timerIDUnix = (String) timerIDUnixObj;
            List<Long> longSet = TimerUtils.SplitTimerIDUnix(timerIDUnix);
            if(longSet.size() != 2){
                log.error("splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"splitTimerIDUnix 错误, timerIDUnix:"+timerIDUnix);
            }
            task.setTimerId(longSet.get(0));
            task.setRunTimer(longSet.get(1));
            tasks.add(task);
        }

        return tasks;
    }

}
