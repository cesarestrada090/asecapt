package com.fitech.app.nutrition.domain.entities;

import com.fitech.app.users.domain.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FoodCategory category;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "serving_size_grams", nullable = false)
    private Integer servingSizeGrams;

    @Column(name = "calories_per_serving", nullable = false, precision = 8, scale = 2)
    private BigDecimal caloriesPerServing;

    @Column(name = "proteins_per_serving", nullable = false, precision = 8, scale = 2)
    private BigDecimal proteinsPerServing;

    @Column(name = "carbohydrates_per_serving", nullable = false, precision = 8, scale = 2)
    private BigDecimal carbohydratesPerServing;

    @Column(name = "fats_per_serving", nullable = false, precision = 8, scale = 2)
    private BigDecimal fatsPerServing;

    @Column(name = "fiber_per_serving", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal fiberPerServing = BigDecimal.ZERO;

    @Column(name = "sugar_per_serving", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal sugarPerServing = BigDecimal.ZERO;

    @Column(name = "sodium_per_serving", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal sodiumPerServing = BigDecimal.ZERO;

    @Column(name = "is_popular", nullable = false)
    @Builder.Default
    private Boolean isPopular = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 