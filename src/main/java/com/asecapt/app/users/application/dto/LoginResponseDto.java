package com.asecapt.app.users.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for authentication response containing JWT token and user information")
public class LoginResponseDto {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Authenticated user information")
    private UserResponseDto user;
} 