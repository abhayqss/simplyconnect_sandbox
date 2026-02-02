package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ResidentPaySourceHistory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
public class PaySourceHistory extends LegacyIdAwareEntity {
    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "pay_source")
    private String paySource;

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

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }
}
