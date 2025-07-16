package com.fitech.app.trainers.application.dtos;

import java.util.List;

public class TrainerExperienceDto {
    
    private List<TrainerEducationDto> education;
    private List<TrainerCertificationDto> certifications;
    private List<TrainerRecognitionDto> recognitions;
    private TrainerExperienceSummaryDto summary;
    
    // Constructors
    public TrainerExperienceDto() {}
    
    public TrainerExperienceDto(List<TrainerEducationDto> education,
                              List<TrainerCertificationDto> certifications,
                              List<TrainerRecognitionDto> recognitions,
                              TrainerExperienceSummaryDto summary) {
        this.education = education;
        this.certifications = certifications;
        this.recognitions = recognitions;
        this.summary = summary;
    }
    
    // Getters and Setters
    public List<TrainerEducationDto> getEducation() {
        return education;
    }
    
    public void setEducation(List<TrainerEducationDto> education) {
        this.education = education;
    }
    
    public List<TrainerCertificationDto> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(List<TrainerCertificationDto> certifications) {
        this.certifications = certifications;
    }
    
    public List<TrainerRecognitionDto> getRecognitions() {
        return recognitions;
    }
    
    public void setRecognitions(List<TrainerRecognitionDto> recognitions) {
        this.recognitions = recognitions;
    }
    
    public TrainerExperienceSummaryDto getSummary() {
        return summary;
    }
    
    public void setSummary(TrainerExperienceSummaryDto summary) {
        this.summary = summary;
    }
    
    // Inner class for experience summary
    public static class TrainerExperienceSummaryDto {
        private Integer totalEducation;
        private Integer totalCertifications;
        private Integer totalRecognitions;
        private Integer validCertifications;
        private Integer expiredCertifications;
        private Integer recentRecognitions;
        private Integer internationalRecognitions;
        
        // Constructors
        public TrainerExperienceSummaryDto() {}
        
        public TrainerExperienceSummaryDto(Integer totalEducation, Integer totalCertifications,
                                         Integer totalRecognitions, Integer validCertifications,
                                         Integer expiredCertifications, Integer recentRecognitions,
                                         Integer internationalRecognitions) {
            this.totalEducation = totalEducation;
            this.totalCertifications = totalCertifications;
            this.totalRecognitions = totalRecognitions;
            this.validCertifications = validCertifications;
            this.expiredCertifications = expiredCertifications;
            this.recentRecognitions = recentRecognitions;
            this.internationalRecognitions = internationalRecognitions;
        }
        
        // Getters and Setters
        public Integer getTotalEducation() {
            return totalEducation;
        }
        
        public void setTotalEducation(Integer totalEducation) {
            this.totalEducation = totalEducation;
        }
        
        public Integer getTotalCertifications() {
            return totalCertifications;
        }
        
        public void setTotalCertifications(Integer totalCertifications) {
            this.totalCertifications = totalCertifications;
        }
        
        public Integer getTotalRecognitions() {
            return totalRecognitions;
        }
        
        public void setTotalRecognitions(Integer totalRecognitions) {
            this.totalRecognitions = totalRecognitions;
        }
        
        public Integer getValidCertifications() {
            return validCertifications;
        }
        
        public void setValidCertifications(Integer validCertifications) {
            this.validCertifications = validCertifications;
        }
        
        public Integer getExpiredCertifications() {
            return expiredCertifications;
        }
        
        public void setExpiredCertifications(Integer expiredCertifications) {
            this.expiredCertifications = expiredCertifications;
        }
        
        public Integer getRecentRecognitions() {
            return recentRecognitions;
        }
        
        public void setRecentRecognitions(Integer recentRecognitions) {
            this.recentRecognitions = recentRecognitions;
        }
        
        public Integer getInternationalRecognitions() {
            return internationalRecognitions;
        }
        
        public void setInternationalRecognitions(Integer internationalRecognitions) {
            this.internationalRecognitions = internationalRecognitions;
        }
    }
} 