package com.scnsoft.eldermark.dump.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentAdmittanceHistory")
public class AdmittanceHistory extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "admit_date")
    private Instant admitDate;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "organization_id")
    private Long communityId;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Instant admitDate) {
        this.admitDate = admitDate;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }
}
