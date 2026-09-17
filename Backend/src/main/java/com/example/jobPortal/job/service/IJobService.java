package com.example.jobPortal.job.service;

import com.example.jobPortal.dto.JobApplicationDto;
import com.example.jobPortal.dto.JobDto;
import com.example.jobPortal.dto.UpdateJobApplicationDto;
import com.example.jobPortal.entity.Job;

import java.util.List;

public interface IJobService {
    List<JobDto> getEmployerJobs(String employerEmail);
    JobDto updateJobStatus(Long jobId,String status,String employerEmail);
    JobDto createJob(JobDto jobDto, String employerEmail);
    List<JobApplicationDto> getApplicationsByJobForEmployer(Long jobId);
    boolean updateJobApplication(UpdateJobApplicationDto dto);
}
