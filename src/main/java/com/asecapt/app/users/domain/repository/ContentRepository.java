package com.asecapt.app.users.domain.repository;

import com.asecapt.app.users.domain.entities.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {
    // List modules (contents) by program
    @Query("SELECT c FROM Content c JOIN ProgramContent pc ON c.id = pc.contentId WHERE pc.programId = :programId")
    List<Content> findModulesByProgramId(Integer programId);
}

