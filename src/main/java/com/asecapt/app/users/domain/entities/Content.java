package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String type; // 'module', 'lesson', 'assignment', 'exam', 'resource'

    @Column(length = 50)
    private String duration;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "is_required")
    private Boolean isRequired = true;

    @Column(name = "order_index")
    private Integer orderIndex;

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

    // Legacy fields for compatibility
    @Column
    private Integer topicNumber;

    @Column(length = 255)
    private String topic;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "parent_topic_id")
    // private Content parentTopic;
}

