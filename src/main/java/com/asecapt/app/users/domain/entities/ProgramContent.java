package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "program_content")
@IdClass(ProgramContentId.class)
@NoArgsConstructor
public class ProgramContent {
    
    @Id
    @Column(name = "program_id", nullable = false)
    private Integer programId;
    
    @Id
    @Column(name = "content_id", nullable = false)
    private Integer contentId;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Custom constructor to ensure defaults
    public ProgramContent(Integer programId, Integer contentId, Integer orderIndex, Boolean isRequired) {
        this.programId = programId;
        this.contentId = contentId;
        this.orderIndex = orderIndex;
        this.isRequired = isRequired != null ? isRequired : false; // Default to false if null
    }

    // Relationships (commented out to avoid circular dependencies for now)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "program_id", insertable = false, updatable = false)
    // private Program program;
    
    // @ManyToOne(fetch = FetchType.LAZY) 
    // @JoinColumn(name = "content_id", insertable = false, updatable = false)
    // private Content content;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRequired == null) {
            isRequired = false; // Ensure default value
        }
    }

    // Explicit getters/setters for isRequired to avoid serialization issues
    public Boolean getIsRequired() {
        return isRequired != null ? isRequired : false;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired != null ? isRequired : false;
    }

    // Alternative getter for boolean fields (Jackson sometimes expects this)
    public boolean isRequired() {
        return isRequired != null ? isRequired : false;
    }
}
