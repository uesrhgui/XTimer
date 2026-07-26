package cn.bitoffer.xtimer.common.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 触发器（Trigger）应用配置类。
 * @author juntroy
 * <p>
 * 负责从配置文件中加载触发器相关的参数，包括 ZRange 扫描间隔、
 * 工作线程数以及线程池相关配置等。
 * </p>
 */
@Component
public class TriggerAppConf {

    /** ZRange 扫描间隔时间（秒），控制每次扫描 Redis 有序集合的时间窗口 */
    @Value("${trigger.zrangeGapSeconds}")
    private int zrangeGapSeconds;

    /** 触发器工作线程数 */
    @Value("${trigger.workersNum}")
    private int workersNum;

    /** 线程池核心线程数 */
    @Value("${trigger.pool.corePoolSize}")
    private int corePoolSize;

    /** 线程池最大线程数 */
    @Value("${trigger.pool.maxPoolSize}")
    private int maxPoolSize;

    /** 线程池队列容量 */
    @Value("${trigger.pool.queueCapacity}")
    private int queueCapacity;

    /** 线程池中线程名称前缀 */
    @Value("${trigger.pool.namePrefix}")
    private String namePrefix;

    /**
     * 获取 ZRange 扫描间隔时间（秒）。
     *
     * @return 扫描间隔时间（秒）
     */
    public int getZrangeGapSeconds() {
        return zrangeGapSeconds;
    }

    /**
     * 设置 ZRange 扫描间隔时间（秒）。
     *
     * @param zrangeGapSeconds 扫描间隔时间（秒）
     */
    public void setZrangeGapSeconds(int zrangeGapSeconds) {
        this.zrangeGapSeconds = zrangeGapSeconds;
    }

    /**
     * 获取触发器工作线程数。
     *
     * @return 工作线程数
     */
    public int getWorkersNum() {
        return workersNum;
    }

    /**
     * 设置触发器工作线程数。
     *
     * @param workersNum 工作线程数
     */
    public void setWorkersNum(int workersNum) {
        this.workersNum = workersNum;
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