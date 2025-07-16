package com.fitech.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "achievements")
@ToString
public class Achievement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "trainer_id", nullable = false)
  private Long trainerId;

  @Column(name = "achievement_type", nullable = false)
  private String achievementType;

  private String title;
  private String description;

  @Column(name = "achieved_at")
  private LocalDate achievedAt;

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

  @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AchievementFile> files = new ArrayList<>();

  public void addFile(AchievementFile file) {
    files.add(file);
    file.setAchievement(this);
  }

  public void removeFile(AchievementFile file) {
    files.remove(file);
    file.setAchievement(null);
  }
}
