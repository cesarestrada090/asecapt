package com.fitech.app.admin.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for admin view of payment transaction data")
public class AdminPaymentDataDTO {
    @Schema(description = "Payment ID", example = "1")
    private Long id;
    
    @Schema(description = "Client name who made the payment", example = "Juan Pérez")
    private String clientName;
    
    @Schema(description = "Client email", example = "juan.perez@example.com")
    private String clientEmail;
    
    @Schema(description = "Trainer name receiving payment", example = "Ana García")
    private String trainerName;
    
    @Schema(description = "Payment amount", example = "150.00")
    private BigDecimal amount;
    
    @Schema(description = "Payment status", example = "COMPLETED", allowableValues = {"PENDING", "COMPLETED", "COLLECTED", "CANCELLED"})
    private String status;
    
    @Schema(description = "Payment method used", example = "CREDIT_CARD", allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER", "STRIPE"})
    private String paymentMethod;
    
    @Schema(description = "Transaction ID", example = "TXN123456789")
    private String transactionId;
    
    @Schema(description = "Payment creation date and time")
    private LocalDateTime createdAt;
    
    @Schema(description = "Payment processing date and time")
    private LocalDateTime processedAt;
    
    @Schema(description = "Date when payment was collected by trainer")
    private LocalDateTime collectedAt;
    
    @Schema(description = "Whether payment has been transferred to trainer", example = "true")
    private boolean paidToTrainer;
    
    @Schema(description = "Platform commission amount", example = "15.00")
    private BigDecimal commission;
    
    @Schema(description = "Payment description", example = "Entrenamiento personal")
    private String description;
    
    @Schema(description = "Service name if applicable", example = "Entrenamiento Personal")
    private String serviceName;
    
    @Schema(description = "Collection status for trainer", example = "COLLECTED")
    private String collectionStatus;

    public AdminPaymentDataDTO() {}

    public AdminPaymentDataDTO(Long id, String clientName, String clientEmail, String trainerName, 
                              BigDecimal amount, String status, String paymentMethod, String transactionId,
                              LocalDateTime createdAt, LocalDateTime processedAt, LocalDateTime collectedAt,
                              boolean paidToTrainer, BigDecimal commission, String description, 
                              String serviceName, String collectionStatus) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.trainerName = trainerName;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.collectedAt = collectedAt;
        this.paidToTrainer = paidToTrainer;
        this.commission = commission;
        this.description = description;
        this.serviceName = serviceName;
        this.collectionStatus = collectionStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }

    public boolean isPaidToTrainer() { return paidToTrainer; }
    public void setPaidToTrainer(boolean paidToTrainer) { this.paidToTrainer = paidToTrainer; }

    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCollectionStatus() { return collectionStatus; }
    public void setCollectionStatus(String collectionStatus) { this.collectionStatus = collectionStatus; }
} 