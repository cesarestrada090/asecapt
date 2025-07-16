package com.fitech.app.trainers.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    @Column(name = "related_entity_id")
    private Integer relatedEntityId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum NotificationType {
        SERVICE_COMPLETED, GENERAL,
        REVIEW_RECEIVED, RATING_RECEIVED, COMMENT_RECEIVED
    }
    
    public static Notification createReviewReceivedNotification(Integer trainerId, String clientName, Integer rating, Integer reviewId) {
        Notification notification = new Notification();
        notification.setUserId(trainerId);
        notification.setType(NotificationType.REVIEW_RECEIVED);
        notification.setTitle("Nueva reseña recibida");
        notification.setMessage(String.format("%s te ha dejado una reseña de %d estrella%s", 
            clientName, rating, rating == 1 ? "" : "s"));
        notification.setRelatedEntityType("Review");
        notification.setRelatedEntityId(reviewId);
        return notification;
    }
    
    public static Notification createRatingReceivedNotification(Integer trainerId, String clientName, Integer rating, String serviceName) {
        Notification notification = new Notification();
        notification.setUserId(trainerId);
        notification.setType(NotificationType.RATING_RECEIVED);
        notification.setTitle("Nueva calificación recibida");
        notification.setMessage(String.format("%s calificó tu servicio '%s' con %d estrella%s", 
            clientName, serviceName, rating, rating == 1 ? "" : "s"));
        notification.setRelatedEntityType("Rating");
        return notification;
    }
    
    public static Notification createCommentReceivedNotification(Integer trainerId, String clientName, String entityType, Integer entityId) {
        Notification notification = new Notification();
        notification.setUserId(trainerId);
        notification.setType(NotificationType.COMMENT_RECEIVED);
        notification.setTitle("Nuevo comentario recibido");
        notification.setMessage(String.format("%s ha comentado en tu perfil", clientName));
        notification.setRelatedEntityType(entityType);
        notification.setRelatedEntityId(entityId);
        return notification;
    }
} 