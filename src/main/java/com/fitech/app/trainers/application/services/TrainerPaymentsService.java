package com.fitech.app.trainers.application.services;

import com.fitech.app.trainers.application.dto.PaymentSummaryDTO;
import com.fitech.app.trainers.application.dto.PaymentsResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TrainerPaymentsService {
    
    /**
     * Get trainer payments with pagination and filters
     */
    PaymentsResponseDTO getTrainerPayments(Integer trainerId, Pageable pageable, 
                                         String status, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get payment summary for trainer
     */
    PaymentSummaryDTO getPaymentSummary(Integer trainerId);
    
    /**
     * Export trainer payments to CSV
     */
    byte[] exportPayments(Integer trainerId, LocalDate startDate, LocalDate endDate);

    /**
     * Collect payment (mark as collected by trainer)
     */
    void collectPayment(Long paymentId);
} 