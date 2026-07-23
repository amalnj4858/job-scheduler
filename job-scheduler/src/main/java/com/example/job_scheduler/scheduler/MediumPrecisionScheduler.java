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
public class MediumPrecisionScheduler extends AbstractPrecisionScheduler {
    private final CronJobRepository cronJobRepository;
    private final JobExecutionService jobExecutionService;
    
    @Qualifier("mediumPrecisionExecutor")
    private final ThreadPoolExecutor mediumPrecisionExecutor;
    
    private final Set<String> mediumInFlight = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedDelay = 5000)
    public void pollMediumPrecisionJobs() {
        poll(SchedulingPrecision.MEDIUM,
                cronJobRepository,
                jobExecutionService,
                mediumPrecisionExecutor,
                mediumInFlight,
                "MEDIUM",
                log);
    }
}
