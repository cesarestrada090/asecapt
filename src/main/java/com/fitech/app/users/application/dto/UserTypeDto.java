package com.fitech.app.users.application.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for user type categories")
public class UserTypeDto {
    @Schema(description = "User type ID", example = "1")
    private Integer id;
    
    @Schema(description = "User type name", example = "CLIENT", allowableValues = {"CLIENT", "TRAINER"})
    private String name;
} 