package com.scnsoft.eldermark.dto.assessment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;

public class ClientAssessmentResultHistoryItemDto {

    private Long id;
    private Long modifiedDate;
    private Long completedDate;
    private String author;
    private AssessmentStatus statusName;
    private String statusTitle;
    private Long typeId;
    private String authorRole;

    @JsonProperty("isArchived")
    private boolean archived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Long completedDate) {
        this.completedDate = completedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public AssessmentStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(AssessmentStatus statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }
}
