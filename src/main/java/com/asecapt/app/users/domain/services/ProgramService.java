package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Program;
import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.repository.ProgramRepository;
import com.asecapt.app.users.domain.repository.ContentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ContentRepository contentRepository;

    public ProgramService(ProgramRepository programRepository, ContentRepository contentRepository) {
        this.programRepository = programRepository;
        this.contentRepository = contentRepository;
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public List<Program> getFavoritePrograms() {
        return programRepository.findByIsFavoriteTrue();
    }

    public List<Content> getModulesByProgramId(Integer programId) {
        return contentRepository.findModulesByProgramId(programId);
    }
}

