package com.scnsoft.eldermark.entity.document.ccd;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

@Entity
public class Payer extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "coverage_activity_id")
    private String coverageActivityId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyActivity> policyActivities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
