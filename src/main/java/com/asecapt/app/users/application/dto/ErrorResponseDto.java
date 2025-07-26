package com.asecapt.app.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for error response information")
public class ErrorResponseDto {
    @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type", example = "Bad Request")
    private String error;
    
    @Schema(description = "Error message", example = "Validation failed")
    private String message;
    
    @Schema(description = "Request path", example = "/api/v1/users")
    private String path;
    
    @Schema(description = "Stack trace for debugging", accessMode = Schema.AccessMode.READ_ONLY)
    private String stackTrace;
    
    @Schema(description = "List of validation errors")
    private List<ValidationError> validationErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Validation error details")
    public static class ValidationError {
        @Schema(description = "Field name with error", example = "firstName")
        private String field;
        
        @Schema(description = "Validation error message", example = "First name is required")
        private String message;
    }
} 