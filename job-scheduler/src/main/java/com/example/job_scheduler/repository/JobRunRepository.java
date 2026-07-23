package com.example.job_scheduler.repository;

import com.example.job_scheduler.entity.CronJob;
import com.example.job_scheduler.entity.JobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, String> {
    
    @Query("SELECT jr FROM JobRun jr WHERE jr.cronJob.id = :jobId ORDER BY jr.startedAt DESC")
    List<JobRun> findByJobIdOrderByStartedAtDesc(@Param("jobId") String jobId);
    
    List<JobRun> findByCronJob(CronJob cronJob);
    
    @Query("SELECT jr FROM JobRun jr WHERE jr.cronJob IS NULL ORDER BY jr.startedAt DESC")
    List<JobRun> findOrphanedRuns();
}
