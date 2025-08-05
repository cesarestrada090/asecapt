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
    
    // List modules (contents) by program - keeping existing query
    @Query("SELECT c FROM Content c JOIN ProgramContent pc ON c.id = pc.contentId WHERE pc.programId = :programId ORDER BY pc.orderIndex")
    List<Content> findModulesByProgramId(@Param("programId") Integer programId);
}

