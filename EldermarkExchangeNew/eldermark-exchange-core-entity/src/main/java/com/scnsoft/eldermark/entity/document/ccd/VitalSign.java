package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;

@Entity
@Table(name = "VitalSign", indexes = {
        @Index(name = "IX_vitalsign_legacy_id", columnList = "legacy_id"),
        @Index(name = "IX_vitalsign_resident", columnList = "resident_id")
})
public class VitalSign extends StringLegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vitalSign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VitalSignObservation> vitalSignObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public List<VitalSignObservation> getVitalSignObservations() {
        return vitalSignObservations;
    }

    public void setVitalSignObservations(List<VitalSignObservation> vitalSignObservations) {
        this.vitalSignObservations = vitalSignObservations;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
