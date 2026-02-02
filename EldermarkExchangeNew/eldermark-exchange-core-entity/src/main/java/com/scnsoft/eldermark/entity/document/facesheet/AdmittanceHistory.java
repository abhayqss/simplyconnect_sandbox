package com.scnsoft.eldermark.entity.document.facesheet;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.LivingStatus;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentAdmittanceHistory")
public class AdmittanceHistory extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @ManyToOne
    @JoinColumn(name = "prev_living_status_id")
    private LivingStatus livingStatus;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "resident_id", updatable = false, insertable = false)
    private Long clientId;

    @Column(name = "county_admitted_from")
    private String countyAdmittedFrom;

    @Column(name = "organization_id")
    private Long communityId;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LivingStatus getLivingStatus() {
        return livingStatus;
    }

    public void setLivingStatus(LivingStatus livingStatus) {
        this.livingStatus = livingStatus;
    }

    public String getCountyAdmittedFrom() {
        return countyAdmittedFrom;
    }

    public void setCountyAdmittedFrom(String countyAdmittedFrom) {
        this.countyAdmittedFrom = countyAdmittedFrom;
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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
