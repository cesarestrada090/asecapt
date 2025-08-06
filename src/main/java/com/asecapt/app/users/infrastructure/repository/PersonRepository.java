package com.asecapt.app.users.infrastructure.repository;

import com.asecapt.app.users.domain.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByDocumentNumber(String documentNumber);
    
    // Validation methods
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
    @Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.documentNumber = :documentNumber AND p.id != :id")
    boolean existsByDocumentNumberAndIdNot(@Param("documentNumber") String documentNumber, @Param("id") Integer id);
    
    @Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.email = :email AND p.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);
}