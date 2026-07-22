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
public class MediumPrecisionScheduler {
    private final CronJobRepository cronJobRepository;
    private final JobExecutionService jobExecutionService;
    
    @Qualifier("mediumPrecisionExecutor")
    private final ThreadPoolExecutor mediumPrecisionExecutor;
    
    private final Set<String> mediumInFlight = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedDelay = 5000)
    public void pollMediumPrecisionJobs() {
        try {
            log.debug("MEDIUM precision scheduler polling...");
            
            List<CronJob> dueJobs = cronJobRepository.findDueJobsByTier(
                    SchedulingPrecision.MEDIUM,
                    LocalDateTime.now()
            );
            
            if (!dueJobs.isEmpty()) {
                log.info("Found {} MEDIUM precision jobs due for execution", dueJobs.size());
            }
            
            for (CronJob job : dueJobs) {
                // Prevent duplicate execution in same cycle
                if (mediumInFlight.add(job.getId())) {
                    log.debug("Submitting MEDIUM precision job: {} to executor", job.getName());
                    mediumPrecisionExecutor.submit(() -> {
                        try {
                            jobExecutionService.executeJob(job);
                        } finally {
                            mediumInFlight.remove(job.getId());
                        }
                    });
                } else {
                    log.debug("Job {} already in flight, skipping", job.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error in MEDIUM precision scheduler", e);
        }
    }
}
