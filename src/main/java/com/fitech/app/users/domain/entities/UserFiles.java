package com.fitech.app.users.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_files")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserFiles {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private Integer userId;

  @Column(nullable = false)
  private String fileName;

  @Size(max = 15)
  @Column(nullable = false)
  private String fileType;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  private LocalDateTime uploadedAt;
}
