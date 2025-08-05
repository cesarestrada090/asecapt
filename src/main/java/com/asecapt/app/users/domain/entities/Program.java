package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "program")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String type; // 'course', 'specialization', 'certification'

    @Column(length = 100)
    private String category;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "active"; // Changed from "draft" to "active"

    @Column(length = 50)
    private String duration;

    @Column
    private Integer credits;

    @Column(length = 50)
    private String price;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(length = 100)
    private String instructor;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(columnDefinition = "TEXT")
    private String prerequisites;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    @Column(name = "created_at", updatable = false)
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

    // Legacy field for compatibility
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;
}

