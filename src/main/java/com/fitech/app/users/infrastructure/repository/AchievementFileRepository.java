package com.fitech.app.users.infrastructure.repository;

import com.fitech.app.users.domain.entities.AchievementFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementFileRepository extends JpaRepository<AchievementFile, Long> {

}
