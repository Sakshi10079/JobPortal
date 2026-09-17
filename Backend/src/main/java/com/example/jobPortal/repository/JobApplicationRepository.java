package com.example.jobPortal.repository;

import com.example.jobPortal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Delete an application by user ID and job ID
    void deleteByUserIdAndJobId(Long userId, Long jobId);

    // Find all applications by user ID
//    List<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId);

    List<JobApplication> findByJobIdOrderByAppliedAtAsc(Long jobId);

    @Modifying
    int updateStatusAndNotesById(@Param("status") String status, @Param("notes") String notes,
                                 @Param("id") Long id, @Param("updatedBy") String updatedBy);
}
