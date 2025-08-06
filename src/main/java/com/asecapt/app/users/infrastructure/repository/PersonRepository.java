package com.asecapt.app.users.infrastructure.repository;

import com.asecapt.app.users.domain.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByDocumentNumber(String documentNumber);
    
    // Validation methods
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
    boolean existsByDocumentNumberAndIdNot(String documentNumber, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
}