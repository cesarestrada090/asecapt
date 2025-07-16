package com.fitech.app.admin.application.services.impl;

import com.fitech.app.admin.application.dto.AdminPaymentDataDTO;
import com.fitech.app.admin.application.dto.AdminPaymentSummaryDTO;
import com.fitech.app.admin.application.dto.AdminPaymentsResponseDTO;
import com.fitech.app.admin.application.services.AdminPaymentsService;
import com.fitech.app.memberships.domain.entities.MembershipPayment;
import com.fitech.app.memberships.infrastructure.repositories.MembershipPaymentRepository;
import com.fitech.app.users.domain.entities.User;
import com.fitech.app.users.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminPaymentsServiceImpl implements AdminPaymentsService {

    @Autowired
    private MembershipPaymentRepository membershipPaymentRepository;

    @Autowired
    private UserRepository userRepository;

    private static final BigDecimal PLATFORM_COMMISSION_RATE = BigDecimal.valueOf(0.05); // 5%

    @Override
    public AdminPaymentsResponseDTO getAllPayments(Pageable pageable, String status, String paymentMethod, 
                                                  LocalDate startDate, LocalDate endDate) {
        
        // Convert dates to LocalDateTime for database queries
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Get all payments with filters
        Page<MembershipPayment> paymentsPage;
        
        if (status != null || paymentMethod != null || startDateTime != null || endDateTime != null) {
            // Apply filters
            paymentsPage = membershipPaymentRepository.findAll(pageable)
                .map(payment -> {
                    // Apply manual filtering since we don't have specific query methods
                    boolean matches = true;
                    
                    if (status != null && !payment.getStatus().equalsIgnoreCase(status)) {
                        matches = false;
                    }
                    
                    if (paymentMethod != null && !payment.getPaymentMethod().equalsIgnoreCase(paymentMethod)) {
                        matches = false;
                    }
                    
                    if (startDateTime != null && payment.getCreatedAt().isBefore(startDateTime)) {
                        matches = false;
                    }
                    
                    if (endDateTime != null && payment.getCreatedAt().isAfter(endDateTime)) {
                        matches = false;
                    }
                    
                    return matches ? payment : null;
                })
                .map(payment -> payment); // This is a simplified approach - in production, you'd want proper query methods
        } else {
            paymentsPage = membershipPaymentRepository.findAll(pageable);
        }
        
        // Convert to DTOs
        List<AdminPaymentDataDTO> paymentDTOs = paymentsPage.getContent().stream()
            .map(this::mapToAdminPaymentDataDTO)
            .collect(Collectors.toList());
        
        // Get summary data
        AdminPaymentSummaryDTO summary = getPaymentSummary();
        
        return new AdminPaymentsResponseDTO(
            paymentDTOs,
            summary,
            paymentsPage.getTotalPages(),
            paymentsPage.getNumber(),
            paymentsPage.getTotalElements()
        );
    }

    @Override
    public AdminPaymentSummaryDTO getPaymentSummary() {
        // Calculate total revenue (all completed and collected payments)
        List<MembershipPayment> allPayments = membershipPaymentRepository.findAll();
        
        BigDecimal totalRevenue = allPayments.stream()
            .filter(p -> MembershipPayment.STATUS_COMPLETED.equals(p.getStatus()) || 
                        MembershipPayment.COLLECTION_COLLECTED.equals(p.getCollectionStatus()))
            .map(MembershipPayment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Count pending payments
        Integer pendingPayments = (int) allPayments.stream()
            .filter(p -> MembershipPayment.STATUS_PENDING.equals(p.getStatus()))
            .count();
        
        // Calculate completed revenue (completed but not yet collected by trainers)
        BigDecimal completedRevenue = allPayments.stream()
            .filter(p -> MembershipPayment.STATUS_COMPLETED.equals(p.getStatus()) && 
                        !MembershipPayment.COLLECTION_COLLECTED.equals(p.getCollectionStatus()))
            .map(MembershipPayment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total commission (5% of all completed payments)
        BigDecimal totalCommission = totalRevenue.multiply(PLATFORM_COMMISSION_RATE);
        
        return new AdminPaymentSummaryDTO(
            totalRevenue,
            pendingPayments,
            completedRevenue,
            totalCommission
        );
    }

    @Override
    public void markPaymentAsPaidToTrainer(Long paymentId) {
        Optional<MembershipPayment> paymentOpt = membershipPaymentRepository.findById(paymentId);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }
        
        MembershipPayment payment = paymentOpt.get();
        
        if (!MembershipPayment.STATUS_COMPLETED.equals(payment.getStatus())) {
            throw new IllegalStateException("Payment must be completed before marking as paid to trainer");
        }
        
        // Mark as collected and set collection date
        payment.setCollectionStatus(MembershipPayment.COLLECTION_COLLECTED);
        payment.setCollectedAt(LocalDateTime.now());
        
        membershipPaymentRepository.save(payment);
    }

    @Override
    public void cancelPayment(Long paymentId, String reason) {
        Optional<MembershipPayment> paymentOpt = membershipPaymentRepository.findById(paymentId);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentId);
        }
        
        MembershipPayment payment = paymentOpt.get();
        
        if (MembershipPayment.STATUS_COMPLETED.equals(payment.getStatus()) && 
            MembershipPayment.COLLECTION_COLLECTED.equals(payment.getCollectionStatus())) {
            throw new IllegalStateException("Cannot cancel a payment that has already been collected");
        }
        
        // Cancel the payment
        payment.setStatus(MembershipPayment.STATUS_REJECTED);
        payment.setCollectionStatus(MembershipPayment.COLLECTION_CANCELLING);
        if (reason != null) {
            payment.setFailureReason(reason);
        }
        
        membershipPaymentRepository.save(payment);
    }

    @Override
    public byte[] exportPayments(String status, String paymentMethod, LocalDate startDate, LocalDate endDate) {
        // Get all payments with filters (simplified approach)
        List<MembershipPayment> payments = membershipPaymentRepository.findAll().stream()
            .filter(payment -> {
                boolean matches = true;
                
                if (status != null && !payment.getStatus().equalsIgnoreCase(status)) {
                    matches = false;
                }
                
                if (paymentMethod != null && !payment.getPaymentMethod().equalsIgnoreCase(paymentMethod)) {
                    matches = false;
                }
                
                if (startDate != null && payment.getCreatedAt().toLocalDate().isBefore(startDate)) {
                    matches = false;
                }
                
                if (endDate != null && payment.getCreatedAt().toLocalDate().isAfter(endDate)) {
                    matches = false;
                }
                
                return matches;
            })
            .collect(Collectors.toList());
        
        // Generate CSV
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream)) {
            
            // CSV Header
            writer.println("ID,Client Name,Client Email,Trainer Name,Amount,Commission,Status,Payment Method,Transaction ID,Created At,Processed At,Description");
            
            // CSV Data
            for (MembershipPayment payment : payments) {
                AdminPaymentDataDTO dto = mapToAdminPaymentDataDTO(payment);
                writer.printf("%d,%s,%s,%s,%.2f,%.2f,%s,%s,%s,%s,%s,%s%n",
                    dto.getId(),
                    escapeCSV(dto.getClientName()),
                    escapeCSV(dto.getClientEmail()),
                    escapeCSV(dto.getTrainerName()),
                    dto.getAmount(),
                    dto.getCommission(),
                    dto.getStatus(),
                    dto.getPaymentMethod(),
                    dto.getTransactionId(),
                    dto.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    dto.getProcessedAt() != null ? dto.getProcessedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                    escapeCSV(dto.getDescription())
                );
            }
            
            writer.flush();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV export", e);
        }
    }

    private AdminPaymentDataDTO mapToAdminPaymentDataDTO(MembershipPayment payment) {
        // Get user information
        Optional<User> userOpt = userRepository.findById(payment.getUserId());
        String clientName = userOpt.map(u -> {
            if (u.getPerson() != null) {
                return u.getPerson().getFirstName() + " " + u.getPerson().getLastName();
            } else {
                return u.getUsername();
            }
        }).orElse("Unknown User");
        String clientEmail = userOpt.map(u -> u.getPerson() != null ? u.getPerson().getEmail() : u.getUsername()).orElse("unknown@email.com");
        
        // For trainer name, we'll need to implement logic based on service contracts
        // For now, using a placeholder
        String trainerName = "Trainer Name"; // TODO: Implement trainer lookup logic
        
        // Calculate commission
        BigDecimal commission = payment.getAmount().multiply(PLATFORM_COMMISSION_RATE);
        
        // Determine if paid to trainer
        boolean paidToTrainer = MembershipPayment.COLLECTION_COLLECTED.equals(payment.getCollectionStatus());
        
        return new AdminPaymentDataDTO(
            payment.getId(),
            clientName,
            clientEmail,
            trainerName,
            payment.getAmount(),
            payment.getStatus(),
            payment.getPaymentMethod(),
            payment.getTransactionId(),
            payment.getCreatedAt(),
            payment.getProcessedAt(),
            payment.getCollectedAt(),
            paidToTrainer,
            commission,
            payment.getDescription() != null ? payment.getDescription() : "Payment description",
            "Service Name", // TODO: Implement service name lookup
            payment.getCollectionStatus()
        );
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
} 