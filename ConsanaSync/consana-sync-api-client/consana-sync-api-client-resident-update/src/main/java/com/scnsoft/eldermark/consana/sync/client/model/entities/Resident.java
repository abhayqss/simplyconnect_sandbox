package com.scnsoft.eldermark.consana.sync.client.model.entities;

import com.scnsoft.eldermark.consana.sync.common.entity.HieConsentPolicyType;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "resident")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "databaseAndFacilityJoins", attributeNodes = {
                @NamedAttributeNode("database"),
                @NamedAttributeNode("facility"),
        })
})
public class Resident extends BaseReadOnlyEntity {

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Database database;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    @Column(name = "consana_xref_id")
    private String consanaXrefId;

    // (DE)ACTIVATED in Care Coordination
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "opt_out")
    private Boolean isOptOut;

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @Column(name = "hp_member_identifier")
    private String healthPartnersMemberIdentifier;

    @OneToMany(mappedBy = "resident", fetch = FetchType.LAZY)
    private List<AdmittanceHistory> admittanceHistories;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_type", nullable = false)
    private HieConsentPolicyType hieConsentPolicyType;


    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

    public String getConsanaXrefId() {
        return consanaXrefId;
    }

    public void setConsanaXrefId(String consanaXrefId) {
        this.consanaXrefId = consanaXrefId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getOptOut() {
        return isOptOut;
    }

    public void setOptOut(Boolean optOut) {
        isOptOut = optOut;
    }

    public Instant getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Instant admitDate) {
        this.admitDate = admitDate;
    }

    public Instant getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Instant dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getHealthPartnersMemberIdentifier() {
        return healthPartnersMemberIdentifier;
    }

    public void setHealthPartnersMemberIdentifier(String healthPartnersMemberIdentifier) {
        this.healthPartnersMemberIdentifier = healthPartnersMemberIdentifier;
    }

    public List<AdmittanceHistory> getAdmittanceHistories() {
        return admittanceHistories;
    }

    public void setAdmittanceHistories(List<AdmittanceHistory> admittanceHistories) {
        this.admittanceHistories = admittanceHistories;
    }

    public HieConsentPolicyType getHieConsentPolicyType() {
        return hieConsentPolicyType;
    }

    public void setHieConsentPolicyType(HieConsentPolicyType hieConsentPolicyType) {
        this.hieConsentPolicyType = hieConsentPolicyType;
    }

    @Override
    public String toString() {
        return "Resident{" +
                "id=" + getId() +
                ", database=" + database +
                ", facility=" + facility +
                '}';
    }
}
