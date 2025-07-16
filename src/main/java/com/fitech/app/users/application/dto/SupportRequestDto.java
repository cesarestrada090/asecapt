package com.fitech.app.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "DTO for customer support requests")
public class SupportRequestDto {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Full name of the person requesting support", example = "Juan Pérez", maxLength = 100, required = true)
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email es inválido")
    @Schema(description = "Email address for support response", example = "juan.perez@email.com", required = true)
    private String email;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Schema(description = "Phone number for contact", example = "+51987654321", maxLength = 20)
    private String phone;
    
    @NotBlank(message = "El tipo de consulta es requerido")
    @Schema(description = "Type of support request", example = "TECHNICAL", allowableValues = {"TECHNICAL", "BILLING", "GENERAL", "ACCOUNT"}, required = true)
    private String type;
    
    @NotBlank(message = "El asunto es requerido")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    @Schema(description = "Subject of the support request", example = "Problema con el pago de membresía", maxLength = 200, required = true)
    private String subject;
    
    @NotBlank(message = "El mensaje es requerido")
    @Size(min = 20, max = 2000, message = "El mensaje debe tener entre 20 y 2000 caracteres")
    @Schema(description = "Detailed message describing the issue", example = "No puedo completar el pago de mi membresía premium...", minLength = 20, maxLength = 2000, required = true)
    private String message;
    
    // Información adicional para el contexto
    @Schema(description = "ID of the user making the request", example = "123")
    private Integer userId;
    
    @Schema(description = "Type of user making the request", example = "CLIENT", allowableValues = {"TRAINER", "CLIENT"})
    private String userType; // TRAINER o CLIENT
} 