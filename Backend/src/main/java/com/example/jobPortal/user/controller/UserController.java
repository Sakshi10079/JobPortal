package com.example.jobPortal.user.controller;

import com.example.jobPortal.dto.*;
import com.example.jobPortal.entity.JobApplication;
import com.example.jobPortal.entity.Profile;
import com.example.jobPortal.user.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    //admin APIs
    @GetMapping(path="/search/admin", version = "1.0")
    public ResponseEntity<?> searchUserByEmail(@RequestParam String email){
     Optional<UserDto> userOptional =  userService.searchUserByEmail(email);
     if(userOptional.isEmpty()){
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body(Map.of("message", "User not found with email: "+email));
     }
     return ResponseEntity.ok(userOptional.get());
    }

    @PatchMapping("/{userId}/role/employer/admin")
    public ResponseEntity<?> elevateToEmployer(@PathVariable Long userId){
        UserDto elevatedUser = userService.elevateToEmployer(userId);
        return ResponseEntity.ok(elevatedUser);
    }

    @PatchMapping("/{userId}/company/{companyId}/admin")
    public ResponseEntity<?> assignCompanyToEmployer(@PathVariable Long userId, @PathVariable Long companyId){
        UserDto assignedUser = userService.assignCompanyToEmpoyer(userId, companyId);
        return ResponseEntity.ok(assignedUser);
    }

    //jobseeker APIs--profile related

    @PutMapping(value="/profile/jobseeker",version = "1.0",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDto> createOrUpdateProfile(Authentication authentication
                                               ,@RequestPart ("profile") String profileJson
                                               ,@RequestPart("profilePicture") MultipartFile profilePicture
                                               ,@RequestPart("resume") MultipartFile resume)
                                                throws JsonProcessingException {
        String userEmail = authentication.getName();
       ProfileDto savedProfile = userService.createOrUpdateProfile(userEmail, profileJson, profilePicture, resume);
       return ResponseEntity.ok(savedProfile);
    }

    @GetMapping(value="/profile/jobseeker",version = "1.0")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication){
        String userEmail = authentication.getName();
        ProfileDto profileDto = userService.getProfile(userEmail);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping(value ="/profile/picture/jobseeker", version = "1.0")
    public  ResponseEntity<byte[]> getProfilePicture(Authentication authentication){
        String userEmail = authentication.getName();
        ProfileDto profileDto = userService.getProfilePicture(userEmail);
        byte[] picture = profileDto.profilePicture();
        if(picture == null || picture.length==0){
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.profilePictureType()));
        headers.setContentLength(picture.length);
        return ResponseEntity.ok().headers(headers).body(picture);
    }

    @GetMapping(value="/profile/resume/jobseeker", version = "1.0")
    public ResponseEntity<byte[]> getResume(Authentication authentication){
        String userEmail = authentication.getName();
        ProfileDto profileDto = userService.getResume(userEmail);
        byte[] resume = profileDto.resume();
        if(resume == null || resume.length==0){
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profileDto.resumeType()));
        headers.setContentLength(resume.length);
        headers.setContentDispositionFormData("attachment", profileDto.resumeName());
        return ResponseEntity.ok().headers(headers).body(resume);
    }

    //saved jobs APIs
    @PostMapping(value = "/saved-jobs/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<String> saveJob(@PathVariable Long jobId,Authentication authentication){
        String userEmail = authentication.getName();
        userService.saveJob(userEmail, jobId);
        return ResponseEntity.ok("Job saved successfully");
    }

    @DeleteMapping(value="/saved-jobs/{jobId}/jobseeker", version = "1.0")
    public ResponseEntity<String> unsaveJob(@PathVariable Long jobId,Authentication authentication){
        String userEmail = authentication.getName();
        userService.unsaveJob(userEmail, jobId);
        return ResponseEntity.ok("Job removed from saved jobs");
    }

    @GetMapping(value="/saved-jobs/jobseeker", version = "1.0")
    public ResponseEntity<List<JobDto>> getSavedJobs(Authentication authentication){
        String userEmail = authentication.getName();
        List<JobDto> savedJobDtos = userService.getSavedJobs(userEmail);
        return ResponseEntity.ok(savedJobDtos);
    }

    //applyJob related
    @PostMapping(value="job-applications/jobseeker",version="1.0")
    public ResponseEntity<JobApplicationDto> applyForJob(@RequestBody @Valid ApplyJobRequestDto applyJobRequestDto,
                                                         Authentication authentication){
        String userEmail = authentication.getName();
        JobApplicationDto application = userService.applyForJob(userEmail, applyJobRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @DeleteMapping(value = "job-applications/{jobId}/jobseeker",version = "1.0")
    public ResponseEntity<String> withdrawApplication(@PathVariable Long jobId,Authentication authentication){
        String userEmail = authentication.getName();
        userService.withdrawApplication(userEmail, jobId);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @GetMapping(value="job-applications/jobseeker",version = "1.0")
    public ResponseEntity<List<JobApplicationDto>> getJobSeekerApplications(Authentication authentication){
        String userEmail = authentication.getName();
        List<JobApplicationDto> applications = userService.getJobSeekerApplications(userEmail);
        return ResponseEntity.ok(applications);
    }
}
