package com.fitech.app.trainers.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Data
@Schema(description = "DTO for creating service contracts between trainers and clients")
public class CreateContractDto {
    
    @NotNull(message = "Service ID es requerido")
    @Schema(description = "ID of the service to contract", example = "1", required = true)
    private Integer serviceId;
    
    @NotNull(message = "Client ID es requerido")
    @Schema(description = "ID of the client contracting the service", example = "123", required = true)
    private Integer clientId;
    
    @Schema(description = "Start date of the contract", example = "2024-01-15")
    private LocalDate startDate;
    
    @Schema(description = "Additional notes for the contract", example = "Cliente requiere sesiones matutinas")
    private String notes;
    
    @NotNull(message = "Debe aceptar los términos y condiciones")
    @Schema(description = "Whether terms and conditions are accepted", example = "true", required = true)
    private Boolean termsAccepted;
} 