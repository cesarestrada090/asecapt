package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Program;
import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.services.ProgramService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    // List all programs
    @GetMapping
    public List<Program> getAllPrograms() {
        return programService.getAllPrograms();
    }

    // List favorite programs
    @GetMapping("/favorites")
    public List<Program> getFavoritePrograms() {
        return programService.getFavoritePrograms();
    }

    // List modules (contents) by program
    @GetMapping("/{programId}/modules")
    public List<Content> getModulesByProgram(@PathVariable Integer programId) {
        return programService.getModulesByProgramId(programId);
    }
}

