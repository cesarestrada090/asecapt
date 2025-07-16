package com.fitech.app.memberships.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "EUR";

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // JSON string con las características del plan

    @Column(name = "max_contracts")
    private Integer maxContracts; // Máximo número de contratos simultáneos

    @Column(name = "max_resources")
    private Integer maxResources; // Máximo número de recursos (dietas/rutinas)

    @Column(name = "priority_support")
    private Boolean prioritySupport = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BillingCycle {
        MONTHLY,
        QUARTERLY,
        SEMI_ANNUAL,
        ANNUAL,
        ONE_TIME
    }

    // Helper methods
    public boolean isMonthly() {
        return billingCycle == BillingCycle.MONTHLY;
    }

    public boolean isAnnual() {
        return billingCycle == BillingCycle.ANNUAL;
    }

    public BigDecimal getMonthlyPrice() {
        if (isMonthly()) return price;
        return price.divide(BigDecimal.valueOf(durationDays / 30.0), 2, java.math.RoundingMode.HALF_UP);
    }

    public String getFormattedPrice() {
        return String.format("%.2f %s", price, currency);
    }

    public String getDurationDescription() {
        if (durationDays == 30) return "1 mes";
        if (durationDays == 90) return "3 meses";
        if (durationDays == 180) return "6 meses";
        if (durationDays == 365) return "1 año";
        return durationDays + " días";
    }
} 