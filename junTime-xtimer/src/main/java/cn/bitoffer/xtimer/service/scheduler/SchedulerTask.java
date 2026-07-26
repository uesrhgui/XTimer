package cn.bitoffer.xtimer.service.scheduler;

import cn.bitoffer.common.redis.ReentrantDistributeLock;
import cn.bitoffer.xtimer.common.conf.SchedulerAppConf;
import cn.bitoffer.xtimer.service.trigger.TriggerWorker;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 任务调度器
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Component
@Slf4j
public class SchedulerTask {

    @Autowired
    ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    SchedulerAppConf schedulerAppConf;

    @Autowired
    TriggerWorker triggerWorker;

    @Async("schedulerPool")
    public void asyncHandleSlice(Date date , int bucketId) {
        log.info("开始处理分片：{}",bucketId);

        String lockToken = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetTimeBucketLockKey(date,bucketId),
                lockToken,
                schedulerAppConf.getTryLockSeconds()
        );
        if (!ok){
            log.info("分片{}已存在任务，请稍后再试！",bucketId);
            return;
        }

        log.info("分片{}开始处理",bucketId);

        triggerWorker.work(TimerUtils.GetSliceMsgKey(date,bucketId));

        // 延期锁
        reentrantDistributeLock.expireLock(
                TimerUtils.GetTimeBucketLockKey(date,bucketId),
                lockToken,
                schedulerAppConf.getSuccessExpireSeconds()
        );

        log.info("分片{}处理完成",bucketId);
    }
}
