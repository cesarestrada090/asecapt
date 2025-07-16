package com.fitech.app.memberships.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "membership_id", nullable = false)
    private Long membershipId;

    @Column(name = "service_contract_id")
    private Integer serviceContractId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "EUR";

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // Payment status: COMPLETED, REJECTED, PENDING

    @Column(name = "collection_status", length = 30)
    private String collectionStatus; // Trainer collection status

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt; // When trainer collected the payment

    @Column(name = "collection_requested_at")
    private LocalDateTime collectionRequestedAt; // When trainer requested collection

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    // Payment status constants (client payment)
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PENDING = "PENDING";

    // Collection status constants (trainer collection)
    public static final String COLLECTION_PENDING_CLIENT_APPROVAL = "PENDING_CLIENT_APPROVAL";
    public static final String COLLECTION_AVAILABLE = "AVAILABLE_FOR_COLLECTION";
    public static final String COLLECTION_PROCESSING = "PROCESSING_COLLECTION";
    public static final String COLLECTION_COLLECTED = "COLLECTED";
    public static final String COLLECTION_CANCELLING = "CANCELLING";
    public static final String COLLECTION_OBSERVED = "OBSERVED";

    // Payment method constants
    public static final String METHOD_CREDIT_CARD = "CREDIT_CARD";
    public static final String METHOD_DEBIT_CARD = "DEBIT_CARD";
    public static final String METHOD_PAYPAL = "PAYPAL";
    public static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    public static final String METHOD_STRIPE = "STRIPE";
    public static final String METHOD_CONTRACT_PAYMENT = "CONTRACT_PAYMENT";
    public static final String METHOD_OTHER = "OTHER";

    // Helper methods
    public boolean isCollected() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isObserved() {
        return STATUS_REJECTED.equals(status);
    }

    public boolean isCancelled() {
        return STATUS_REJECTED.equals(status);
    }

    public void markAsCollected(String transactionId) {
        this.status = STATUS_COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsObserved(String reason) {
        this.status = STATUS_REJECTED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsCancelled(String reason) {
        this.status = STATUS_REJECTED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsPending() {
        this.status = STATUS_PENDING;
        this.processedAt = null;
        this.failureReason = null;
    }

    // Helper methods for payment status
    public boolean isPaymentCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isPaymentPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isPaymentRejected() {
        return STATUS_REJECTED.equals(status);
    }

    // Helper methods for collection status
    public boolean isPendingClientApproval() {
        return COLLECTION_PENDING_CLIENT_APPROVAL.equals(collectionStatus);
    }

    public boolean isAvailableForCollection() {
        return COLLECTION_AVAILABLE.equals(collectionStatus);
    }

    public boolean isCollectedByTrainer() {
        return COLLECTION_COLLECTED.equals(collectionStatus);
    }

    public boolean isCancelling() {
        return COLLECTION_CANCELLING.equals(collectionStatus);
    }

    public boolean isObservedForCollection() {
        return COLLECTION_OBSERVED.equals(collectionStatus);
    }

    public boolean isProcessingCollection() {
        return COLLECTION_PROCESSING.equals(collectionStatus);
    }

    // Payment status methods
    public void markPaymentAsCompleted(String transactionId) {
        this.status = STATUS_COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
        // When payment is completed, it's pending client approval by default
        this.collectionStatus = COLLECTION_PENDING_CLIENT_APPROVAL;
    }

    public void markPaymentAsRejected(String reason) {
        this.status = STATUS_REJECTED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
        this.collectionStatus = null; // No collection status if payment failed
    }

    public void markPaymentAsPending() {
        this.status = STATUS_PENDING;
        this.processedAt = null;
        this.failureReason = null;
        this.collectionStatus = null; // No collection status if payment not completed
    }

    // Collection status methods
    public void approveByClient() {
        // Client approves that service was completed correctly
        this.collectionStatus = COLLECTION_AVAILABLE;
    }

    public void markAsCollectedByTrainer() {
        this.collectionStatus = COLLECTION_COLLECTED;
        this.collectedAt = LocalDateTime.now();
    }

    public void markAsObservedForCollection(String reason) {
        this.collectionStatus = COLLECTION_OBSERVED;
        this.failureReason = reason;
    }

    public void markAsCancelling(String reason) {
        this.collectionStatus = COLLECTION_CANCELLING;
        this.failureReason = reason;
    }

    public void markAsAvailableForCollection() {
        this.collectionStatus = COLLECTION_AVAILABLE;
    }

    public void markAsProcessingCollection() {
        this.collectionStatus = COLLECTION_PROCESSING;
        this.collectionRequestedAt = LocalDateTime.now();
    }
} 