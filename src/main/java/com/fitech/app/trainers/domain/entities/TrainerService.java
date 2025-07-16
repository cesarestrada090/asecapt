package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "trainer_services")
@ToString
public class TrainerService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trainer_id", nullable = false)
    private Integer trainerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "price_per_session", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSession;

    @Column(name = "platform_commission_rate", precision = 5, scale = 2)
    private BigDecimal platformCommissionRate = new BigDecimal("5.00");

    @Column(name = "platform_commission_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformCommissionAmount;

    @Column(name = "trainer_earnings", nullable = false, precision = 10, scale = 2)
    private BigDecimal trainerEarnings;

    @Column(name = "is_in_person")
    private Boolean isInPerson = true;

    @Column(name = "transport_included")
    private Boolean transportIncluded = false;

    @Column(name = "transport_cost_per_session", precision = 10, scale = 2)
    private BigDecimal transportCostPerSession = BigDecimal.ZERO;

    @Column(name = "enrolled_users_count")
    private Integer enrolledUsersCount = 0;

    @Column(length = 100)
    private String country = "Perú";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceDistrict> districts = new ArrayList<>();

    // Método de conveniencia para obtener el nombre del servicio
    public String getName() {
        return serviceType != null ? serviceType.getName() : null;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        calculateFinancials();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateFinancials();
    }

    /**
     * Calcula automáticamente los valores financieros basados en el precio total
     */
    private void calculateFinancials() {
        if (totalPrice != null) {
            // Calcular comisión de la plataforma
            this.platformCommissionAmount = totalPrice
                .multiply(platformCommissionRate)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            
            // Calcular ganancias del trainer
            this.trainerEarnings = totalPrice.subtract(platformCommissionAmount);
            
            // El precio por sesión ahora es igual al precio total
            this.pricePerSession = totalPrice;
        }
    }

    public void addDistrict(ServiceDistrict district) {
        districts.add(district);
        district.setService(this);
    }

    public void removeDistrict(ServiceDistrict district) {
        districts.remove(district);
        district.setService(null);
    }
} 