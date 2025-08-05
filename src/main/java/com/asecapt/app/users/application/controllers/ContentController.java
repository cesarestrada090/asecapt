package com.asecapt.app.users.application.controllers;

import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contents")
@CrossOrigin(origins = {"http://localhost:4200", "https://asecapt.com"})
public class ContentController {

    @Autowired
    private ContentService contentService;

    // === CONTENT CRUD ===

    @GetMapping
    public ResponseEntity<List<Content>> getAllContents() {
        try {
            List<Content> contents = contentService.getAllContents();
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Integer id) {
        try {
            Content content = contentService.getContentById(id);
            if (content != null) {
                return ResponseEntity.ok(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Content> createContent(@RequestBody CreateContentRequest request) {
        try {
            Content content = contentService.createContent(request);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> updateContent(@PathVariable Integer id, @RequestBody UpdateContentRequest request) {
        try {
            Content content = contentService.updateContent(id, request);
            if (content != null) {
                return ResponseEntity.ok(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        try {
            contentService.deleteContent(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Content>> searchContents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type) {
        try {
            List<Content> contents = contentService.searchContents(query, type);
            return ResponseEntity.ok(contents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === CONTENT STATISTICS ===

    @GetMapping("/statistics")
    public ResponseEntity<ContentStatisticsResponse> getContentStatistics() {
        try {
            ContentStatisticsResponse stats = contentService.getContentStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === CONTENT USAGE ===

    @GetMapping("/{id}/usage")
    public ResponseEntity<ContentUsageResponse> getContentUsage(@PathVariable Integer id) {
        try {
            ContentUsageResponse usage = contentService.getContentUsage(id);
            return ResponseEntity.ok(usage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === CONTENT ACTIONS ===

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Content> duplicateContent(@PathVariable Integer id, @RequestBody DuplicateContentRequest request) {
        try {
            Content duplicatedContent = contentService.duplicateContent(id, request.getTitle());
            if (duplicatedContent != null) {
                return ResponseEntity.ok(duplicatedContent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === CONTENT TYPES AND TEMPLATES ===

    @GetMapping("/types")
    public ResponseEntity<List<ContentTypeResponse>> getContentTypes() {
        try {
            List<ContentTypeResponse> types = contentService.getContentTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/templates/{type}")
    public ResponseEntity<List<ContentTemplateResponse>> getContentTemplates(@PathVariable String type) {
        try {
            List<ContentTemplateResponse> templates = contentService.getContentTemplates(type);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // === DTOs ===

    public static class CreateContentRequest {
        private String title;
        private String description;
        private String type;
        private String duration;
        private String content;
        private Boolean isRequired;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    }

    public static class UpdateContentRequest extends CreateContentRequest {
        private Integer id;
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }

    public static class DuplicateContentRequest {
        private String title;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public static class ContentStatisticsResponse {
        private Integer totalContents;
        private Map<String, Integer> contentsByType;
        private String averageDuration;
        private List<Content> mostUsedContents;

        public Integer getTotalContents() { return totalContents; }
        public void setTotalContents(Integer totalContents) { this.totalContents = totalContents; }
        public Map<String, Integer> getContentsByType() { return contentsByType; }
        public void setContentsByType(Map<String, Integer> contentsByType) { this.contentsByType = contentsByType; }
        public String getAverageDuration() { return averageDuration; }
        public void setAverageDuration(String averageDuration) { this.averageDuration = averageDuration; }
        public List<Content> getMostUsedContents() { return mostUsedContents; }
        public void setMostUsedContents(List<Content> mostUsedContents) { this.mostUsedContents = mostUsedContents; }
    }

    public static class ContentUsageResponse {
        private Integer contentId;
        private Integer programsUsingThis;
        private Integer totalEnrollments;
        private String lastUsed;

        public Integer getContentId() { return contentId; }
        public void setContentId(Integer contentId) { this.contentId = contentId; }
        public Integer getProgramsUsingThis() { return programsUsingThis; }
        public void setProgramsUsingThis(Integer programsUsingThis) { this.programsUsingThis = programsUsingThis; }
        public Integer getTotalEnrollments() { return totalEnrollments; }
        public void setTotalEnrollments(Integer totalEnrollments) { this.totalEnrollments = totalEnrollments; }
        public String getLastUsed() { return lastUsed; }
        public void setLastUsed(String lastUsed) { this.lastUsed = lastUsed; }
    }

    public static class ContentTypeResponse {
        private String value;
        private String label;
        private String description;

        public ContentTypeResponse() {}

        public ContentTypeResponse(String value, String label, String description) {
            this.value = value;
            this.label = label;
            this.description = description;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ContentTemplateResponse {
        private String id;
        private String name;
        private String template;

        public ContentTemplateResponse() {}

        public ContentTemplateResponse(String id, String name, String template) {
            this.id = id;
            this.name = name;
            this.template = template;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
    }
} 