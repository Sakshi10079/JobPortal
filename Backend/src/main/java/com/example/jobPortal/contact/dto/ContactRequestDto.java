package com.example.jobPortal.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ContactRequestDto(

        @NotBlank(message = "This field is required")
        @Email
        String email,

        @NotBlank(message = "This field is required")
        @Size(min=5, max=200, message = "Message must be between 5 and 200 characters")
        String message,

        @NotBlank(message = "This field is required")
        @Size(min=2, max=30, message = "Name must be between 2 and 30 characters")
        String name,

        @NotBlank(message = "This field is required")
        @Size(min=5, max=150, message = "Subject must be between 5 and 150 characters")
        String subject,

        @NotBlank(message = "This field is required")
        @Pattern(regexp= "Job Seeker|Employer|Other", message = "User type must be Job Seeker, Employer or Other")
        String userType
) implements Serializable {
}
