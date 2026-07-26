package cn.bitoffer.xtimer.common.pool;

import cn.bitoffer.xtimer.common.conf.SchedulerAppConf;
import cn.bitoffer.xtimer.common.conf.TriggerAppConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置类。
 * @author juntroy
 * <p>
 * 基于 Spring 的 {@link ThreadPoolTaskExecutor} 创建两个独立的线程池：
 * <ul>
 *   <li><b>schedulerPool</b> — 调度器线程池，用于执行定时任务的分片调度工作。</li>
 *   <li><b>triggerPool</b> — 触发器线程池，用于执行到期的定时任务触发工作。</li>
 * </ul>
 * 两个线程池均采用 {@link ThreadPoolExecutor.CallerRunsPolicy} 作为拒绝策略，
 * 当线程池达到最大容量时，任务将由调用者线程直接执行。
 * </p>
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncPool {

    /** 调度器应用配置，提供调度器线程池的参数 */
    @Autowired
    SchedulerAppConf schedulerAppConf;

    /** 触发器应用配置，提供触发器线程池的参数 */
    @Autowired
    TriggerAppConf triggerAppConf;

    /**
     * 创建调度器线程池 Bean。
     * <p>
     * 线程池的核心线程数、最大线程数、队列容量、线程名称前缀等参数
     * 均从 {@link SchedulerAppConf} 中读取。
     * </p>
     *
     * @return 调度器线程池执行器
     */
    @Bean(name = "schedulerPool")
    public Executor schedulerPoolExecutor() {
        log.info("start schedulerPoolExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(schedulerAppConf.getCorePoolSize());
        // 配置最大线程数
        executor.setMaxPoolSize(schedulerAppConf.getMaxPoolSize());
        // 配置队列大小
        executor.setQueueCapacity(schedulerAppConf.getQueueCapacity());
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(schedulerAppConf.getNamePrefix());

        // rejection-policy：当 pool 已经达到 max size 的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }

    /**
     * 创建触发器线程池 Bean。
     * <p>
     * 线程池的核心线程数、最大线程数、队列容量、线程名称前缀等参数
     * 均从 {@link TriggerAppConf} 中读取。
     * </p>
     *
     * @return 触发器线程池执行器
     */
    @Bean(name = "triggerPool")
    public Executor triggerPoolExecutor() {
        log.info("start triggerPoolExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(triggerAppConf.getCorePoolSize());
        // 配置最大线程数
        executor.setMaxPoolSize(triggerAppConf.getMaxPoolSize());
        // 配置队列大小
        executor.setQueueCapacity(triggerAppConf.getQueueCapacity());
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(triggerAppConf.getNamePrefix());

        // rejection-policy：当 pool 已经达到 max size 的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }
}