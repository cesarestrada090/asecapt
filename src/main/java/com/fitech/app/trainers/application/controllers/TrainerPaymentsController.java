package com.fitech.app.trainers.application.controllers;

import com.fitech.app.trainers.application.dto.PaymentSummaryDTO;
import com.fitech.app.trainers.application.dto.PaymentsResponseDTO;
import com.fitech.app.trainers.application.services.TrainerPaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/trainers")
@CrossOrigin(origins = "*")
@Tag(name = "Trainer Payments", description = "Payment management and earnings for trainers")
@SecurityRequirement(name = "bearerAuth")
public class TrainerPaymentsController {

    @Autowired
    private TrainerPaymentsService trainerPaymentsService;

    @GetMapping("/{trainerId}/payments")
    public ResponseEntity<PaymentsResponseDTO> getTrainerPayments(
            @PathVariable Integer trainerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            
            PaymentsResponseDTO response = trainerPaymentsService.getTrainerPayments(
                trainerId, pageable, status, start, end);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{trainerId}/payments/summary")
    public ResponseEntity<PaymentSummaryDTO> getPaymentSummary(@PathVariable Integer trainerId) {
        try {
            PaymentSummaryDTO summary = trainerPaymentsService.getPaymentSummary(trainerId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{trainerId}/payments/export")
    public ResponseEntity<byte[]> exportPayments(
            @PathVariable Integer trainerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            
            byte[] csvData = trainerPaymentsService.exportPayments(trainerId, start, end);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "pagos_trainer_" + trainerId + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/payments/{paymentId}/collect")
    public ResponseEntity<?> collectPayment(@PathVariable Long paymentId) {
        try {
            trainerPaymentsService.collectPayment(paymentId);
            return ResponseEntity.ok().body(Map.of("message", "Pago marcado como cobrado exitosamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }
} 