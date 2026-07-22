package com.example.job_scheduler.scheduler;

import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.entity.SchedulingPrecision;
import com.example.job_scheduler.repository.CronJobRepository;
import com.example.job_scheduler.service.JobExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LowPrecisionScheduler {
    private final CronJobRepository cronJobRepository;
    private final JobExecutionService jobExecutionService;
    
    @Qualifier("lowPrecisionExecutor")
    private final ThreadPoolExecutor lowPrecisionExecutor;
    
    private final Set<String> lowInFlight = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedDelay = 60000)
    public void pollLowPrecisionJobs() {
        try {
            log.debug("LOW precision scheduler polling...");
            
            List<CronJob> dueJobs = cronJobRepository.findDueJobsByTier(
                    SchedulingPrecision.LOW,
                    LocalDateTime.now()
            );
            
            if (!dueJobs.isEmpty()) {
                log.info("Found {} LOW precision jobs due for execution", dueJobs.size());
            }
            
            for (CronJob job : dueJobs) {
                // Prevent duplicate execution in same cycle
                if (lowInFlight.add(job.getId())) {
                    log.debug("Submitting LOW precision job: {} to executor", job.getName());
                    lowPrecisionExecutor.submit(() -> {
                        try {
                            jobExecutionService.executeJob(job);
                        } finally {
                            lowInFlight.remove(job.getId());
                        }
                    });
                } else {
                    log.debug("Job {} already in flight, skipping", job.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error in LOW precision scheduler", e);
        }
    }
}
