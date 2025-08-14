package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Program;
import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.entities.ProgramContent;
import com.asecapt.app.users.domain.services.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/programs")
@CrossOrigin(origins = {"http://localhost:4200", "https://asecapt.com"})
public class ProgramController {

    @Autowired
    private ProgramService programService;

    // === PROGRAM CRUD ===

    @GetMapping
    public ResponseEntity<List<Program>> getAllPrograms(@RequestParam(defaultValue = "false") boolean includeCounts) {
        try {
            List<Program> programs = programService.getAllPrograms();
            
            // If includeCounts is true, add content counts as a custom response
            if (includeCounts) {
                // For now, just return programs - frontend will use separate endpoint
                // This is for future enhancement
            }
            
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Program> getProgramById(@PathVariable Integer id) {
        try {
            Program program = programService.getProgramById(id);
            if (program != null) {
                return ResponseEntity.ok(program);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Program> createProgram(@RequestBody CreateProgramRequest request) {
        try {
            Program program = programService.createProgram(request);
            return ResponseEntity.ok(program);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Program> updateProgram(@PathVariable Integer id, @RequestBody UpdateProgramRequest request) {
        try {
            Program program = programService.updateProgram(id, request);
            if (program != null) {
                return ResponseEntity.ok(program);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Integer id) {
        try {
            programService.deleteProgram(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Program>> searchPrograms(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        try {
            List<Program> programs = programService.searchPrograms(query, type, status);
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === PROGRAM STATISTICS ===

    @GetMapping("/statistics")
    public ResponseEntity<ProgramStatisticsResponse> getProgramStatistics() {
        try {
            ProgramStatisticsResponse stats = programService.getProgramStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === PROGRAM CONTENT MANAGEMENT ===

    @GetMapping("/{id}/contents")
    public ResponseEntity<ProgramWithContentsResponse> getProgramWithContents(@PathVariable Integer id) {
        try {
            ProgramWithContentsResponse response = programService.getProgramWithContents(id);
            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/program-contents")
    public ResponseEntity<List<ProgramContent>> getProgramContents(@PathVariable Integer id) {
        try {
            List<ProgramContent> contents = programService.getProgramContents(id);
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === OPTIMIZED ENDPOINT: Get content counts for all programs in single request ===
    @GetMapping("/content-counts")
    public ResponseEntity<Map<Integer, Long>> getAllProgramContentCounts() {
        try {
            Map<Integer, Long> contentCounts = programService.getAllProgramContentCounts();
            return ResponseEntity.ok(contentCounts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/contents")
    public ResponseEntity<ProgramContent> addContentToProgram(@PathVariable Integer id, @RequestBody AddContentToProgramRequest request) {
        try {
            ProgramContent programContent = programService.addContentToProgram(id, request);
            if (programContent != null) {
                return ResponseEntity.ok(programContent);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{programId}/contents/{contentId}")
    public ResponseEntity<ProgramContent> updateProgramContent(
            @PathVariable Integer programId,
            @PathVariable Integer contentId,
            @RequestBody UpdateProgramContentRequest request) {
        try {
            ProgramContent programContent = programService.updateProgramContent(programId, contentId, request);
            if (programContent != null) {
                return ResponseEntity.ok(programContent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{programId}/contents/{contentId}")
    public ResponseEntity<Void> removeProgramContent(@PathVariable Integer programId, @PathVariable Integer contentId) {
        try {
            programService.removeProgramContent(programId, contentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === INDIVIDUAL CONTENT OPERATIONS ===
    
    @PutMapping("/{programId}/contents/{contentId}/toggle-required")
    public ResponseEntity<ProgramContent> toggleContentRequiredStatus(
            @PathVariable Integer programId, 
            @PathVariable Integer contentId) {
        try {
            ProgramContent updatedProgramContent = programService.toggleContentRequiredStatus(programId, contentId);
            if (updatedProgramContent != null) {
                return ResponseEntity.ok(updatedProgramContent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/contents/reorder")
    public ResponseEntity<List<ProgramContent>> reorderProgramContents(@PathVariable Integer id, @RequestBody ReorderContentsRequest request) {
        try {
            List<ProgramContent> reorderedContents = programService.reorderProgramContents(id, request.getContentOrders());
            return ResponseEntity.ok(reorderedContents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === PROGRAM ACTIONS ===

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Program> duplicateProgram(@PathVariable Integer id, @RequestBody DuplicateProgramRequest request) {
        try {
            Program duplicatedProgram = programService.duplicateProgram(id, request.getTitle());
            if (duplicatedProgram != null) {
                return ResponseEntity.ok(duplicatedProgram);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<Program> toggleProgramStatus(@PathVariable Integer id) {
        try {
            Program program = programService.toggleProgramStatus(id);
            if (program != null) {
                return ResponseEntity.ok(program);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<ProgramValidationResponse> validateProgram(@PathVariable Integer id) {
        try {
            ProgramValidationResponse validation = programService.validateProgramForPublication(id);
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === LANDING PAGE SPECIFIC ENDPOINTS ===
    
    @GetMapping("/landing")
    public ResponseEntity<List<Program>> getProgramsForLanding() {
        try {
            List<Program> programs = programService.getProgramsForLanding();
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/landing/all")
    public ResponseEntity<List<Program>> getAllProgramsForLanding() {
        try {
            List<Program> programs = programService.getAllProgramsForLanding();
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === DTOs ===

    public static class CreateProgramRequest {
        private String title;
        private String description;
        private String type;
        private String category;
        private String status;
        private String duration;
        private Integer credits;
        private String price;
        private String startDate;
        private String endDate;
        private String instructor;
        private Integer maxStudents;
        private String prerequisites;
        private String objectives;
        private String imageUrl;
        private Boolean showInLanding;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getInstructor() { return instructor; }
        public void setInstructor(String instructor) { this.instructor = instructor; }
        public Integer getMaxStudents() { return maxStudents; }
        public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
        public String getPrerequisites() { return prerequisites; }
        public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
        public String getObjectives() { return objectives; }
        public void setObjectives(String objectives) { this.objectives = objectives; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public Boolean getShowInLanding() { return showInLanding; }
        public void setShowInLanding(Boolean showInLanding) { this.showInLanding = showInLanding; }
    }

    public static class UpdateProgramRequest extends CreateProgramRequest {
        private Integer id;
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }

    public static class AddContentToProgramRequest {
        private Integer programId;
        private Integer contentId;
        private Integer orderIndex;
        private Boolean isRequired;

        public Integer getProgramId() { return programId; }
        public void setProgramId(Integer programId) { this.programId = programId; }
        public Integer getContentId() { return contentId; }
        public void setContentId(Integer contentId) { this.contentId = contentId; }
        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    }

    public static class UpdateProgramContentRequest {
        private Integer orderIndex;
        private Boolean isRequired;

        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    }

    public static class ReorderContentsRequest {
        private List<ContentOrder> contentOrders;

        public List<ContentOrder> getContentOrders() { return contentOrders; }
        public void setContentOrders(List<ContentOrder> contentOrders) { this.contentOrders = contentOrders; }

        public static class ContentOrder {
            private Integer contentId;
            private Integer orderIndex;

            public Integer getContentId() { return contentId; }
            public void setContentId(Integer contentId) { this.contentId = contentId; }
            public Integer getOrderIndex() { return orderIndex; }
            public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
        }
    }

    public static class DuplicateProgramRequest {
        private String title;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public static class ProgramWithContentsResponse {
        private Program program;
        private List<Content> contents;
        private Integer totalContents;
        private String totalDuration;

        public Program getProgram() { return program; }
        public void setProgram(Program program) { this.program = program; }
        public List<Content> getContents() { return contents; }
        public void setContents(List<Content> contents) { this.contents = contents; }
        public Integer getTotalContents() { return totalContents; }
        public void setTotalContents(Integer totalContents) { this.totalContents = totalContents; }
        public String getTotalDuration() { return totalDuration; }
        public void setTotalDuration(String totalDuration) { this.totalDuration = totalDuration; }
    }

    public static class ProgramStatisticsResponse {
        private Long totalPrograms;
        private Long activePrograms;
        private Long inactivePrograms;
        private Map<String, Long> typeStatistics;
        private Map<String, Long> categoryStatistics;

        public Long getTotalPrograms() { return totalPrograms; }
        public void setTotalPrograms(Long totalPrograms) { this.totalPrograms = totalPrograms; }
        public Long getActivePrograms() { return activePrograms; }
        public void setActivePrograms(Long activePrograms) { this.activePrograms = activePrograms; }
        public Long getInactivePrograms() { return inactivePrograms; }
        public void setInactivePrograms(Long inactivePrograms) { this.inactivePrograms = inactivePrograms; }
        public Map<String, Long> getTypeStatistics() { return typeStatistics; }
        public void setTypeStatistics(Map<String, Long> typeStatistics) { this.typeStatistics = typeStatistics; }
        public Map<String, Long> getCategoryStatistics() { return categoryStatistics; }
        public void setCategoryStatistics(Map<String, Long> categoryStatistics) { this.categoryStatistics = categoryStatistics; }
    }

    public static class ProgramValidationResponse {
        private Boolean isValid;
        private List<String> errors;
        private List<String> warnings;

        public Boolean getIsValid() { return isValid; }
        public void setIsValid(Boolean isValid) { this.isValid = isValid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
}

