package com.scnsoft.eldermark.entity.serviceplan;

public interface ServicePlanScoringCalculable {

    Integer getBehavioralScore();

    Integer getSupportScore();

    Integer getHealthStatusScore();

    Integer getHousingScore();

    Integer getNutritionSecurityScore();

    Integer getTransportationScore();

    Integer getOtherScore();

    Integer getHousingOnlyScore();

    Integer getSocialWellnessScore();

    Integer getEmploymentScore();

    Integer getMentalWellnessScore();

    Integer getPhysicalWellnessScore();

    Integer getLegalScore();

    Integer getFinancesScore();

    Integer getMedicalOtherSupplyScore();

    Integer getMedicationMgmtAssistanceScore();

    Integer getHomeHealthScore();
}
