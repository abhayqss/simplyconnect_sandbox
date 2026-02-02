package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ResidentOrder extends BasicEntity {
    @ManyToOne
    @JoinColumn(name="resident_id")
    public Resident resident;

    @Column(name = "order_name", columnDefinition="text")
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "order_start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "order_end_date")
    private Date endDate;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
