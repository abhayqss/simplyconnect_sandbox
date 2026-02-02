package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class OrganizationTelecom extends StringLegacyTableAwareEntity implements Telecom {
    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 100)
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    public String getUseCode() {
        return useCode;
    }

    public void setUseCode(String useCode) {
        this.useCode = useCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

}
