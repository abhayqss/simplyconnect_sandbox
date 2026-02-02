package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class ResidentHealthPlan extends LegacyIdAwareEntity {
    @ManyToOne
    @JoinColumn(name="resident_id")
    public Resident resident;

    @Column(name = "plan_name")
    private String healthPlanName;

    @Column(name = "plan_policy_number")
    private String policyNumber;

    @Column(name = "plan_group_number")
    private String groupNumber;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getHealthPlanName() {
        return healthPlanName;
    }

    public void setHealthPlanName(String healthPlanName) {
        this.healthPlanName = healthPlanName;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }
}
