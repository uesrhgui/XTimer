package cn.bitoffer.xtimer.service.scheduler;

import cn.bitoffer.xtimer.common.conf.SchedulerAppConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 定时任务调度
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Component
@Slf4j
public class SchedulerWorker {

    @Autowired
    SchedulerAppConf schedulerAppConf;

    @Autowired
    SchedulerTask schedulerTask;

    // 每秒执行一次
    @Scheduled(fixedRate = 1000)
    public void schedulerTask() {
        log.info(
                "开始执行定时任务，执行时间：" + LocalDateTime.now()
        );
        handleSlices();
    }

    public void handleSlices() {
        for (int i = 0 ; i < schedulerAppConf.getBucketsNum() ; i++){
            handleSlice(i);
        }
    }

    public void handleSlice(int bucketId) {
        Date now = new Date();
        // 上一分钟
        Date nowPreMin = new Date(now.getTime() - 60000);
        try {
            schedulerTask.asyncHandleSlice(nowPreMin , bucketId);
        } catch (Exception e) {
            log.error("处理(上分钟)定时任务失败：" + e.getMessage());
        }

        try {
            schedulerTask.asyncHandleSlice(now , bucketId);
        } catch (Exception e) {
            log.error("处理定时任务失败：" + e.getMessage());
        }
    }
}
