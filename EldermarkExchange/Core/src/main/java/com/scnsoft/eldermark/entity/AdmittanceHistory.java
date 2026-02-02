package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ResidentAdmittanceHistory")
public class AdmittanceHistory extends LegacyIdAwareEntity {
    @Column(name = "admit_date")
    private Date admitDate;

    @Column(name = "discharge_date")
    private Date dischargeDate;

    @ManyToOne
    @JoinColumn(name = "prev_living_status_id")
    private LivingStatus livingStatus;
	
	@ManyToOne
    @JoinColumn(name = "resident_id")
	private Resident resident;

    @Column(name = "county_admitted_from")
    private String countyAdmittedFrom;

    @Column(name = "organization_id")
    private Long organizationId;
    
	@Column(name = "archive_date")
	private Date archiveDate;

	public Date getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public LivingStatus getLivingStatus() {
        return livingStatus;
    }

    public void setLivingStatus(LivingStatus livingStatus) {
        this.livingStatus = livingStatus;
    }

    public String getCountyAdmittedFrom() {
        return countyAdmittedFrom;
    }

    public void setCountyAdmittedFrom(String countyAdmittedFrom) {
        this.countyAdmittedFrom = countyAdmittedFrom;
    }

    public Date getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Date admitDate) {
        this.admitDate = admitDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(final Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
