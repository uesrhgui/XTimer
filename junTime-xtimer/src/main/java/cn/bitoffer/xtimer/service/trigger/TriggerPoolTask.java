package cn.bitoffer.xtimer.service.trigger;

import cn.bitoffer.common.redis.ReentrantDistributeLock;
import cn.bitoffer.xtimer.model.TaskModel;
import cn.bitoffer.xtimer.service.executor.ExecutorWorker;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author JunTroy
 * @email 1437743263@qq.com
 */
@Component
@Slf4j
public class TriggerPoolTask {

    @Autowired
    ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    ExecutorWorker executorWorker;


    @Async("triggerPool")
    public void runExecutor(TaskModel taskModel) {
        if (taskModel == null) {
            return;
        }
        log.info("开始执行任务：" + taskModel.getApp() + ":" + taskModel.getTaskId());

        executorWorker.work(TimerUtils.UnionTimerIDUnix(taskModel.getTimerId() , taskModel.getRunTimer()));

        log.info("任务执行完毕：" + taskModel.getApp() + ":" + taskModel.getTaskId());
    }
}
