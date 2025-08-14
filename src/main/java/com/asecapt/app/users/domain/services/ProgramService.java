package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Program;
import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.entities.ProgramContent;
import com.asecapt.app.users.domain.repository.ProgramRepository;
import com.asecapt.app.users.domain.repository.ContentRepository;
import com.asecapt.app.users.domain.repository.ProgramContentRepository;
import com.asecapt.app.users.application.controllers.ProgramController.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ProgramContentRepository programContentRepository;

    // === PROGRAM CRUD ===

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    // === LANDING PAGE SPECIFIC METHODS ===
    
    public List<Program> getProgramsForLanding() {
        return programRepository.findByShowInLandingTrueAndStatusOrderByCreatedAtDesc("active");
    }
    
    public List<Program> getAllProgramsForLanding() {
        return programRepository.findByShowInLandingTrueOrderByCreatedAtDesc();
    }

    public Program getProgramById(Integer id) {
        return programRepository.findById(id).orElse(null);
    }

    public Program createProgram(CreateProgramRequest request) {
        Program program = new Program();
        program.setTitle(request.getTitle());
        program.setName(request.getTitle()); // Map title to required name field
        program.setDescription(request.getDescription());
        program.setType(request.getType());
        program.setCategory(request.getCategory());
        program.setStatus(request.getStatus());
        program.setDuration(request.getDuration());
        program.setHours(extractHoursFromDuration(request.getDuration())); // Extract hours from duration
        program.setCredits(request.getCredits());
        program.setPrice(request.getPrice());
        program.setStartDate(request.getStartDate());
        program.setEndDate(request.getEndDate());
        program.setInstructor(request.getInstructor());
        program.setMaxStudents(request.getMaxStudents());
        program.setPrerequisites(request.getPrerequisites());
        program.setObjectives(request.getObjectives());
        
        return programRepository.save(program);
    }

    public Program updateProgram(Integer id, UpdateProgramRequest request) {
        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isPresent()) {
            Program program = optionalProgram.get();
            program.setTitle(request.getTitle());
            program.setName(request.getTitle()); // Map title to required name field
            program.setDescription(request.getDescription());
            program.setType(request.getType());
            program.setCategory(request.getCategory());
            program.setStatus(request.getStatus());
            program.setDuration(request.getDuration());
            program.setHours(extractHoursFromDuration(request.getDuration())); // Extract hours from duration
            program.setCredits(request.getCredits());
            program.setPrice(request.getPrice());
            program.setStartDate(request.getStartDate());
            program.setEndDate(request.getEndDate());
            program.setInstructor(request.getInstructor());
            program.setMaxStudents(request.getMaxStudents());
            program.setPrerequisites(request.getPrerequisites());
            program.setObjectives(request.getObjectives());
            
            return programRepository.save(program);
        }
        return null;
    }

    public void deleteProgram(Integer id) {
        // Delete program contents first
        programContentRepository.deleteByProgramId(id);
        // Then delete the program
        programRepository.deleteById(id);
    }

    public List<Program> searchPrograms(String query, String type, String status) {
        if (query != null && !query.trim().isEmpty()) {
            return programRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        } else {
            List<Program> programs = programRepository.findAll();
            if (type != null && !type.trim().isEmpty()) {
                programs = programs.stream()
                    .filter(p -> type.equals(p.getType()))
                    .collect(Collectors.toList());
            }
            if (status != null && !status.trim().isEmpty()) {
                programs = programs.stream()
                    .filter(p -> status.equals(p.getStatus()))
                    .collect(Collectors.toList());
            }
            return programs;
        }
    }

    // === PROGRAM STATISTICS ===

    public ProgramStatisticsResponse getProgramStatistics() {
        List<Program> allPrograms = programRepository.findAll();
        
        long totalPrograms = allPrograms.size();
        long activePrograms = programRepository.countByStatus("active");
        long inactivePrograms = programRepository.countByStatus("inactive");
        
        Map<String, Long> typeStats = allPrograms.stream()
            .collect(Collectors.groupingBy(Program::getType, Collectors.counting()));
        
        Map<String, Long> categoryStats = allPrograms.stream()
            .collect(Collectors.groupingBy(Program::getCategory, Collectors.counting()));
        
        ProgramStatisticsResponse stats = new ProgramStatisticsResponse();
        stats.setTotalPrograms(totalPrograms);
        stats.setActivePrograms(activePrograms);
        stats.setInactivePrograms(inactivePrograms);
        stats.setTypeStatistics(typeStats);
        stats.setCategoryStatistics(categoryStats);
        
        return stats;
    }

    // === PROGRAM CONTENT MANAGEMENT ===

    public ProgramWithContentsResponse getProgramWithContents(Integer programId) {
        Optional<Program> optionalProgram = programRepository.findById(programId);
        if (optionalProgram.isPresent()) {
            Program program = optionalProgram.get();
            List<ProgramContent> programContents = programContentRepository.findByProgramIdOrderByOrderIndex(programId);
            List<Content> contents = programContents.stream()
                .map(pc -> contentRepository.findById(pc.getContentId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            ProgramWithContentsResponse response = new ProgramWithContentsResponse();
            response.setProgram(program);
            response.setContents(contents);
            response.setTotalContents(contents.size());
            response.setTotalDuration(calculateTotalDuration(contents));
            
            return response;
        }
        return null;
    }

    public List<ProgramContent> getProgramContents(Integer programId) {
        return programContentRepository.findByProgramIdOrderByOrderIndex(programId);
    }

    // === OPTIMIZED METHOD: Get content counts for all programs in single query ===
    public Map<Integer, Long> getAllProgramContentCounts() {
        List<Program> allPrograms = programRepository.findAll();
        Map<Integer, Long> contentCounts = new HashMap<>();
        
        // Initialize all programs with count 0
        for (Program program : allPrograms) {
            contentCounts.put(program.getId(), 0L);
        }
        
        // Get actual counts using a single query
        List<Object[]> counts = programContentRepository.countContentsByProgramId();
        for (Object[] count : counts) {
            Integer programId = (Integer) count[0];
            Long contentCount = (Long) count[1];
            contentCounts.put(programId, contentCount);
        }
        
        return contentCounts;
    }

    public ProgramContent addContentToProgram(Integer programId, AddContentToProgramRequest request) {
        Optional<Program> optionalProgram = programRepository.findById(programId);
        Optional<Content> optionalContent = contentRepository.findById(request.getContentId());
        
        if (optionalProgram.isPresent() && optionalContent.isPresent()) {
            // Check if content is already added to this program
            Optional<ProgramContent> existing = programContentRepository.findByProgramIdAndContentId(programId, request.getContentId());
            if (existing.isPresent()) {
                return null; // Already exists
            }
            
            ProgramContent programContent = new ProgramContent();
            programContent.setProgramId(programId);
            programContent.setContentId(request.getContentId());
            programContent.setOrderIndex(request.getOrderIndex());
            programContent.setIsRequired(request.getIsRequired());
            
            return programContentRepository.save(programContent);
        }
        return null;
    }

    public ProgramContent updateProgramContent(Integer programId, Integer contentId, UpdateProgramContentRequest request) {
        Optional<ProgramContent> optionalProgramContent = programContentRepository.findByProgramIdAndContentId(programId, contentId);
        if (optionalProgramContent.isPresent()) {
            ProgramContent programContent = optionalProgramContent.get();
            if (request.getOrderIndex() != null) {
                programContent.setOrderIndex(request.getOrderIndex());
            }
            if (request.getIsRequired() != null) {
                programContent.setIsRequired(request.getIsRequired());
            }
            return programContentRepository.save(programContent);
        }
        return null;
    }

    public void removeProgramContent(Integer programId, Integer contentId) {
        programContentRepository.deleteByProgramIdAndContentId(programId, contentId);
    }

    // === INDIVIDUAL CONTENT OPERATIONS ===
    
    public ProgramContent toggleContentRequiredStatus(Integer programId, Integer contentId) {
        Optional<ProgramContent> programContentOpt = programContentRepository.findByProgramIdAndContentId(programId, contentId);
        
        if (programContentOpt.isPresent()) {
            ProgramContent programContent = programContentOpt.get();
            // Toggle the isRequired status
            programContent.setIsRequired(!programContent.getIsRequired());
            return programContentRepository.save(programContent);
        }
        
        return null; // Content not found in this program
    }

    public List<ProgramContent> reorderProgramContents(Integer programId, List<ReorderContentsRequest.ContentOrder> contentOrders) {
        for (ReorderContentsRequest.ContentOrder order : contentOrders) {
            Optional<ProgramContent> optionalProgramContent = programContentRepository.findByProgramIdAndContentId(programId, order.getContentId());
            if (optionalProgramContent.isPresent()) {
                ProgramContent programContent = optionalProgramContent.get();
                programContent.setOrderIndex(order.getOrderIndex());
                programContentRepository.save(programContent);
            }
        }
        return programContentRepository.findByProgramIdOrderByOrderIndex(programId);
    }

    // === PROGRAM ACTIONS ===

    public Program duplicateProgram(Integer id, String newTitle) {
        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isPresent()) {
            Program originalProgram = optionalProgram.get();
            
            // Create new program
            Program duplicatedProgram = new Program();
            duplicatedProgram.setTitle(newTitle);
            duplicatedProgram.setName(newTitle); // Map title to required name field
            duplicatedProgram.setDescription(originalProgram.getDescription());
            duplicatedProgram.setType(originalProgram.getType());
            duplicatedProgram.setCategory(originalProgram.getCategory());
            duplicatedProgram.setStatus("inactive"); // New programs start as inactive
            duplicatedProgram.setDuration(originalProgram.getDuration());
            duplicatedProgram.setHours(originalProgram.getHours()); // Copy hours from original
            duplicatedProgram.setCredits(originalProgram.getCredits());
            duplicatedProgram.setPrice(originalProgram.getPrice());
            duplicatedProgram.setStartDate(originalProgram.getStartDate());
            duplicatedProgram.setEndDate(originalProgram.getEndDate());
            duplicatedProgram.setInstructor(originalProgram.getInstructor());
            duplicatedProgram.setMaxStudents(originalProgram.getMaxStudents());
            duplicatedProgram.setPrerequisites(originalProgram.getPrerequisites());
            duplicatedProgram.setObjectives(originalProgram.getObjectives());
            
            duplicatedProgram = programRepository.save(duplicatedProgram);
            
            // Copy program contents
            List<ProgramContent> originalContents = programContentRepository.findByProgramIdOrderByOrderIndex(id);
            for (ProgramContent originalContent : originalContents) {
                ProgramContent newContent = new ProgramContent();
                newContent.setProgramId(duplicatedProgram.getId());
                newContent.setContentId(originalContent.getContentId());
                newContent.setOrderIndex(originalContent.getOrderIndex());
                newContent.setIsRequired(originalContent.getIsRequired());
                programContentRepository.save(newContent);
            }
            
            return duplicatedProgram;
        }
        return null;
    }

    public Program toggleProgramStatus(Integer id) {
        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isPresent()) {
            Program program = optionalProgram.get();
            // Simple toggle between active and inactive
            String newStatus = "active".equals(program.getStatus()) ? "inactive" : "active";
            program.setStatus(newStatus);
            return programRepository.save(program);
        }
        return null;
    }

    public ProgramValidationResponse validateProgramForPublication(Integer id) {
        ProgramValidationResponse validation = new ProgramValidationResponse();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isPresent()) {
            Program program = optionalProgram.get();
            
            // Validate required fields
            if (program.getTitle() == null || program.getTitle().trim().isEmpty()) {
                errors.add("El título del programa es requerido");
            }
            if (program.getDescription() == null || program.getDescription().trim().isEmpty()) {
                errors.add("La descripción del programa es requerida");
            }
            if (program.getInstructor() == null || program.getInstructor().trim().isEmpty()) {
                warnings.add("Se recomienda asignar un instructor");
            }
            
            // Validate content
            List<ProgramContent> contents = programContentRepository.findByProgramIdOrderByOrderIndex(id);
            if (contents.isEmpty()) {
                warnings.add("El programa no tiene contenidos asignados");
            }
            
            validation.setIsValid(errors.isEmpty());
            validation.setErrors(errors);
            validation.setWarnings(warnings);
        } else {
            errors.add("Programa no encontrado");
            validation.setIsValid(false);
            validation.setErrors(errors);
            validation.setWarnings(warnings);
        }
        
        return validation;
    }

    // === UTILITY METHODS ===

    private String calculateTotalDuration(List<Content> contents) {
        // Simple implementation - in real scenario, parse duration strings and sum them
        return contents.size() + " módulos";
    }

    private Integer extractHoursFromDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return 40; // Default value if no duration provided
        }
        
        // Try to extract number from duration string (e.g., "20 horas" -> 20)
        try {
            String cleanDuration = duration.toLowerCase()
                .replaceAll("[^0-9]", ""); // Remove all non-numeric characters
            if (!cleanDuration.isEmpty()) {
                return Integer.parseInt(cleanDuration);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, return default
        }
        
        return 40; // Default fallback value
    }
}
