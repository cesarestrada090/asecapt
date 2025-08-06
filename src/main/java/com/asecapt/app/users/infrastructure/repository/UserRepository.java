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
    
    // Búsqueda por tipo de usuario
    List<User> findByType(Integer type);
    Optional<User> findByIdAndType(Integer id, Integer type);
    
    // Búsqueda por nombre
    List<User> findByTypeAndPersonFirstNameContainingIgnoreCase(Integer type, String firstName);
    List<User> findByTypeAndPersonLastNameContainingIgnoreCase(Integer type, String lastName);
    
    // Contadores
    long countByType(Integer type);
    long countByTypeAndActive(Integer type, boolean active);
    
    // Búsqueda de estudiantes - simplified for now
    // TODO: Implement search functionality properly
    @Query("SELECT u FROM User u WHERE u.type = 3")
    List<User> findStudentsByQuery(@Param("query") String query);
    
}