package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.ProgramContent;
import com.asecapt.app.users.domain.entities.ProgramContentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramContentRepository extends JpaRepository<ProgramContent, ProgramContentId> {
    
    List<ProgramContent> findByProgramId(Integer programId);
    
    List<ProgramContent> findByProgramIdOrderByOrderIndex(Integer programId);
    
    List<ProgramContent> findByContentId(Integer contentId);
    
    Optional<ProgramContent> findByProgramIdAndContentId(Integer programId, Integer contentId);
    
    void deleteByProgramId(Integer programId);
    
    void deleteByContentId(Integer contentId);
    
    void deleteByProgramIdAndContentId(Integer programId, Integer contentId);
    
    Long countByProgramId(Integer programId);
    
    Long countByContentId(Integer contentId);
    
    // === OPTIMIZED QUERY: Get content counts for all programs in single query ===
    @Query("SELECT pc.programId, COUNT(pc.contentId) FROM ProgramContent pc GROUP BY pc.programId")
    List<Object[]> countContentsByProgramId();
    
    List<ProgramContent> findByProgramIdAndIsRequired(Integer programId, Boolean isRequired);
} 