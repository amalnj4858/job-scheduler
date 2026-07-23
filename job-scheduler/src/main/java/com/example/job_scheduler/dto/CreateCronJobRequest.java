package com.example.job_scheduler.dto;

import com.example.job_scheduler.entity.SchedulingPrecision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCronJobRequest {
    private String name;
    private String cronExpression;
    private SchedulingPrecision tier;
    private Boolean isEnabled;
}
