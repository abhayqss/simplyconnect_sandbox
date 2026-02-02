package com.scnsoft.eldermark.dto.serviceplan;

public class ServicePlanHistoryDto {
    private Long id;
    private Long dateModified;
    private String status;
    private String author;
    private String authorRole;
    private boolean isArchived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDateModified() {
        return dateModified;
    }

    public void setDateModified(Long dateModified) {
        this.dateModified = dateModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean archived) {
        isArchived = archived;
    }
}
