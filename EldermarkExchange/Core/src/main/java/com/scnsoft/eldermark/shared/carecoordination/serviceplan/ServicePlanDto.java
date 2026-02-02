package com.scnsoft.eldermark.shared.carecoordination.serviceplan;

import java.util.Date;
import java.util.List;

public class ServicePlanDto {
    private Long id;
    private Date dateCreated;
    private Date dateCompleted;
    private Boolean completed;
    private String createdBy;
    private Long createdById;
    private Long chainId;

    private Long servicePlanScoringId;
    private Integer healthStatusScore;
    private Integer transportationScore;
    private Integer housingScore;
    private Integer nutritionSecurityScore;
    private Integer supportScore;
    private Integer behavioralScore;
    private Integer otherScore;
    private Integer housingOnlyScore;
    private Integer socialWellnessScore;
    private Integer mentalWellnessScore;
    private Integer physicalWellnessScore;
    private Integer taskScore;
    private Integer employmentScore;
    private Integer legalScore;
    private Integer financesScore;
    private Integer medicalOtherSupplyScore;
    private Integer medicationMgmtAssistanceScore;
    private Integer homeHealthScore;

    private List<NeedDto> needs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Long getServicePlanScoringId() {
        return servicePlanScoringId;
    }

    public void setServicePlanScoringId(Long servicePlanScoringId) {
        this.servicePlanScoringId = servicePlanScoringId;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getHealthStatusScore() {
        return healthStatusScore;
    }

    public void setHealthStatusScore(Integer healthStatusScore) {
        this.healthStatusScore = healthStatusScore;
    }

    public Integer getTransportationScore() {
        return transportationScore;
    }

    public void setTransportationScore(Integer transportationScore) {
        this.transportationScore = transportationScore;
    }

    public Integer getHousingScore() {
        return housingScore;
    }

    public void setHousingScore(Integer housingScore) {
        this.housingScore = housingScore;
    }

    public Integer getNutritionSecurityScore() {
        return nutritionSecurityScore;
    }

    public void setNutritionSecurityScore(Integer nutritionSecurityScore) {
        this.nutritionSecurityScore = nutritionSecurityScore;
    }

    public Integer getSupportScore() {
        return supportScore;
    }

    public void setSupportScore(Integer supportScore) {
        this.supportScore = supportScore;
    }

    public Integer getBehavioralScore() {
        return behavioralScore;
    }

    public void setBehavioralScore(Integer behavioralScore) {
        this.behavioralScore = behavioralScore;
    }

    public Integer getOtherScore() {
        return otherScore;
    }

    public void setOtherScore(Integer otherScore) {
        this.otherScore = otherScore;
    }

    public List<NeedDto> getNeeds() {
        return needs;
    }

    public void setNeeds(List<NeedDto> needs) {
        this.needs = needs;
    }

    public Integer getHousingOnlyScore() {
        return housingOnlyScore;
    }

    public void setHousingOnlyScore(Integer housingOnlyScore) {
        this.housingOnlyScore = housingOnlyScore;
    }

    public Integer getSocialWellnessScore() {
        return socialWellnessScore;
    }

    public void setSocialWellnessScore(Integer socialWellnessScore) {
        this.socialWellnessScore = socialWellnessScore;
    }

    public Integer getMentalWellnessScore() {
        return mentalWellnessScore;
    }

    public void setMentalWellnessScore(Integer mentalWellnessScore) {
        this.mentalWellnessScore = mentalWellnessScore;
    }

    public Integer getPhysicalWellnessScore() {
        return physicalWellnessScore;
    }

    public void setPhysicalWellnessScore(Integer physicalWellnessScore) {
        this.physicalWellnessScore = physicalWellnessScore;
    }

    public Integer getTaskScore() {
        return taskScore;
    }

    public void setTaskScore(Integer taskScore) {
        this.taskScore = taskScore;
    }

    public Integer getEmploymentScore() {
        return employmentScore;
    }

    public void setEmploymentScore(Integer employmentScore) {
        this.employmentScore = employmentScore;
    }

    public Integer getLegalScore() {
        return legalScore;
    }

    public void setLegalScore(Integer legalScore) {
        this.legalScore = legalScore;
    }

    public Integer getFinancesScore() {
        return financesScore;
    }

    public void setFinancesScore(Integer financesScore) {
        this.financesScore = financesScore;
    }

    public Integer getMedicalOtherSupplyScore() {
        return medicalOtherSupplyScore;
    }

    public void setMedicalOtherSupplyScore(Integer medicalOtherSupplyScore) {
        this.medicalOtherSupplyScore = medicalOtherSupplyScore;
    }

    public Integer getMedicationMgmtAssistanceScore() {
        return medicationMgmtAssistanceScore;
    }

    public void setMedicationMgmtAssistanceScore(Integer medicationMgmtAssistanceScore) {
        this.medicationMgmtAssistanceScore = medicationMgmtAssistanceScore;
    }

    public Integer getHomeHealthScore() {
        return homeHealthScore;
    }

    public void setHomeHealthScore(Integer homeHealthScore) {
        this.homeHealthScore = homeHealthScore;
    }
}
