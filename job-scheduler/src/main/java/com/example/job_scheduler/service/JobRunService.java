package com.example.job_scheduler.service;

import com.example.job_scheduler.dto.JobRunResponse;
import com.example.job_scheduler.entity.JobRun;
import com.example.job_scheduler.repository.JobRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRunService {
    private final JobRunRepository jobRunRepository;

    public List<JobRunResponse> getJobRunsByJobId(String jobId) {
        List<JobRun> jobRuns = jobRunRepository.findByJobIdOrderByStartedAtDesc(jobId);
        
        return jobRuns.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<JobRunResponse> getOrphanedRuns() {
        List<JobRun> orphanedRuns = jobRunRepository.findOrphanedRuns();
        
        return orphanedRuns.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private JobRunResponse toResponse(JobRun jobRun) {
        JobRunResponse.JobRunResponseBuilder builder = JobRunResponse.builder()
                .id(jobRun.getId())
                .status(jobRun.getStatus())
                .startedAt(jobRun.getStartedAt())
                .finishedAt(jobRun.getFinishedAt())
                .durationMs(jobRun.getDurationMs())
                .errorMessage(jobRun.getErrorMessage());
        
        if (jobRun.getCronJob() != null) {
            builder.jobId(jobRun.getCronJob().getId())
                    .jobName(jobRun.getCronJob().getName());
        }
        
        return builder.build();
    }
}
