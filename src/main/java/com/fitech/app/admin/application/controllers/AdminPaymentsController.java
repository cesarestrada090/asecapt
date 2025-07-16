package com.fitech.app.admin.application.controllers;

import com.fitech.app.admin.application.dto.AdminPaymentDataDTO;
import com.fitech.app.admin.application.dto.AdminPaymentSummaryDTO;
import com.fitech.app.admin.application.dto.AdminPaymentsResponseDTO;
import com.fitech.app.admin.application.services.AdminPaymentsService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/v1/app/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin Payments", description = "Payment management for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminPaymentsController {

    @Autowired
    private AdminPaymentsService adminPaymentsService;

    @Operation(summary = "Get all platform payments", description = "Retrieve all payments with pagination and filters for admin view")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminPaymentsResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/payments")
    public ResponseEntity<AdminPaymentsResponseDTO> getAllPayments(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Filter by payment status", example = "COMPLETED")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Filter by payment method", example = "CREDIT_CARD")
            @RequestParam(required = false) String paymentMethod,
            
            @Parameter(description = "Filter from date (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "Filter to date (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) String endDate) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            
            AdminPaymentsResponseDTO response = adminPaymentsService.getAllPayments(
                pageable, status, paymentMethod, start, end);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get platform payment summary", description = "Get aggregated payment statistics for admin dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminPaymentSummaryDTO.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/payments/summary")
    public ResponseEntity<AdminPaymentSummaryDTO> getPaymentSummary() {
        try {
            AdminPaymentSummaryDTO summary = adminPaymentsService.getPaymentSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Mark payment as paid to trainer", description = "Admin action to mark that payment has been transferred to trainer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment marked as paid successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment ID or payment cannot be marked as paid"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/payments/{paymentId}/mark-paid")
    public ResponseEntity<?> markPaymentAsPaidToTrainer(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long paymentId) {
        try {
            adminPaymentsService.markPaymentAsPaidToTrainer(paymentId);
            return ResponseEntity.ok().body(Map.of("message", "Pago marcado como abonado al trainer exitosamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @Operation(summary = "Cancel payment", description = "Admin action to cancel a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment ID or payment cannot be cancelled"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long paymentId,
            
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String reason = requestBody != null ? requestBody.get("reason") : null;
            adminPaymentsService.cancelPayment(paymentId, reason);
            return ResponseEntity.ok().body(Map.of("message", "Pago cancelado exitosamente"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @Operation(summary = "Export payments to CSV", description = "Export all payments matching filters to CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/payments/export")
    public ResponseEntity<byte[]> exportPayments(
            @Parameter(description = "Filter by payment status", example = "COMPLETED")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Filter by payment method", example = "CREDIT_CARD")
            @RequestParam(required = false) String paymentMethod,
            
            @Parameter(description = "Filter from date (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "Filter to date (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) String endDate) {
        
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            
            byte[] csvData = adminPaymentsService.exportPayments(status, paymentMethod, start, end);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=platform_payments.csv")
                    .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 