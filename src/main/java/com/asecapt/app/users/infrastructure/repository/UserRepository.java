package com.asecapt.app.users.infrastructure.repository;

import com.asecapt.app.users.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmailVerificationToken(String token);
    boolean existsByPersonEmail(String email);
    
    // Búsqueda de entrenadores usando query methods
    List<User> findByType(Integer type);
    
    List<User> findByTypeAndPersonFirstNameContainingIgnoreCase(Integer type, String firstName);
    
    List<User> findByTypeAndPersonLastNameContainingIgnoreCase(Integer type, String lastName);
    
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.person.fitnessGoalTypes fgt " +
           "WHERE u.type = 1 AND fgt.id IN :fitnessGoalIds")
    List<User> findTrainersByFitnessGoalIds(@Param("fitnessGoalIds") List<Integer> fitnessGoalIds);
    
    @Query("SELECT u FROM User u " +
           "JOIN FETCH u.person p " +
           "LEFT JOIN FETCH p.fitnessGoalTypes " +
           "WHERE u.id = :userId")
    Optional<User> findByIdWithPersonAndGoals(@Param("userId") Integer userId);
}