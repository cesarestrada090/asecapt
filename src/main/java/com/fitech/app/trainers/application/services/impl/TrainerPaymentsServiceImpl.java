package com.fitech.app.trainers.application.services.impl;

import com.fitech.app.memberships.domain.entities.MembershipPayment;
import com.fitech.app.memberships.infrastructure.repositories.MembershipPaymentRepository;
import com.fitech.app.trainers.application.dto.PaymentDataDTO;
import com.fitech.app.trainers.application.dto.PaymentSummaryDTO;
import com.fitech.app.trainers.application.dto.PaymentsResponseDTO;
import com.fitech.app.trainers.application.services.TrainerPaymentsService;
import com.fitech.app.trainers.infrastructure.repositories.TrainerPaymentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerPaymentsServiceImpl implements TrainerPaymentsService {

    @Autowired
    private TrainerPaymentsRepository trainerPaymentsRepository;

    @Autowired
    private MembershipPaymentRepository membershipPaymentRepository;

    private static final BigDecimal PLATFORM_COMMISSION_RATE = BigDecimal.valueOf(0.05); // 5%

    @Override
    public PaymentsResponseDTO getTrainerPayments(Integer trainerId, Pageable pageable, 
                                                String status, LocalDate startDate, LocalDate endDate) {
        
        // Convert dates to LocalDateTime for database queries
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Get paginated payments for trainer
        Page<Object[]> paymentsPage = trainerPaymentsRepository.findTrainerPaymentsWithClientInfo(
            trainerId, status, startDateTime, endDateTime, pageable);
        
        // Convert to DTOs
        List<PaymentDataDTO> paymentDTOs = paymentsPage.getContent().stream()
            .map(this::mapToPaymentDataDTO)
            .collect(Collectors.toList());
        
        // Get summary data
        PaymentSummaryDTO summary = getPaymentSummary(trainerId);
        
        return new PaymentsResponseDTO(
            paymentDTOs,
            summary,
            paymentsPage.getTotalPages(),
            paymentsPage.getNumber(),
            paymentsPage.getTotalElements()
        );
    }

    @Override
    public PaymentSummaryDTO getPaymentSummary(Integer trainerId) {
        // Get summary data from repository
        Object[] summaryData = trainerPaymentsRepository.getTrainerPaymentSummary(trainerId);
        
        BigDecimal collectedToDate = summaryData[0] != null ? (BigDecimal) summaryData[0] : BigDecimal.ZERO;
        BigDecimal pendingCollection = summaryData[1] != null ? (BigDecimal) summaryData[1] : BigDecimal.ZERO;
        BigDecimal availableForCollection = summaryData[2] != null ? (BigDecimal) summaryData[2] : BigDecimal.ZERO;
        
        return new PaymentSummaryDTO(
            collectedToDate,
            pendingCollection,
            availableForCollection
        );
    }

    @Override
    public byte[] exportPayments(Integer trainerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        List<Object[]> payments = trainerPaymentsRepository.findTrainerPaymentsForExport(
            trainerId, startDateTime, endDateTime);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("Fecha,Cliente,Servicio,Monto Total,Comisión Plataforma,Tus Ganancias,Estado,Método de Pago,ID Transacción");
            
            // CSV Data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Object[] payment : payments) {
                PaymentDataDTO dto = mapToPaymentDataDTO(payment);
                writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%s,%s,%s%n",
                    dto.getPaymentDate().format(formatter),
                    escapeCsvValue(dto.getClientName()),
                    escapeCsvValue(dto.getServiceName()),
                    dto.getTotalAmount(),
                    dto.getPlatformCommission(),
                    dto.getTrainerEarnings(),
                    dto.getPaymentStatus(),
                    dto.getPaymentMethod(),
                    dto.getTransactionId() != null ? dto.getTransactionId() : ""
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV export", e);
        }
    }

    @Override
    public void collectPayment(Long paymentId) {
        // Find the payment
        MembershipPayment payment = membershipPaymentRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        
        // Verify payment can be collected
        if (!payment.isAvailableForCollection()) {
            throw new IllegalStateException("El pago no está disponible para cobrar. Estado actual: " + payment.getCollectionStatus());
        }
        
        // Mark as processing collection (trainer requested, admin needs to process)
        payment.markAsProcessingCollection();
        
        // Save the updated payment
        membershipPaymentRepository.save(payment);
    }

    private PaymentDataDTO mapToPaymentDataDTO(Object[] row) {
        // Map database result to DTO
        // Expected columns: payment_id, user_id, client_name, service_name, amount, 
        // payment_date, payment_status, collection_status, payment_method, transaction_id, collected_at, collection_requested_at
        
        Long id = ((Number) row[0]).longValue();
        Integer userId = ((Number) row[1]).intValue();
        String clientName = (String) row[2];
        String serviceName = (String) row[3];
        BigDecimal totalAmount = (BigDecimal) row[4];
        
        // Convert Timestamp to LocalDateTime properly
        LocalDateTime paymentDate = null;
        if (row[5] != null) {
            if (row[5] instanceof Timestamp) {
                paymentDate = ((Timestamp) row[5]).toLocalDateTime();
            } else if (row[5] instanceof LocalDateTime) {
                paymentDate = (LocalDateTime) row[5];
            }
        }
        
        String paymentStatus = (String) row[6];
        String collectionStatus = (String) row[7];
        String paymentMethod = (String) row[8];
        String transactionId = (String) row[9];
        
        // Convert collected_at timestamp
        LocalDateTime collectedAt = null;
        if (row[10] != null) {
            if (row[10] instanceof Timestamp) {
                collectedAt = ((Timestamp) row[10]).toLocalDateTime();
            } else if (row[10] instanceof LocalDateTime) {
                collectedAt = (LocalDateTime) row[10];
            }
        }

        // Convert collection_requested_at timestamp
        LocalDateTime collectionRequestedAt = null;
        if (row[11] != null) {
            if (row[11] instanceof Timestamp) {
                collectionRequestedAt = ((Timestamp) row[11]).toLocalDateTime();
            } else if (row[11] instanceof LocalDateTime) {
                collectionRequestedAt = (LocalDateTime) row[11];
            }
        }
        
        // Calculate commission and earnings
        BigDecimal platformCommission = totalAmount.multiply(PLATFORM_COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal trainerEarnings = totalAmount.subtract(platformCommission);
        
        return new PaymentDataDTO(
            id, userId, clientName, serviceName,
            totalAmount, platformCommission, trainerEarnings,
            paymentDate, collectionStatus != null ? collectionStatus : paymentStatus, 
            paymentMethod, transactionId, collectionRequestedAt
        );
    }

    private String escapeCsvValue(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
} 