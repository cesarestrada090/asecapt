package com.fitech.app.users.application.controllers;

import com.fitech.app.users.application.dto.SupportRequestDto;
import com.fitech.app.users.infrastructure.email.EmailService;
import com.fitech.app.users.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/app/support")
@Tag(name = "Support", description = "Customer support and help desk operations")
public class SupportController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send-inquiry")
    public ResponseEntity<?> sendSupportInquiry(
            @Valid @RequestBody SupportRequestDto supportRequest,
            HttpServletRequest request) {
        try {
            log.info("Received support inquiry from: {}", supportRequest.getEmail());
            
            // Intentar obtener información del usuario autenticado si hay un token
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    if (jwtTokenProvider.validateToken(token)) {
                        log.info("Support request from authenticated user: {}", username);
                        // Podrías agregar más información del usuario autenticado aquí si es necesario
                    }
                } catch (Exception e) {
                    log.warn("Could not extract user info from token, proceeding as anonymous: {}", e.getMessage());
                }
            }
            
            // Enviar el email de soporte
            emailService.sendSupportEmail(
                supportRequest.getName(),
                supportRequest.getEmail(),
                supportRequest.getPhone(),
                supportRequest.getType(),
                supportRequest.getSubject(),
                supportRequest.getMessage(),
                supportRequest.getUserType(),
                supportRequest.getUserId()
            );
            
            log.info("Support email sent successfully for: {}", supportRequest.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tu consulta ha sido enviada exitosamente. Te responderemos a la brevedad en el email proporcionado."
            ));
            
        } catch (Exception e) {
            log.error("Error sending support email for: {}", supportRequest.getEmail(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Error al enviar la consulta. Por favor, intenta nuevamente."
            ));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testSupportEmail(@RequestParam String email) {
        try {
            emailService.sendSupportEmail(
                "Usuario de Prueba",
                email,
                "+51 999 999 999",
                "technical",
                "Prueba de email de soporte",
                "Este es un mensaje de prueba para verificar que el sistema de soporte funciona correctamente.",
                "TRAINER",
                1
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Email de prueba de soporte enviado exitosamente a " + email
            ));
        } catch (Exception e) {
            log.error("Error sending test support email to: {}", email, e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al enviar el email de prueba: " + e.getMessage()
            ));
        }
    }
} 