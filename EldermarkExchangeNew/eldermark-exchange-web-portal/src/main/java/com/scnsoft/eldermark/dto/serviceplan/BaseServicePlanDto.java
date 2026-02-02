package com.scnsoft.eldermark.dto.serviceplan;

import java.util.List;

public abstract class BaseServicePlanDto {

    private Long id;

    private Long dateCreated;

    private Long dateCompleted;

    private List<ServicePlanDomainScoreDto> scoring;

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

    public Long getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Long dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public List<ServicePlanDomainScoreDto> getScoring() {
        return scoring;
    }

    public void setScoring(List<ServicePlanDomainScoreDto> scoring) {
        this.scoring = scoring;
    }
}
