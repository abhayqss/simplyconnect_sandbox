package com.scnsoft.eldermark.dto.serviceplan;

public class ServicePlanDomainScoreDto {
    private Long domainId;
    private Integer score;

    public ServicePlanDomainScoreDto() {
    }

    public ServicePlanDomainScoreDto(Long domainId, Integer score) {
        this.domainId = domainId;
        this.score = score;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
