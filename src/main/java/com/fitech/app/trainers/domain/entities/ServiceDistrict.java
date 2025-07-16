package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "service_districts")
@ToString
public class ServiceDistrict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @ToString.Exclude
    private TrainerService service;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(length = 100)
    private String city = "Lima";

    @Column(length = 100)
    private String state = "Lima";

    @Column(length = 100)
    private String country = "Perú";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ServiceDistrict() {}

    public ServiceDistrict(String districtName) {
        this.districtName = districtName;
    }

    public ServiceDistrict(String districtName, String city, String state, String country) {
        this.districtName = districtName;
        this.city = city;
        this.state = state;
        this.country = country;
    }
} 