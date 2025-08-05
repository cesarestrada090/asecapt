package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.domain.entities.Content;
import com.asecapt.app.users.domain.entities.ProgramContent;
import com.asecapt.app.users.domain.repository.ContentRepository;
import com.asecapt.app.users.domain.repository.ProgramContentRepository;
import com.asecapt.app.users.application.controllers.ContentController.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ProgramContentRepository programContentRepository;

    // === CONTENT CRUD ===

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public Content getContentById(Integer id) {
        return contentRepository.findById(id).orElse(null);
    }

    public Content createContent(CreateContentRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setType(request.getType());
        content.setDuration(request.getDuration());
        content.setContent(request.getContent());
        content.setIsRequired(request.getIsRequired());
        
        return contentRepository.save(content);
    }

    public Content updateContent(Integer id, UpdateContentRequest request) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if (optionalContent.isPresent()) {
            Content content = optionalContent.get();
            content.setTitle(request.getTitle());
            content.setDescription(request.getDescription());
            content.setType(request.getType());
            content.setDuration(request.getDuration());
            content.setContent(request.getContent());
            content.setIsRequired(request.getIsRequired());
            
            return contentRepository.save(content);
        }
        return null;
    }

    public void deleteContent(Integer id) {
        // Delete from program contents first
        programContentRepository.deleteByContentId(id);
        // Then delete the content
        contentRepository.deleteById(id);
    }

    public List<Content> searchContents(String query, String type) {
        if (query != null && !query.trim().isEmpty()) {
            return contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        } else {
            List<Content> contents = contentRepository.findAll();
            if (type != null && !type.trim().isEmpty()) {
                contents = contents.stream()
                    .filter(c -> type.equals(c.getType()))
                    .collect(Collectors.toList());
            }
            return contents;
        }
    }

    // === CONTENT STATISTICS ===

    public ContentStatisticsResponse getContentStatistics() {
        ContentStatisticsResponse stats = new ContentStatisticsResponse();
        
        List<Content> allContents = contentRepository.findAll();
        stats.setTotalContents(allContents.size());
        
        // Calculate content types distribution
        Map<String, Integer> contentsByType = allContents.stream()
            .collect(Collectors.groupingBy(
                Content::getType,
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        stats.setContentsByType(contentsByType);
        
        // Calculate average duration (simplified)
        stats.setAverageDuration("2 horas promedio");
        
        // Get most used contents (top 5)
        List<ProgramContent> programContents = programContentRepository.findAll();
        Map<Integer, Long> contentUsageCount = programContents.stream()
            .collect(Collectors.groupingBy(ProgramContent::getContentId, Collectors.counting()));
        
        List<Content> mostUsedContents = contentUsageCount.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> contentRepository.findById(entry.getKey()).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        stats.setMostUsedContents(mostUsedContents);
        
        return stats;
    }

    // === CONTENT USAGE ===

    public ContentUsageResponse getContentUsage(Integer id) {
        ContentUsageResponse usage = new ContentUsageResponse();
        usage.setContentId(id);
        
        List<ProgramContent> programContents = programContentRepository.findByContentId(id);
        usage.setProgramsUsingThis(programContents.size());
        
        // TODO: Calculate total enrollments from enrollment service
        usage.setTotalEnrollments(0);
        
        // TODO: Get last used date from actual usage data
        usage.setLastUsed("2024-01-15");
        
        return usage;
    }

    // === CONTENT ACTIONS ===

    public Content duplicateContent(Integer id, String newTitle) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if (optionalContent.isPresent()) {
            Content originalContent = optionalContent.get();
            
            Content duplicatedContent = new Content();
            duplicatedContent.setTitle(newTitle);
            duplicatedContent.setDescription(originalContent.getDescription());
            duplicatedContent.setType(originalContent.getType());
            duplicatedContent.setDuration(originalContent.getDuration());
            duplicatedContent.setContent(originalContent.getContent());
            duplicatedContent.setIsRequired(originalContent.getIsRequired());
            
            return contentRepository.save(duplicatedContent);
        }
        return null;
    }

    // === CONTENT TYPES AND TEMPLATES ===

    public List<ContentTypeResponse> getContentTypes() {
        List<ContentTypeResponse> types = new ArrayList<>();
        types.add(new ContentTypeResponse("module", "Módulo", "Módulo principal de aprendizaje"));
        types.add(new ContentTypeResponse("lesson", "Lección", "Lección individual dentro de un módulo"));
        types.add(new ContentTypeResponse("assignment", "Asignación", "Tarea o ejercicio para completar"));
        types.add(new ContentTypeResponse("exam", "Examen", "Evaluación formal"));
        types.add(new ContentTypeResponse("resource", "Recurso", "Material de apoyo o referencia"));
        return types;
    }

    public List<ContentTemplateResponse> getContentTemplates(String type) {
        List<ContentTemplateResponse> templates = new ArrayList<>();
        
        switch (type) {
            case "module":
                templates.add(new ContentTemplateResponse("basic_module", "Módulo Básico", "Plantilla para módulo básico con objetivos, contenido y evaluación"));
                templates.add(new ContentTemplateResponse("advanced_module", "Módulo Avanzado", "Plantilla para módulo avanzado con múltiples lecciones"));
                break;
            case "lesson":
                templates.add(new ContentTemplateResponse("theory_lesson", "Lección Teórica", "Plantilla para lección teórica con conceptos y ejemplos"));
                templates.add(new ContentTemplateResponse("practical_lesson", "Lección Práctica", "Plantilla para lección práctica con ejercicios"));
                break;
            case "assignment":
                templates.add(new ContentTemplateResponse("homework", "Tarea", "Plantilla para tarea individual"));
                templates.add(new ContentTemplateResponse("project", "Proyecto", "Plantilla para proyecto grupal"));
                break;
            case "exam":
                templates.add(new ContentTemplateResponse("quiz", "Quiz", "Plantilla para evaluación corta"));
                templates.add(new ContentTemplateResponse("final_exam", "Examen Final", "Plantilla para evaluación final"));
                break;
            case "resource":
                templates.add(new ContentTemplateResponse("reading", "Lectura", "Plantilla para material de lectura"));
                templates.add(new ContentTemplateResponse("video", "Video", "Plantilla para contenido en video"));
                break;
        }
        
        return templates;
    }
} 