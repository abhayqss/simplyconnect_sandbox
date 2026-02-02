package com.scnsoft.eldermark.shared.carecoordination.serviceplan;

import java.util.Date;

public class ServicePlanHistoryListItemDto {
    private Long id;
    private Date dateModified;
    private String status;
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
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
}
