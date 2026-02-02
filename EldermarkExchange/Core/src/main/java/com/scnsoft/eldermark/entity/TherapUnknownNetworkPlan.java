package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TherapUnknownNetworkPlan")
public class TherapUnknownNetworkPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "med_plan_id ", length = 200)
    private String medPlanId;

    @Column(name = "med_plan_name", length = 200)
    private String medPlanName;

    @Column(name = "med_plan_issuer ", length = 200)
    private String medPlanIssuer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedPlanId() {
        return medPlanId;
    }

    public void setMedPlanId(String medPlanId) {
        this.medPlanId = medPlanId;
    }

    public String getMedPlanName() {
        return medPlanName;
    }

    public void setMedPlanName(String medPlanName) {
        this.medPlanName = medPlanName;
    }

    public String getMedPlanIssuer() {
        return medPlanIssuer;
    }

    public void setMedPlanIssuer(String medPlanIssuer) {
        this.medPlanIssuer = medPlanIssuer;
    }

}
