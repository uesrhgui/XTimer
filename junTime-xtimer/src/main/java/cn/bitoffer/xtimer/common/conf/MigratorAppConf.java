package cn.bitoffer.xtimer.common.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 迁移器（Migrator）应用配置类。
 * @author juntroy
 * <p>
 * 负责从配置文件中加载迁移器相关的参数，包括工作线程数、迁移步长、
 * 过期时间、锁超时时间以及定时器详情缓存时间等。
 * </p>
 */
@Component
public class MigratorAppConf {

    /** 迁移器工作线程数 */
    @Value("${migrator.workersNum}")
    private int workersNum;

    /** 迁移步长（分钟），每次迁移处理的时间窗口大小 */
    @Value("${migrator.migrateStepMinutes}")
    private int migrateStepMinutes;

    /** 迁移成功记录过期时间（分钟） */
    @Value("${migrator.migrateSuccessExpireMinutes}")
    private int migrateSuccessExpireMinutes;

    /** 迁移尝试加锁超时时间（分钟） */
    @Value("${migrator.migrateTryLockMinutes}")
    private int migrateTryLockMinutes;

    /** 定时器详情缓存时间（分钟） */
    @Value("${migrator.timerDetailCacheMinutes}")
    private int timerDetailCacheMinutes;

    /**
     * 获取迁移器工作线程数。
     *
     * @return 工作线程数
     */
    public int getWorkersNum() {
        return workersNum;
    }

    /**
     * 设置迁移器工作线程数。
     *
     * @param workersNum 工作线程数
     */
    public void setWorkersNum(int workersNum) {
        this.workersNum = workersNum;
    }

    /**
     * 获取迁移步长（分钟）。
     *
     * @return 迁移步长（分钟）
     */
    public int getMigrateStepMinutes() {
        return migrateStepMinutes;
    }

    /**
     * 设置迁移步长（分钟）。
     *
     * @param migrateStepMinutes 迁移步长（分钟）
     */
    public void setMigrateStepMinutes(int migrateStepMinutes) {
        this.migrateStepMinutes = migrateStepMinutes;
    }

    /**
     * 获取迁移成功记录过期时间（分钟）。
     *
     * @return 过期时间（分钟）
     */
    public int getMigrateSuccessExpireMinutes() {
        return migrateSuccessExpireMinutes;
    }

    /**
     * 设置迁移成功记录过期时间（分钟）。
     *
     * @param migrateSuccessExpireMinutes 过期时间（分钟）
     */
    public void setMigrateSuccessExpireMinutes(int migrateSuccessExpireMinutes) {
        this.migrateSuccessExpireMinutes = migrateSuccessExpireMinutes;
    }

    /**
     * 获取迁移尝试加锁超时时间（分钟）。
     *
     * @return 锁超时时间（分钟）
     */
    public int getMigrateTryLockMinutes() {
        return migrateTryLockMinutes;
    }

    /**
     * 设置迁移尝试加锁超时时间（分钟）。
     *
     * @param migrateTryLockMinutes 锁超时时间（分钟）
     */
    public void setMigrateTryLockMinutes(int migrateTryLockMinutes) {
        this.migrateTryLockMinutes = migrateTryLockMinutes;
    }

    /**
     * 获取定时器详情缓存时间（分钟）。
     *
     * @return 缓存时间（分钟）
     */
    public int getTimerDetailCacheMinutes() {
        return timerDetailCacheMinutes;
    }

    /**
     * 设置定时器详情缓存时间（分钟）。
     *
     * @param timerDetailCacheMinutes 缓存时间（分钟）
     */
    public void setTimerDetailCacheMinutes(int timerDetailCacheMinutes) {
        this.timerDetailCacheMinutes = timerDetailCacheMinutes;
    }
}
