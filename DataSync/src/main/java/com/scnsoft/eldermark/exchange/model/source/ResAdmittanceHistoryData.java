package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.sql.Date;
import java.sql.Time;

@Table(ResAdmittanceHistoryData.TABLE_NAME)
public class ResAdmittanceHistoryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "res_admittance_history";
    public static final String UNIQUE_ID = "Unique_ID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Admit_Date")
    private Date admitDate;

    @Column("Discharge_Date")
    private Date dischargeDate;

    @Column("archive_date")	
    private Date archiveDate;	
    
    @Column("Deposit_Date")
    private Date depositDate;

    @Column("Rental_Agreement_Date")
    private Date rentalAgreementDate;

    @Column("SalesRep_Employee_ID")
    private String salesRepEmployeeId;

    @Column("Reserved_From_Date")
    private Date reservedFromDate;

    @Column("Reserved_To_Date")
    private Date reservedToDate;

    @Column("Admit_Sequence")
    private Integer admitSequence;

    @Column("Admit_Facility_Sequence")
    private Integer admitFacilitySequence;

    @Column("Facility")
    private String facility;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Unit_Number")
    private String unitNumber;

    @Column("County_Admitted_From")
    private String countyAdmittedFrom;

    @Column("Admit_Time")
    private Time admitTime;

    @Column("Discharge_Time")
    private Time dischargeTime;

    @Column("Prev_Living_Status_ID")
    private Long prevLivingStatus;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }
    
	public Date getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}

	public Date getRentalAgreementDate() {
		return rentalAgreementDate;
	}

    public void setRentalAgreementDate(Date rentalAgreementDate) {
        this.rentalAgreementDate = rentalAgreementDate;
    }

    public String getSalesRepEmployeeId() {
        return salesRepEmployeeId;
    }

    public void setSalesRepEmployeeId(String salesRepEmployeeId) {
        this.salesRepEmployeeId = salesRepEmployeeId;
    }

    public Date getReservedFromDate() {
        return reservedFromDate;
    }

    public void setReservedFromDate(Date reservedFromDate) {
        this.reservedFromDate = reservedFromDate;
    }

    public Date getReservedToDate() {
        return reservedToDate;
    }

    public void setReservedToDate(Date reservedToDate) {
        this.reservedToDate = reservedToDate;
    }

    public Integer getAdmitSequence() {
        return admitSequence;
    }

    public void setAdmitSequence(Integer admitSequence) {
        this.admitSequence = admitSequence;
    }

    public Integer getAdmitFacilitySequence() {
        return admitFacilitySequence;
    }

    public void setAdmitFacilitySequence(Integer admitFacilitySequence) {
        this.admitFacilitySequence = admitFacilitySequence;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Time getAdmitTime() {
        return admitTime;
    }

    public void setAdmitTime(Time admitTime) {
        this.admitTime = admitTime;
    }

    public Time getDischargeTime() {
        return dischargeTime;
    }

    public void setDischargeTime(Time dischargeTime) {
        this.dischargeTime = dischargeTime;
    }

    public String getCountyAdmittedFrom() {
        return countyAdmittedFrom;
    }

    public void setCountyAdmittedFrom(String countyAdmittedFrom) {
        this.countyAdmittedFrom = countyAdmittedFrom;
    }

    public Long getPrevLivingStatus() {
        return prevLivingStatus;
    }

    public void setPrevLivingStatus(Long prevLivingStatus) {
        this.prevLivingStatus = prevLivingStatus;
    }
}
