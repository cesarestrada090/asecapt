package com.fitech.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metric_type_uom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricTypeUOM {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metric_type_id", nullable = false)
    private MetricType metricType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_of_measure_id", nullable = false)
    private UnitOfMeasure unitOfMeasure;
    
    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
} 