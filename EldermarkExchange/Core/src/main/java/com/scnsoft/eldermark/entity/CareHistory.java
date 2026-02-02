package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CareHistory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
public class CareHistory extends LegacyIdAwareEntity {
    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

	@ManyToOne
    @JoinColumn(name = "resident_id")
	private Resident resident;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
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
}
