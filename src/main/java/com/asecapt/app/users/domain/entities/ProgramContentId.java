package com.asecapt.app.users.domain.entities;

import java.io.Serializable;
import java.util.Objects;

public class ProgramContentId implements Serializable {
    private Integer programId;
    private Integer contentId;

    public ProgramContentId() {}

    public ProgramContentId(Integer programId, Integer contentId) {
        this.programId = programId;
        this.contentId = contentId;
    }

    // Getters and setters
    public Integer getProgramId() { return programId; }
    public void setProgramId(Integer programId) { this.programId = programId; }
    public Integer getContentId() { return contentId; }
    public void setContentId(Integer contentId) { this.contentId = contentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramContentId that = (ProgramContentId) o;
        return Objects.equals(programId, that.programId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(programId, contentId);
    }
}

