package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Para contratos con trainer
    @Column(name = "trainer_id")
    private Integer trainerId;

    @Column(name = "contract_details", columnDefinition = "TEXT")
    private String contractDetails;

    // Para pagos
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "currency", length = 3)
    private String currency = "EUR";

    @Column(name = "auto_renewal")
    private Boolean autoRenewal = false;

    public enum MembershipType {
        PAYMENT,
        CONTRACT
    }

    public enum MembershipStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED,
        PENDING
    }

    // Helper methods
    public boolean isActive() {
        return status == MembershipStatus.ACTIVE && endDate.isAfter(LocalDate.now());
    }

    public boolean isExpired() {
        return endDate.isBefore(LocalDate.now()) || status == MembershipStatus.EXPIRED;
    }

    public long getDaysUntilExpiry() {
        if (isExpired()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }
} 