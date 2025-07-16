package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "service_contracts")
@ToString
public class ServiceContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "trainer_id", nullable = false)
    private Integer trainerId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false)
    private ContractStatus contractStatus = ContractStatus.ACTIVE;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED;

    @Column(name = "terms_accepted_at")
    private LocalDateTime termsAcceptedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private TrainerService service;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        // Aceptar términos al crear el contrato
        if (termsAcceptedAt == null) {
            termsAcceptedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ContractStatus {
        PENDING,    // Contrato creado, esperando confirmación
        ACTIVE,     // Contrato activo, servicio en curso
        COMPLETED,  // Servicio completado satisfactoriamente
        CANCELLED   // Contrato cancelado
    }

    public enum PaymentStatus {
        PENDING,    // Pago pendiente
        COMPLETED,  // Pago completado
        FAILED,     // Pago fallido
        REFUNDED    // Pago reembolsado
    }

    // Métodos de utilidad
    public boolean isActive() {
        return contractStatus == ContractStatus.ACTIVE;
    }

    public boolean canBeModified() {
        return contractStatus == ContractStatus.PENDING;
    }

    public boolean isCompleted() {
        return contractStatus == ContractStatus.COMPLETED;
    }
} 