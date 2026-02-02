package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Payer extends LegacyIdAwareEntity {
    @Column(name = "coverage_activity_id")
    private String coverageActivityId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyActivity> policyActivities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long residentId;

    public String getCoverageActivityId() {
        return coverageActivityId;
    }

    public void setCoverageActivityId(String coverageActivityId) {
        this.coverageActivityId = coverageActivityId;
    }

    public List<PolicyActivity> getPolicyActivities() {
        return policyActivities;
    }

    public void setPolicyActivities(List<PolicyActivity> policyActivities) {
        this.policyActivities = policyActivities;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }
}
