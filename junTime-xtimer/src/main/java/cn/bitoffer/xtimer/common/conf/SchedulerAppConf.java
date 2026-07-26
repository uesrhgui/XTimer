package cn.bitoffer.xtimer.common.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 调度器（Scheduler）应用配置类。
 * @author juntroy
 * <p>
 * 负责从配置文件中加载调度器相关的参数，包括分桶数量、分布式锁超时时间、
 * 成功记录过期时间以及线程池相关配置等。
 * </p>
 */
@Component
public class SchedulerAppConf {

    /** 时间分桶数量，用于分散调度压力 */
    @Value("${scheduler.bucketsNum}")
    private int bucketsNum;

    /** 分布式锁尝试加锁超时时间（秒） */
    @Value("${scheduler.tryLockSeconds}")
    private int tryLockSeconds;

    /** 分布式锁尝试加锁间隔时间（毫秒） */
    @Value("${scheduler.tryLockGapMilliSeconds}")
    private int tryLockGapMilliSeconds;

    /** 调度成功记录过期时间（秒） */
    @Value("${scheduler.successExpireSeconds}")
    private int successExpireSeconds;

    /** 线程池核心线程数 */
    @Value("${scheduler.pool.corePoolSize}")
    private int corePoolSize;

    /** 线程池最大线程数 */
    @Value("${scheduler.pool.maxPoolSize}")
    private int maxPoolSize;

    /** 线程池队列容量 */
    @Value("${scheduler.pool.queueCapacity}")
    private int queueCapacity;

    /** 线程池中线程名称前缀 */
    @Value("${scheduler.pool.namePrefix}")
    private String namePrefix;

    /**
     * 获取时间分桶数量。
     *
     * @return 分桶数量
     */
    public int getBucketsNum() {
        return bucketsNum;
    }

    /**
     * 设置时间分桶数量。
     *
     * @param bucketsNum 分桶数量
     */
    public void setBucketsNum(int bucketsNum) {
        this.bucketsNum = bucketsNum;
    }

    /**
     * 获取分布式锁尝试加锁超时时间（秒）。
     *
     * @return 锁超时时间（秒）
     */
    public int getTryLockSeconds() {
        return tryLockSeconds;
    }

    /**
     * 设置分布式锁尝试加锁超时时间（秒）。
     *
     * @param tryLockSeconds 锁超时时间（秒）
     */
    public void setTryLockSeconds(int tryLockSeconds) {
        this.tryLockSeconds = tryLockSeconds;
    }

    /**
     * 获取分布式锁尝试加锁间隔时间（毫秒）。
     *
     * @return 间隔时间（毫秒）
     */
    public int getTryLockGapMilliSeconds() {
        return tryLockGapMilliSeconds;
    }

    /**
     * 设置分布式锁尝试加锁间隔时间（毫秒）。
     *
     * @param tryLockGapMilliSeconds 间隔时间（毫秒）
     */
    public void setTryLockGapMilliSeconds(int tryLockGapMilliSeconds) {
        this.tryLockGapMilliSeconds = tryLockGapMilliSeconds;
    }

    /**
     * 获取调度成功记录过期时间（秒）。
     *
     * @return 过期时间（秒）
     */
    public int getSuccessExpireSeconds() {
        return successExpireSeconds;
    }

    /**
     * 设置调度成功记录过期时间（秒）。
     *
     * @param successExpireSeconds 过期时间（秒）
     */
    public void setSuccessExpireSeconds(int successExpireSeconds) {
        this.successExpireSeconds = successExpireSeconds;
    }

    /**
     * 获取线程池核心线程数。
     *
     * @return 核心线程数
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * 设置线程池核心线程数。
     *
     * @param corePoolSize 核心线程数
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * 获取线程池最大线程数。
     *
     * @return 最大线程数
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * 设置线程池最大线程数。
     *
     * @param maxPoolSize 最大线程数
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * 获取线程池队列容量。
     *
     * @return 队列容量
     */
    public int getQueueCapacity() {
        return queueCapacity;
    }

    /**
     * 设置线程池队列容量。
     *
     * @param queueCapacity 队列容量
     */
    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    /**
     * 获取线程池线程名称前缀。
     *
     * @return 名称前缀
     */
    public String getNamePrefix() {
        return namePrefix;
    }

    /**
     * 设置线程池线程名称前缀。
     *
     * @param namePrefix 名称前缀
     */
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
}