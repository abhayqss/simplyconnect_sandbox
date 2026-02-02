package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ResidentNotes extends BasicEntity {
    @ManyToOne
    @JoinColumn(name="resident_id")
    public Resident resident;

    @Column(name = "note", columnDefinition="text")
    private String note;

    @Temporal(TemporalType.DATE)
    @Column(name = "note_start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "note_end_date")
    private Date endDate;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        Date now = new Date();
        return startDate != null && now.after(startDate) && endDate == null;
    }
}
