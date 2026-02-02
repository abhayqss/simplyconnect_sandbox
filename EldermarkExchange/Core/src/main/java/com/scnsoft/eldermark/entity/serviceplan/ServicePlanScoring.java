package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;

@Entity
@Table(name = "ServicePlanScoring")
public class ServicePlanScoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn (name = "service_plan_id")
    private ServicePlan servicePlan;

    @Column(name = "health_status_score")
    private Integer healthStatusScore;

    @Column(name = "transportation_score")
    private Integer transportationScore;

    @Column(name = "housing_score")
    private Integer housingScore;

    @Column(name = "nutrition_security_score")
    private Integer nutritionSecurityScore;

    @Column(name = "support_score")
    private Integer supportScore;

    @Column(name = "behavioral_score")
    private Integer behavioralScore;

    @Column(name = "other_score")
    private Integer otherScore;

    @Column(name = "total_score", insertable = false, updatable = false)
    private Integer totalScore;

    @Column(name = "housing_only_score")
    private Integer housingOnlyScore;

    @Column(name = "social_wellness_score")
    private Integer socialWellnessScore;

    @Column(name = "mental_wellness_score")
    private Integer mentalWellnessScore;

    @Column(name = "physical_wellness_score")
    private Integer physicalWellness;

    @Column(name = "task_score")
    private Integer taskScore;

    @Column(name = "employment_score")
    private Integer employmentScore;

    @Column(name = "legal_score")
    private Integer legalScore;

    @Column(name = "finances_score")
    private Integer financesScore;

    @Column(name = "medical_other_supply_score")
    private Integer medicalOtherSupplyScore;

    @Column(name = "medication_mgmt_assistance_score")
    private Integer medicationMgmtAssistanceScore;

    @Column(name = "home_health_score")
    private Integer homeHealthScore;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
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

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
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

    public Integer getPhysicalWellness() {
        return physicalWellness;
    }

    public void setPhysicalWellness(Integer physicalWellness) {
        this.physicalWellness = physicalWellness;
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
