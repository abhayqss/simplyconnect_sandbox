package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Organization")
public class Organization extends BaseReadOnlyEntity {

    @Column(name = "is_consana_enabled", nullable = false)
    private Boolean isConsanaIntegrationEnabled;

    @Column(name = "inactive")
    private Boolean isInactive;

    @Column(name = "consana_org_id")
    private String consanaOrgId;

    public Organization() {
    }

    public Organization(Long id, String consanaOrgId) {
        super(id);
        this.consanaOrgId = consanaOrgId;
    }

    public Organization(Long id, String consanaOrgId, Boolean isConsanaIntegrationEnabled) {
        this(id, consanaOrgId);
        this.isConsanaIntegrationEnabled = isConsanaIntegrationEnabled;
    }

    public Boolean getConsanaIntegrationEnabled() {
        return isConsanaIntegrationEnabled;
    }

    public void setConsanaIntegrationEnabled(Boolean consanaIntegrationEnabled) {
        isConsanaIntegrationEnabled = consanaIntegrationEnabled;
    }

    public Boolean getInactive() {
        return isInactive;
    }

    public void setInactive(Boolean inactive) {
        isInactive = inactive;
    }

    public String getConsanaOrgId() {
        return consanaOrgId;
    }

    public void setConsanaOrgId(String consanaOrgId) {
        this.consanaOrgId = consanaOrgId;
    }
}
