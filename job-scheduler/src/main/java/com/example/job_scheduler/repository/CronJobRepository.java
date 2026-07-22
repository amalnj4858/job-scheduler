package com.example.job_scheduler.repository;

import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.entity.SchedulingPrecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CronJobRepository extends JpaRepository<CronJob, String> {
    
    @Query("SELECT cj FROM CronJob cj WHERE cj.tier = :tier AND cj.nextRunAt <= :now")
    List<CronJob> findDueJobsByTier(@Param("tier") SchedulingPrecision tier, @Param("now") LocalDateTime now);
}

