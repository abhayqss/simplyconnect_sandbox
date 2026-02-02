package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "AdmitIntakeResidentDate")
@Immutable
public class AdmitIntakeResidentDate {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "resident_id")
    private Long residentId;

    @Column(name = "admit_intake_date")
    private Date admitIntakeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Date getAdmitIntakeDate() {
        return admitIntakeDate;
    }

    public void setAdmitIntakeDate(Date admitIntakeDate) {
        this.admitIntakeDate = admitIntakeDate;
    }
}
