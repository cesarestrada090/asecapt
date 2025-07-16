package com.fitech.app.trainers.infrastructure.repository;

import com.fitech.app.trainers.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    // Buscar notificaciones por usuario
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    // Buscar notificaciones no leídas por usuario
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
    
    // Buscar notificaciones por tipo
    List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotificationType type);
    
    // Contar notificaciones no leídas por usuario
    Long countByUserIdAndIsReadFalse(Integer userId);
} 