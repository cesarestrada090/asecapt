package com.asecapt.app.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for user login requests")
public class LoginRequestDto {
    @NotBlank(message = "Username is required")
    @Schema(description = "Username for authentication", example = "usuario123")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password for authentication", example = "password123")
    private String password;
} 