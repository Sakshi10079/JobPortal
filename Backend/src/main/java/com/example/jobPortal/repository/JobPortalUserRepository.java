package com.example.jobPortal.repository;

import com.example.jobPortal.entity.JobPortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPortalUserRepository extends JpaRepository<JobPortalUser, Long> {
    Optional<JobPortalUser> readUserByEmailOrMobileNumber(String email, String mobileNumber);
    Optional<JobPortalUser> readUserByEmail(String email);
    Optional<JobPortalUser> findJobPortalUserByEmail(String email);
}
