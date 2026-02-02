package com.scnsoft.eldermark.consana.sync.client.entities;

import javax.persistence.*;

@Entity
@Table(name = "resident")
public class Resident extends BaseReadOnlyEntity {

    @Column(name = "facility_id")
    private Long facilityId;

    public Long getFacilityId() {
        return facilityId;
    }

    public Resident setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    @Override
    public String toString() {
        return "Resident{" +
                "id=" + getId() +
                ", facilityId=" + facilityId +
                '}';
    }
}
