package com.example.job_scheduler.util;

import org.quartz.CronExpression;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class CronExpressionHelper {

    /**
     * Validates if a cron expression is valid.
     */
    public boolean isValidCronExpression(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Calculates the next run time based on the cron expression.
     * Returns the next valid execution time after the given time.
     */
    public LocalDateTime getNextRunTime(String cronExpression, LocalDateTime afterTime) throws ParseException {
        CronExpression cron = new CronExpression(cronExpression);
        Date afterDate = convertLocalDateTimeToDate(afterTime);
        Date nextRunDate = cron.getNextValidTimeAfter(afterDate);
        
        if (nextRunDate == null) {
            throw new IllegalArgumentException("Could not calculate next run time for cron expression: " + cronExpression);
        }
        
        return convertDateToLocalDateTime(nextRunDate);
    }

    private Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
