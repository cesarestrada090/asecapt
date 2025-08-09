package com.asecapt.app.users.domain.services;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class CourseInitialsService {
    
    public String generateCourseInitials(String courseTitle) {
        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            return "GEN"; // General por defecto
        }
        
        // Remover palabras comunes y generar iniciales
        String[] commonWords = {"de", "del", "la", "el", "en", "para", "con", "por", "y", "o", "un", "una"};
        String[] words = courseTitle.trim().split("\\s+");
        
        StringBuilder initials = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0 && !Arrays.asList(commonWords).contains(word.toLowerCase())) {
                initials.append(word.substring(0, 1).toUpperCase());
                if (initials.length() >= 3) break; // Máximo 3 iniciales
            }
        }
        
        // Si no hay suficientes iniciales, usar las primeras letras
        if (initials.length() < 2) {
            initials = new StringBuilder();
            String cleanTitle = courseTitle.replaceAll("[^a-zA-Z]", "").toUpperCase();
            for (int i = 0; i < Math.min(3, cleanTitle.length()); i++) {
                initials.append(cleanTitle.charAt(i));
            }
        }
        
        return initials.length() > 0 ? initials.toString() : "GEN";
    }
}
