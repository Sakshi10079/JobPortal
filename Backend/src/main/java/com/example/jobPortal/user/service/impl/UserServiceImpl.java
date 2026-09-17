package com.example.jobPortal.user.service.impl;

import com.example.jobPortal.company.entity.Company;
import com.example.jobPortal.company.repository.CompanyRepository;
import com.example.jobPortal.constants.ApplicationConstants;
import com.example.jobPortal.dto.*;
import com.example.jobPortal.entity.*;
import com.example.jobPortal.job.repository.JobRepository;
import com.example.jobPortal.repository.JobApplicationRepository;
import com.example.jobPortal.repository.JobPortalUserRepository;
import com.example.jobPortal.repository.ProfileRepository;
import com.example.jobPortal.repository.RoleRepository;
import com.example.jobPortal.user.service.IUserService;
import com.example.jobPortal.util.ApplicationUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    private final JobPortalUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public Optional<UserDto> searchUserByEmail(String email) {
        return userRepository.findJobPortalUserByEmail(email).map(this::mapToUserDto);
    }

    @Transactional
    @Override
    public UserDto elevateToEmployer(Long userId) { //change role of user to employer
        JobPortalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //check if user is already employer
        if(ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())){
          return mapToUserDto(user);
        }

        //check if user is admin
        if(ApplicationConstants.ROLE_ADMIN.equals(user.getRole().getName())){
            throw new RuntimeException("Admin cannot be elevated to employer");
        }

        Role employerRole = roleRepository.findRoleByName(ApplicationConstants.ROLE_EMPLOYER)
                .orElseThrow(()-> new RuntimeException("ROLE_EMPLOYER not found"));
        user.setRole(employerRole);
        // JobPortalUser updatedUser = userRepository.save(user);
        /**
         * Why it’s unnecessary
         * findById() returns a managed entity
         * You modify it inside a transaction
         * Dirty checking automatically updates it
         */
        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto assignCompanyToEmpoyer(Long id, Long companyId) {
        JobPortalUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(!ApplicationConstants.ROLE_EMPLOYER.equals(user.getRole().getName())){
            throw new RuntimeException("User must be an employer to be assigned a company");
        }
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
        user.setCompany(company);
        return mapToUserDto(user);
    }

    @Transactional
    @Override
    public ProfileDto createOrUpdateProfile(String userEmail, String profileJson, MultipartFile profilePicture, MultipartFile resume) throws JsonProcessingException {
      JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
              .orElseThrow(() -> new RuntimeException("User not found"));
      Profile profile = user.getProfile();
      if(profile==null) {
          profile = new Profile();
          profile.setUser(user);
      }
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileDto profileDto = objectMapper.readValue(profileJson, ProfileDto.class);
        Profile savedProfile = profileRepository.save(mapToProfile(profile,profileDto,profilePicture,resume));
        return mapToProfileDto(savedProfile,false);
    }

    @Override
    public ProfileDto getProfile(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();
        if(profile==null) {
            return null;
        }
        return mapToProfileDto(profile,false);
    }

    @Override
    public ProfileDto getProfilePicture(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();
        if(profile==null) {
            return null;
        }
        return mapToProfileDto(profile,true);
    }

    @Override
    public ProfileDto getResume(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();
        if(profile==null) {
            return null;
        }
        return mapToProfileDto(profile,true);
    }

    @Transactional
    @Override
    public JobDto saveJob(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not found"));
        user.getSavedJobs().add(job);
        return ApplicationUtility.transformJobToDto(job);

    }

    @Transactional
    @Override
    public JobDto unsaveJob(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not found"));
        user.getSavedJobs().remove(job);
        return ApplicationUtility.transformJobToDto(job);
    }

    @Override
    public List<JobDto> getSavedJobs(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        return user.getSavedJobs().stream().map(job -> ApplicationUtility.transformJobToDto(job))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobApplicationDto applyForJob(String userEmail, ApplyJobRequestDto applyJobRequestDto) {

        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        if(jobApplicationRepository.existsByUserIdAndJobId(user.getId(),applyJobRequestDto.jobId())){
            throw new RuntimeException("User already applied for this job");
        }
        Job job = jobRepository.findById(applyJobRequestDto.jobId())
                .orElseThrow(()-> new RuntimeException("Job not found"));
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setUser(user);
        application.setAppliedAt(Instant.now());
        application.setStatus(ApplicationConstants.PENDING);
        application.setCoverLetter(applyJobRequestDto.coverLetter());
        JobApplication saved = jobApplicationRepository.save(application);

        job.setApplicationsCount(job.getApplicationsCount()!=null?job.getApplicationsCount()+1:1);
        return ApplicationUtility.mapToJobApplicationDto(saved);
    }

    @Transactional
    @Override
    public void withdrawApplication(String userEmail, Long jobId) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        if(!jobApplicationRepository.existsByUserIdAndJobId(user.getId(),jobId)){
            throw new RuntimeException("User has not applied for this job");
        }
        jobApplicationRepository.deleteByUserIdAndJobId(user.getId(),jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new RuntimeException("Job not found"));
        if(job.getApplicationsCount()!=null && job.getApplicationsCount()>0){
            job.setApplicationsCount(job.getApplicationsCount()-1);
        }
    }

    @Override
    public List<JobApplicationDto> getJobSeekerApplications(String userEmail) {
        JobPortalUser user = userRepository.findJobPortalUserByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));
        return user.getJobApplications().stream().map(jobApp ->
                        ApplicationUtility.mapToJobApplicationDto(jobApp))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(JobPortalUser user){
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setUserId(user.getId());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setCompanyId(user.getCompany() != null ? user.getCompany().getId() : null);
        dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);
        return dto;
    }

    private Profile mapToProfile(Profile profile, ProfileDto profileDto, MultipartFile profilePicture,
                                 MultipartFile resume){
        //upload text fields
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setLocation(profileDto.location());
        profile.setJobTitle(profileDto.jobTitle());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());

        //handle profile picture
        if(profilePicture!=null && !profilePicture.isEmpty()){
            try{
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureType(profilePicture.getContentType());
            }
            catch(IOException e){
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }

        //handle resume
        if(resume!=null && !resume.isEmpty()) {
            try {
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume", e);
            }
        }
        return profile;
    }

    private ProfileDto mapToProfileDto(Profile profile, boolean includeBinaryData) {
        ProfileDto dto;
        if (includeBinaryData) {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), profile.getProfilePicture(),
                    profile.getProfilePictureName(), profile.getProfilePictureType(), profile.getResume(),
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt()
            );
        } else {
            dto = new ProfileDto(profile.getId(), profile.getUser().getId(),
                    profile.getJobTitle(), profile.getLocation(), profile.getExperienceLevel(),
                    profile.getProfessionalBio(), profile.getPortfolioWebsite(), null,
                    profile.getProfilePictureName(), profile.getProfilePictureType(), null,
                    profile.getResumeName(), profile.getResumeType(), profile.getCreatedAt(), profile.getUpdatedAt());
        }
        return dto;
    }


}
