package com.fitech.app.admin.application.services;

import com.fitech.app.admin.application.dto.AdminPaymentSummaryDTO;
import com.fitech.app.admin.application.dto.AdminPaymentsResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminPaymentsService {
    
    /**
     * Get all platform payments with pagination and filters
     */
    AdminPaymentsResponseDTO getAllPayments(Pageable pageable, String status, String paymentMethod, 
                                          LocalDate startDate, LocalDate endDate);
    
    /**
     * Get platform payment summary for admin dashboard
     */
    AdminPaymentSummaryDTO getPaymentSummary();
    
    /**
     * Mark payment as paid to trainer (admin action)
     */
    void markPaymentAsPaidToTrainer(Long paymentId);

    /**
     * Cancel payment (admin action)
     */
    void cancelPayment(Long paymentId, String reason);
    
    /**
     * Export platform payments to CSV
     */
    byte[] exportPayments(String status, String paymentMethod, LocalDate startDate, LocalDate endDate);
} 