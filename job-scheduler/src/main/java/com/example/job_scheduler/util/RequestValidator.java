package com.example.job_scheduler.util;

import com.example.job_scheduler.dto.CreateCronJobRequest;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RequestValidator {
    private final CronExpressionHelper cronExpressionHelper;

    public RequestValidator(CronExpressionHelper cronExpressionHelper) {
        this.cronExpressionHelper = cronExpressionHelper;
    }

    /**
     * Validates a CreateCronJobRequest and returns validation errors if any.
     * Returns Optional.empty() if valid, or Optional with error map if invalid.
     */
    public Optional<Map<String, String>> validateCreateCronJobRequest(CreateCronJobRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.put("error", "Job name is required");
        }

        if (request.getCronExpression() == null || request.getCronExpression().trim().isEmpty()) {
            errors.put("error", "Cron expression is required");
        }

        if (request.getTier() == null) {
            errors.put("error", "Scheduling tier is required");
        }

        if (request.getCronExpression() != null && !request.getCronExpression().trim().isEmpty()) {
            if (!cronExpressionHelper.isValidCronExpression(request.getCronExpression())) {
                errors.put("error", "Invalid cron expression: " + request.getCronExpression());
            }
        }

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }
}
