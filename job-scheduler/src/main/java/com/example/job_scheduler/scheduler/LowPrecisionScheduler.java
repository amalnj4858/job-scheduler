package com.example.job_scheduler.scheduler;

import com.example.job_scheduler.entity.SchedulingPrecision;
import com.example.job_scheduler.repository.CronJobRepository;
import com.example.job_scheduler.service.JobExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LowPrecisionScheduler extends AbstractPrecisionScheduler {
    private final CronJobRepository cronJobRepository;
    private final JobExecutionService jobExecutionService;
    
    @Qualifier("lowPrecisionExecutor")
    private final ThreadPoolExecutor lowPrecisionExecutor;
    
    private final Set<String> lowInFlight = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedDelay = 60000)
    public void pollLowPrecisionJobs() {
        poll(SchedulingPrecision.LOW,
                cronJobRepository,
                jobExecutionService,
                lowPrecisionExecutor,
                lowInFlight,
                "LOW",
                log);
    }
}
