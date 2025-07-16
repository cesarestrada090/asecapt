package com.fitech.app.trainers.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for grouping client resources by client")
public class ClientResourceGroupDto {
    @Schema(description = "Client ID", example = "123")
    private Integer clientId;
    
    @Schema(description = "Client full name", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "List of resources created for this client")
    private List<ClientResourceResponseDto> resources;
} 