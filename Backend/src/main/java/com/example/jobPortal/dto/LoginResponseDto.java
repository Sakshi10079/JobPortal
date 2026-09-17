package com.example.jobPortal.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
