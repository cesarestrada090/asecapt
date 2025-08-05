package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {
    
    // Search methods
    List<Content> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    List<Content> findByTitleContainingIgnoreCase(String title);
    List<Content> findByDescriptionContainingIgnoreCase(String description);
    
    // Filter by type
    List<Content> findByType(String type);
    List<Content> findByTypeOrderByTitleAsc(String type);
    
    // Filter by required status
    List<Content> findByIsRequired(Boolean isRequired);
    
    // Find by content text
    List<Content> findByContentContainingIgnoreCase(String content);
    
    // Order by date
    List<Content> findAllByOrderByCreatedAtDesc();
    List<Content> findByTypeOrderByCreatedAtDesc(String type);
    
    // Count methods
    Long countByType(String type);
    Long countByIsRequired(Boolean isRequired);
    
    // List modules (contents) by program - keeping existing query
    @Query("SELECT c FROM Content c JOIN ProgramContent pc ON c.id = pc.contentId WHERE pc.programId = :programId ORDER BY pc.orderIndex")
    List<Content> findModulesByProgramId(@Param("programId") Integer programId);
}

