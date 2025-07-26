package com.asecapt.app.users.infrastructure.repository;

import com.asecapt.app.users.domain.entities.UserFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFileRepository extends JpaRepository<UserFiles, Integer> {
  List<UserFiles> findByUserId(Integer userId);
  void deleteByUserId(Integer userId);
  Boolean existsByUserId(Integer userId);
}
