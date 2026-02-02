package com.scnsoft.eldermark.dto.serviceplan;

public class ServicePlanDateDto {
    private Long id;
    private Long dateCreated;

    public ServicePlanDateDto(Long id, Long dateCreated) {
        this.id = id;
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
