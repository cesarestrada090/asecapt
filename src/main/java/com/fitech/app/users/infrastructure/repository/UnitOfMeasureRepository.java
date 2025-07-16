package com.fitech.app.users.infrastructure.repository;

import com.fitech.app.users.domain.entities.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Integer> {
    Optional<UnitOfMeasure> findByName(String name);
    Optional<UnitOfMeasure> findBySymbol(String symbol);
    boolean existsByName(String name);
    boolean existsBySymbol(String symbol);
} 