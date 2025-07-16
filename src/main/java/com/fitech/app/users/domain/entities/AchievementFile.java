package com.fitech.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_files")
@Data
@ToString
public class AchievementFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne()
  @JoinColumn(name = "achievement_id")
  @ToString.Exclude
  private Achievement achievement;

  @ManyToOne()
  @JoinColumn(name = "file_user_id", nullable = false)
  private UserFiles userFile;

  @Column(name = "uploaded_at", updatable = false)
  private LocalDateTime uploadedAt;

  @PrePersist
  protected void onCreate() {
    uploadedAt = LocalDateTime.now();
  }
}
