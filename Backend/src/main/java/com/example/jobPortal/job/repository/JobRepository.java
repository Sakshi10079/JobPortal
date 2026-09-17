package com.example.jobPortal.job.repository;

import com.example.jobPortal.entity.Job;
import com.example.jobPortal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
}
