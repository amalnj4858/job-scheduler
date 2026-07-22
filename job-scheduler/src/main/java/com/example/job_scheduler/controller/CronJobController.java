package com.example.job_scheduler.controller;

import com.example.job_scheduler.dto.CreateCronJobRequest;
import com.example.job_scheduler.dto.CronJobResponse;
import com.example.job_scheduler.service.CronJobService;
import com.example.job_scheduler.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class CronJobController {
    private final CronJobService cronJobService;
    private final RequestValidator requestValidator;

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody CreateCronJobRequest request) {
        log.info("Creating job: {}", request.getName());
        
        var validationError = requestValidator.validateCreateCronJobRequest(request);
        if (validationError.isPresent()) {
            log.warn("Validation failed for job: {}", request.getName());
            return ResponseEntity.badRequest().body(validationError.get());
        }

        try {
            CronJobResponse response = cronJobService.createJob(request);
            log.info("Job created successfully with id: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating job: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create job: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CronJobResponse>> getAllJobs() {
        log.info("Fetching all jobs");
        return ResponseEntity.ok(cronJobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable String id) {
        log.info("Fetching job with id: {}", id);
        try {
            CronJobResponse response = cronJobService.getJobById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Job not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable String id, @RequestBody CreateCronJobRequest request) {
        log.info("Updating job with id: {}", id);
        
        var validationError = requestValidator.validateCreateCronJobRequest(request);
        if (validationError.isPresent()) {
            log.warn("Validation failed for job update: {}", id);
            return ResponseEntity.badRequest().body(validationError.get());
        }

        try {
            CronJobResponse response = cronJobService.updateJob(id, request);
            log.info("Job updated successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Job not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating job with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update job: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        log.info("Deleting job with id: {}", id);
        try {
            cronJobService.deleteJob(id);
            log.info("Job deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Job not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
