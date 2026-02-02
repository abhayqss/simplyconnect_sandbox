package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ClientPharmacyFilterView")
public class ResidentPharmacyFilterView {

    @EmbeddedId
    private ResidentPharmacyFilterView.Id id;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long residentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Resident resident;

    @Column(name = "pharmacy_name", nullable = false, insertable = false, updatable = false)
    private String pharmacyName;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
        private Long residentId;

        @Column(name = "pharmacy_name", nullable = false, insertable = false, updatable = false)
        private String pharmacyName;

        public Long getResidentId() {
            return residentId;
        }

        public void setResidentId(Long residentId) {
            this.residentId = residentId;
        }

        public String getPharmacyName() {
            return pharmacyName;
        }

        public void setPharmacyName(String pharmacyName) {
            this.pharmacyName = pharmacyName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id id = (Id) o;
            return Objects.equals(getResidentId(), id.getResidentId()) &&
                    Objects.equals(getPharmacyName(), id.getPharmacyName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getResidentId(), getPharmacyName());
        }
    }
}
