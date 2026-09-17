package com.example.jobPortal.job.controller;

import com.example.jobPortal.dto.JobApplicationDto;
import com.example.jobPortal.dto.JobDto;
import com.example.jobPortal.dto.UpdateJobApplicationDto;
import com.example.jobPortal.job.repository.JobRepository;
import com.example.jobPortal.job.service.IJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {
    private final IJobService jobService;
    private final JobRepository jobRepository;

    @GetMapping(path="/employer", version="1.0")
    public ResponseEntity<List<JobDto>> getEmployerJobs(Authentication authentication) {
       String employerEmail = authentication.getName();
       List<JobDto> jobs = jobService.getEmployerJobs(employerEmail);
       return  ResponseEntity.ok().body(jobs);
    }

    @PatchMapping(path = "/{jobId}/status/employer", version = "1.0")
    public ResponseEntity<?> updateJobStatus(Authentication authentication,
                                             @PathVariable Long jobId,
                                             @RequestBody Map<String, String> requestBody) {
        String employerEmail = authentication.getName();
        String status = requestBody.get("status");

        if(status == null || status.trim().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","status is required"));
        }
        JobDto updatedJob = jobService.updateJobStatus(jobId, status.toUpperCase(), employerEmail);
        return ResponseEntity.ok().body(updatedJob);
    }

    @PostMapping(path = "/employer",version = "1.0")
    public ResponseEntity<JobDto> createJob(@RequestBody @Valid JobDto jobDto, Authentication authentication) {
        String employerEmail = authentication.getName();
        JobDto createdJob = jobService.createJob(jobDto, employerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @GetMapping(value="/applications/{jobId}/employer",version = "1.0")
    public ResponseEntity<List<JobApplicationDto>> getApplicationsByJobForEmployer(@PathVariable Long jobId){
        List<JobApplicationDto> applications = jobService.getApplicationsByJobForEmployer(jobId);
        return ResponseEntity.ok().body(applications);
    }

    @PatchMapping(value="/applications/employer",version = "1.0")
    public ResponseEntity<String> updateJobApplication(@RequestBody @Valid UpdateJobApplicationDto
                                                                   updateJobApplicationDto){
        boolean isUpdated = jobService.updateJobApplication(updateJobApplicationDto);
        if(!isUpdated){
            return ResponseEntity.badRequest().body("Failed to update application status");
        }
        return ResponseEntity.ok().body("Application status updated successfully");
    }
}
