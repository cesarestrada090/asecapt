package com.fitech.app.users.infrastructure.repository;

import com.fitech.app.users.domain.entities.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
  List<Achievement> findByTrainerId(Long trainerId);
}
