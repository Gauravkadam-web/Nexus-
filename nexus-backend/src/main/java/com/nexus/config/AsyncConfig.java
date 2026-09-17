package com.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for Nexus background tasks.
 * Registers a dedicated thread pool ({@code nexusAiExecutor}) for AI operations,
 * keeping them isolated from the main Spring MVC thread pool.
 * <p>
 * This enables {@code @Async("nexusAiExecutor")} usage in AI services and event listeners.
 * No external queue or worker process is needed — Spring's thread pool is sufficient for V1 load.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool executor for AI-related async tasks.
     * Core: 2 threads, Max: 5, Queue capacity: 50.
     * Threads are named {@code nexus-ai-N} for easy identification in logs and profilers.
     *
     * @return the configured executor
     */
    @Bean(name = "nexusAiExecutor")
    public Executor nexusAiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("nexus-ai-");
        executor.initialize();
        return executor;
    }
}
