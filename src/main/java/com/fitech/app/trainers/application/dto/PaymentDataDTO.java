package com.fitech.app.trainers.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for individual payment transaction data")
public class PaymentDataDTO {
    @Schema(description = "Payment ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID who made the payment", example = "123")
    private Integer userId;
    
    @Schema(description = "Client name who made the payment", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Service name that was paid for", example = "Entrenamiento Personal")
    private String serviceName;
    
    @Schema(description = "Total payment amount", example = "300.00")
    private BigDecimal totalAmount;
    
    @Schema(description = "Platform commission amount", example = "15.00")
    private BigDecimal platformCommission;
    
    @Schema(description = "Trainer earnings after commission", example = "285.00")
    private BigDecimal trainerEarnings;
    
    @Schema(description = "Payment date and time")
    private LocalDateTime paymentDate;
    
    @Schema(description = "Payment status", example = "COMPLETED", allowableValues = {"PENDING", "COMPLETED", "FAILED", "REFUNDED"})
    private String paymentStatus;
    
    @Schema(description = "Payment method used", example = "CREDIT_CARD", allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "DIGITAL_WALLET"})
    private String paymentMethod;
    
    @Schema(description = "Transaction ID from payment processor", example = "TXN123456789")
    private String transactionId;
    
    @Schema(description = "Date when collection was requested")
    private LocalDateTime collectionRequestedAt;

    public PaymentDataDTO() {}

    public PaymentDataDTO(Long id, Integer userId, String clientName, String serviceName,
                         BigDecimal totalAmount, BigDecimal platformCommission, BigDecimal trainerEarnings,
                         LocalDateTime paymentDate, String paymentStatus, String paymentMethod, String transactionId,
                         LocalDateTime collectionRequestedAt) {
        this.id = id;
        this.userId = userId;
        this.clientName = clientName;
        this.serviceName = serviceName;
        this.totalAmount = totalAmount;
        this.platformCommission = platformCommission;
        this.trainerEarnings = trainerEarnings;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.collectionRequestedAt = collectionRequestedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPlatformCommission() { return platformCommission; }
    public void setPlatformCommission(BigDecimal platformCommission) { this.platformCommission = platformCommission; }

    public BigDecimal getTrainerEarnings() { return trainerEarnings; }
    public void setTrainerEarnings(BigDecimal trainerEarnings) { this.trainerEarnings = trainerEarnings; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getCollectionRequestedAt() { return collectionRequestedAt; }
    public void setCollectionRequestedAt(LocalDateTime collectionRequestedAt) { this.collectionRequestedAt = collectionRequestedAt; }
} 