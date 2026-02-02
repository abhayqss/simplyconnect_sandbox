package com.scnsoft.eldermark.dump.entity.serviceplan;


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
    private ClientServicePlan servicePlan;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ClientServicePlan servicePlan) {
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
}
