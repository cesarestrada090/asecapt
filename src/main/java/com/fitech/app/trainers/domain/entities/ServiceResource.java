package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "client_resources")
@ToString
public class ServiceResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_id", nullable = true)
    private Integer serviceId;

    @Column(name = "client_id", nullable = true)
    private Integer clientId;

    @Column(name = "trainer_id", nullable = true)
    private Integer trainerId;

    @Column(name = "resource_name", nullable = false, length = 200)
    private String resourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "resource_objective", length = 500)
    private String resourceObjective;

    @Column(name = "resource_details", columnDefinition = "TEXT")
    private String resourceDetails;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "trainer_notes", columnDefinition = "TEXT")
    private String trainerNotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con el servicio
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private TrainerService service;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ResourceType {
        DIETA("Dieta"),
        RUTINA("Rutina");

        private final String displayName;

        ResourceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
} 