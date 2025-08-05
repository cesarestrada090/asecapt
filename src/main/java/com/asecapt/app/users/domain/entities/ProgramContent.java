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
@AllArgsConstructor
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
    private Boolean isRequired = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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
    }
}
