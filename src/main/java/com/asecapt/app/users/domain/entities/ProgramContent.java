package com.asecapt.app.users.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "program_content")
@IdClass(ProgramContentId.class)
public class ProgramContent {
    @Id
    private Integer programId;

    @Id
    private Integer contentId;
}
