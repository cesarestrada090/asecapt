package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Integer> {
    // List all programs
    List<Program> findAll();

    // List favorite programs
    List<Program> findByIsFavoriteTrue();
}

