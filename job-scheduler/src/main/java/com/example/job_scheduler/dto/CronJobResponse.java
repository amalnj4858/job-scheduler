package com.example.job_scheduler.dto;

import com.example.job_scheduler.entity.SchedulingPrecision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CronJobResponse {
    private String id;
    private String name;
    private String cronExpression;
    private SchedulingPrecision tier;
    private LocalDateTime nextRunAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
