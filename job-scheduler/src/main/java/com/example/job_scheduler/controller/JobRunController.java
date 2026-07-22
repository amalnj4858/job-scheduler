package com.example.job_scheduler.controller;

import com.example.job_scheduler.dto.JobRunResponse;
import com.example.job_scheduler.service.JobRunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/jobs/{jobId}/runs")
@RequiredArgsConstructor
public class JobRunController {
    private final JobRunService jobRunService;

    @GetMapping
    public ResponseEntity<List<JobRunResponse>> getJobRuns(@PathVariable String jobId) {
        log.info("Fetching job runs for job id: {}", jobId);
        
        List<JobRunResponse> responses = jobRunService.getJobRunsByJobId(jobId);
        
        log.info("Found {} job runs for job id: {}", responses.size(), jobId);
        return ResponseEntity.ok(responses);
    }
}
