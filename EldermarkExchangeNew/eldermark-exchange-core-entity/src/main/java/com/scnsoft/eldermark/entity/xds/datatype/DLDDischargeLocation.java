package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "DLD_DischargeLocation")
public class DLDDischargeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "discharge_location", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String dischargeLocation;

    @Column(name = "effective_date")
    private Instant effectiveDate;

    public DLDDischargeLocation() {
    }

    public DLDDischargeLocation(String dischargeLocation, Instant effectiveDate) {
        this.dischargeLocation = dischargeLocation;
        this.effectiveDate = effectiveDate;
    }

    public String getDischargeLocation() {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation) {
        this.dischargeLocation = dischargeLocation;
    }

    public Instant getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
