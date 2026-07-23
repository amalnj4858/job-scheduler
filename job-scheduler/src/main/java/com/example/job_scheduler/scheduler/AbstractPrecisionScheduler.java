package com.example.job_scheduler.scheduler;

import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.entity.SchedulingPrecision;
import com.example.job_scheduler.repository.CronJobRepository;
import com.example.job_scheduler.service.JobExecutionService;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractPrecisionScheduler {

    protected void poll(SchedulingPrecision precision,
                        CronJobRepository cronJobRepository,
                        JobExecutionService jobExecutionService,
                        ThreadPoolExecutor executor,
                        Set<String> inFlight,
                        String label,
                        Logger log) {
        try {
            log.debug("{} precision scheduler polling...", label);

            List<CronJob> dueJobs = cronJobRepository.findDueJobsByTier(
                    precision,
                    LocalDateTime.now()
            );

            if (!dueJobs.isEmpty()) {
                log.info("Found {} {} precision jobs due for execution", dueJobs.size(), label);
            }

            for (CronJob job : dueJobs) {
                if (inFlight.add(job.getId())) {
                    log.debug("Submitting {} precision job: {} to executor", label, job.getName());
                    executor.submit(() -> {
                        try {
                            jobExecutionService.executeJob(job);
                        } finally {
                            inFlight.remove(job.getId());
                        }
                    });
                } else {
                    log.debug("Job {} already in flight, skipping", job.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error in {} precision scheduler", label, e);
        }
    }
}
