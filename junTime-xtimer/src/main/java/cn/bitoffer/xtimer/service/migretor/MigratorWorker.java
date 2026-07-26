package cn.bitoffer.xtimer.service.migretor;

import cn.bitoffer.common.redis.ReentrantDistributeLock;
import cn.bitoffer.xtimer.common.conf.MigratorAppConf;
import cn.bitoffer.xtimer.enums.TimerStatus;
import cn.bitoffer.xtimer.manager.MigratorManager;
import cn.bitoffer.xtimer.mapper.TimerMapper;
import cn.bitoffer.xtimer.model.TimerModel;
import cn.bitoffer.xtimer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 迁移任务执行器
 * @author JunTroy
 * &#064;email  1437743263@qq.com
 */
@Component
@Slf4j
public class MigratorWorker {

    @Autowired
    private MigratorManager migratorManager;

    @Autowired
    private TimerMapper timerMapper;

    @Autowired
    private MigratorAppConf migratorAppConf;

    @Autowired
    private ReentrantDistributeLock reentrantDistributeLock;

    // 每小时执行一次
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void work() {
        log.info("开始执行迁移任务，迁移时间：" + LocalDateTime.now());
        Date startHour = getStartHour(new Date());
        String lockToken = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetMigratorLockKey(startHour),
                lockToken,
                60L * migratorAppConf.getMigrateTryLockMinutes() // 锁定时间
        );
        if (!ok){
            log.warn("当前时间已存在迁移任务，请稍后再试！");
            return;
        }

        migrate();
    }

    // 获取开始时间
    private Date getStartHour(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        try {
            return sdf.parse(sdf.format(date));
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    // 迁移
    public void migrate() {
        List<TimerModel> timers = timerMapper.getTimersByStatus(TimerStatus.Enable.getStatus());
        if (CollectionUtils.isEmpty(timers)){
            log.info("没有需要迁移的定时任务");
        }

        for (TimerModel timerModel : timers) {
            migratorManager.migrateTimer(timerModel);
        }
    }

}
