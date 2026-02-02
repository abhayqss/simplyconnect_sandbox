package com.scnsoft.eldermark.entity.serviceplan;

public class ClientServicePlanScoringAware implements ServicePlanScoringCalculable {

    private Long clientId;
    private ServicePlanNeedType servicePlanNeedType;
    private Integer behavioralScore;
    private Integer supportScore;
    private Integer healthStatusScore;
    private Integer housingScore;
    private Integer nutritionSecurityScore;
    private Integer transportationScore;
    private Integer otherScore;
    private Integer housingOnlyScore;
    private Integer socialWellnessScore;
    private Integer employmentScore;
    private Integer mentalWellnessScore;
    private Integer physicalWellnessScore;
    private Integer legalScore;
    private Integer financesScore;
    private Integer medicalOtherSupplyScore;
    private Integer medicationMgmtAssistanceScore;
    private Integer homeHealthScore;

    public ClientServicePlanScoringAware(
        Long clientId,
        ServicePlanNeedType servicePlanNeedType,
        Integer behavioralScore,
        Integer supportScore,
        Integer healthStatusScore,
        Integer housingScore,
        Integer nutritionSecurityScore,
        Integer transportationScore,
        Integer otherScore,
        Integer housingOnlyScore,
        Integer socialWellnessScore,
        Integer employmentScore,
        Integer mentalWellnessScore,
        Integer physicalWellnessScore,
        Integer legalScore,
        Integer financesScore,
        Integer medicalOtherSupplyScore,
        Integer medicationMgmtAssistanceScore,
        Integer homeHealthScore
    ) {
        this.clientId = clientId;
        this.servicePlanNeedType = servicePlanNeedType;
        this.behavioralScore = behavioralScore;
        this.supportScore = supportScore;
        this.healthStatusScore = healthStatusScore;
        this.housingScore = housingScore;
        this.nutritionSecurityScore = nutritionSecurityScore;
        this.transportationScore = transportationScore;
        this.otherScore = otherScore;
        this.housingOnlyScore = housingOnlyScore;
        this.socialWellnessScore = socialWellnessScore;
        this.employmentScore = employmentScore;
        this.mentalWellnessScore = mentalWellnessScore;
        this.physicalWellnessScore = physicalWellnessScore;
        this.legalScore = legalScore;
        this.financesScore = financesScore;
        this.medicalOtherSupplyScore = medicalOtherSupplyScore;
        this.medicationMgmtAssistanceScore = medicationMgmtAssistanceScore;
        this.homeHealthScore = homeHealthScore;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ServicePlanNeedType getServicePlanNeedType() {
        return servicePlanNeedType;
    }

    public void setServicePlanNeedType(ServicePlanNeedType servicePlanNeedType) {
        this.servicePlanNeedType = servicePlanNeedType;
    }

    @Override
    public Integer getBehavioralScore() {
        return behavioralScore;
    }

    public void setBehavioralScore(Integer behavioralScore) {
        this.behavioralScore = behavioralScore;
    }

    @Override
    public Integer getSupportScore() {
        return supportScore;
    }

    public void setSupportScore(Integer supportScore) {
        this.supportScore = supportScore;
    }

    @Override
    public Integer getHealthStatusScore() {
        return healthStatusScore;
    }

    public void setHealthStatusScore(Integer healthStatusScore) {
        this.healthStatusScore = healthStatusScore;
    }

    @Override
    public Integer getHousingScore() {
        return housingScore;
    }

    public void setHousingScore(Integer housingScore) {
        this.housingScore = housingScore;
    }

    @Override
    public Integer getNutritionSecurityScore() {
        return nutritionSecurityScore;
    }

    public void setNutritionSecurityScore(Integer nutritionSecurityScore) {
        this.nutritionSecurityScore = nutritionSecurityScore;
    }

    @Override
    public Integer getTransportationScore() {
        return transportationScore;
    }

    public void setTransportationScore(Integer transportationScore) {
        this.transportationScore = transportationScore;
    }

    @Override
    public Integer getOtherScore() {
        return otherScore;
    }

    public void setOtherScore(Integer otherScore) {
        this.otherScore = otherScore;
    }

    @Override
    public Integer getHousingOnlyScore() {
        return housingOnlyScore;
    }

    public void setHousingOnlyScore(Integer housingOnlyScore) {
        this.housingOnlyScore = housingOnlyScore;
    }

    @Override
    public Integer getSocialWellnessScore() {
        return socialWellnessScore;
    }

    public void setSocialWellnessScore(Integer socialWellnessScore) {
        this.socialWellnessScore = socialWellnessScore;
    }

    @Override
    public Integer getEmploymentScore() {
        return employmentScore;
    }

    public void setEmploymentScore(Integer employmentScore) {
        this.employmentScore = employmentScore;
    }

    @Override
    public Integer getMentalWellnessScore() {
        return mentalWellnessScore;
    }

    public void setMentalWellnessScore(Integer mentalWellnessScore) {
        this.mentalWellnessScore = mentalWellnessScore;
    }

    @Override
    public Integer getPhysicalWellnessScore() {
        return physicalWellnessScore;
    }

    public void setPhysicalWellnessScore(Integer physicalWellnessScore) {
        this.physicalWellnessScore = physicalWellnessScore;
    }

    @Override
    public Integer getLegalScore() {
        return legalScore;
    }

    public void setLegalScore(Integer legalScore) {
        this.legalScore = legalScore;
    }

    @Override
    public Integer getFinancesScore() {
        return financesScore;
    }

    public void setFinancesScore(Integer financesScore) {
        this.financesScore = financesScore;
    }

    @Override
    public Integer getMedicalOtherSupplyScore() {
        return medicalOtherSupplyScore;
    }

    public void setMedicalOtherSupplyScore(Integer medicalOtherSupplyScore) {
        this.medicalOtherSupplyScore = medicalOtherSupplyScore;
    }

    @Override
    public Integer getMedicationMgmtAssistanceScore() {
        return medicationMgmtAssistanceScore;
    }

    public void setMedicationMgmtAssistanceScore(Integer medicationMgmtAssistanceScore) {
        this.medicationMgmtAssistanceScore = medicationMgmtAssistanceScore;
    }

    @Override
    public Integer getHomeHealthScore() {
        return homeHealthScore;
    }

    public void setHomeHealthScore(Integer homeHealthScore) {
        this.homeHealthScore = homeHealthScore;
    }
}
