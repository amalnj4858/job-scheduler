package com.example.job_scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    @Bean(name = "highPrecisionExecutor")
    public ThreadPoolExecutor highPrecisionExecutor() {
        return new ThreadPoolExecutor(
                5,                          // corePoolSize
                10,                         // maxPoolSize
                60,                         // keepAliveTime
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>() // unbounded queue
        );
    }

    @Bean(name = "mediumPrecisionExecutor")
    public ThreadPoolExecutor mediumPrecisionExecutor() {
        return new ThreadPoolExecutor(
                3,
                5,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    @Bean(name = "lowPrecisionExecutor")
    public ThreadPoolExecutor lowPrecisionExecutor() {
        return new ThreadPoolExecutor(
                2,
                3,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
    }
}
