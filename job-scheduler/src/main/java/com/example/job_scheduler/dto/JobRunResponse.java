package com.example.job_scheduler.dto;

import com.example.job_scheduler.entity.JobRunStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRunResponse {
    private String id;
    private String jobId;
    private String jobName;
    private JobRunStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private long durationMs;
    private String errorMessage;
}
