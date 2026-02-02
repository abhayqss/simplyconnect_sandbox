package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DLD_DischargeLocation")
public class DLDDischargeLocation {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //@Nationalized
    @Column(name = "discharge_location")
    private String dischargeLocation;

    @Column(name = "effective_date")
    private Date effectiveDate;

    public DLDDischargeLocation() {
    }

    public DLDDischargeLocation(String dischargeLocation, Date effectiveDate) {
        this.dischargeLocation = dischargeLocation;
        this.effectiveDate = effectiveDate;
    }

    public String getDischargeLocation() {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation) {
        this.dischargeLocation = dischargeLocation;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
