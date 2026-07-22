package com.example.job_scheduler.service;

import com.example.job_scheduler.dto.CreateCronJobRequest;
import com.example.job_scheduler.dto.CronJobResponse;
import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.repository.CronJobRepository;
import com.example.job_scheduler.util.CronExpressionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CronJobService {
    private final CronJobRepository cronJobRepository;
    private final CronExpressionHelper cronExpressionHelper;

    public CronJobResponse createJob(CreateCronJobRequest request) {
        try {
            // Calculate next run time
            LocalDateTime nextRunAt = cronExpressionHelper.getNextRunTime(
                    request.getCronExpression(),
                    LocalDateTime.now()
            );

            CronJob cronJob = CronJob.builder()
                    .name(request.getName())
                    .cronExpression(request.getCronExpression())
                    .tier(request.getTier())
                    .nextRunAt(nextRunAt)
                    .build();

            CronJob savedJob = cronJobRepository.save(cronJob);
            return toResponse(savedJob);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create job: " + e.getMessage(), e);
        }
    }

    public CronJobResponse getJobById(String id) {
        CronJob cronJob = cronJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        return toResponse(cronJob);
    }

    public List<CronJobResponse> getAllJobs() {
        return cronJobRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CronJobResponse updateJob(String id, CreateCronJobRequest request) {
        CronJob cronJob = cronJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));

        try {
            // Recalculate next run time based on updated cron expression
            LocalDateTime nextRunAt = cronExpressionHelper.getNextRunTime(
                    request.getCronExpression(),
                    LocalDateTime.now()
            );

            cronJob.setName(request.getName());
            cronJob.setCronExpression(request.getCronExpression());
            cronJob.setTier(request.getTier());
            cronJob.setNextRunAt(nextRunAt);

            CronJob updatedJob = cronJobRepository.save(cronJob);
            return toResponse(updatedJob);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update job: " + e.getMessage(), e);
        }
    }

    public void deleteJob(String id) {
        CronJob cronJob = cronJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + id));
        cronJobRepository.delete(cronJob);
    }

    private CronJobResponse toResponse(CronJob cronJob) {
        return CronJobResponse.builder()
                .id(cronJob.getId())
                .name(cronJob.getName())
                .cronExpression(cronJob.getCronExpression())
                .tier(cronJob.getTier())
                .nextRunAt(cronJob.getNextRunAt())
                .createdAt(cronJob.getCreatedAt())
                .updatedAt(cronJob.getUpdatedAt())
                .build();
    }
}
