package com.example.job_scheduler.service;

import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.entity.JobRun;
import com.example.job_scheduler.entity.JobRunStatus;
import com.example.job_scheduler.repository.CronJobRepository;
import com.example.job_scheduler.repository.JobRunRepository;
import com.example.job_scheduler.util.CronExpressionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionService {
    private final JobRunRepository jobRunRepository;
    private final CronJobRepository cronJobRepository;
    private final CronExpressionHelper cronExpressionHelper;

    @Transactional
    public void executeJob(CronJob cronJob) {
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime finishedAt;
        JobRunStatus status;
        String errorMessage = null;

        // Only wrap job execution in try-catch
        try {
            log.info("Executing job: {} (id: {})", cronJob.getName(), cronJob.getId());
            simulateJobExecution();
            status = JobRunStatus.SUCCESS;
            log.info("Job execution succeeded: {}", cronJob.getName());
        } catch (Exception e) {
            status = JobRunStatus.FAILED;
            errorMessage = e.getMessage();
            log.error("Job execution failed: {} (id: {})", cronJob.getName(), cronJob.getId(), e);
        }

        // Calculate duration
        finishedAt = LocalDateTime.now();
        long durationMs = java.time.temporal.ChronoUnit.MILLIS.between(startedAt, finishedAt);

        // Record job run (both SUCCESS and FAILED)
        recordJobRun(cronJob, startedAt, finishedAt, durationMs, status, errorMessage);

        // Update next run time regardless of success or failure
        try {
            LocalDateTime nextRunAt = cronExpressionHelper.getNextRunTime(
                    cronJob.getCronExpression(),
                    finishedAt
            );
            cronJob.setNextRunAt(nextRunAt);
            cronJobRepository.save(cronJob);
            log.info("Job next run scheduled: {} (id: {}). Next run: {}", 
                    cronJob.getName(), cronJob.getId(), nextRunAt);
        } catch (Exception e) {
            log.error("Error calculating next run time for job: {} (id: {})", 
                    cronJob.getName(), cronJob.getId(), e);
            throw new RuntimeException("Failed to update next run time", e);
        }
    }

    private void simulateJobExecution() throws Exception {
        log.info("Job is running...");
        Thread.sleep(500);
    }

    private void recordJobRun(CronJob cronJob, LocalDateTime startedAt, LocalDateTime finishedAt,
                              long durationMs, JobRunStatus status, String errorMessage) {
        JobRun jobRun = JobRun.builder()
                .cronJob(cronJob)
                .status(status)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .durationMs(durationMs)
                .errorMessage(errorMessage)
                .build();
        
        jobRunRepository.save(jobRun);
    }
}
