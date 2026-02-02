package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;
import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResLeaveOfAbsenceData.TABLE_NAME)
public class ResLeaveOfAbsenceData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Res_Leave_of_Absence";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("From_Date")
	private Date fromDate;
	
	@Column("From_Time")
	private Time fromTime;
	
	@Column("To_Date")
	private Date toDate;
	
	@Column("To_Time")
	private Time toTime;
	
	@Column("Reason")
	private String reason;
	
	@Column("Who_Requested")
	private String whoRequested;
	
	@Column("Facility")
	private String facility;
	
	@Column("Last_Updated")
	private Date lastUpdated;
	
	@Column("Last_Updated_By")
	private String lastUpdatedBy;
	
	@Column("From_When")
	private Long fromWhen;
	
	@Column("To_When_Future")
	private Long toWhenFuture;
	
	@Column("Service_On_Hold")
	private Boolean serviceOnHold;
	
	@Column("On_Leave")
	private Boolean onLeave;
	
	@Column("Meds_On_Hold")
	private Boolean medsOnHold;
	
	@Column("PrePour_Meds")
	private Boolean prePourMeds;
	
	@Column("LOA_Reason_ID")
	private Long loaReasonId;
	
	@Column("Hospital_Visit_Location")
	private String hospitalVisitLocation;
	
	@Column("Hospital_Visit_Reason")
	private String hospitalVisitReason;
	
	@Column("Hospital_Visit_Outcome")
	private String hospitalVisitOutcome;
	
	@Column("Bed_Hold_Letter_Sent")
	private Boolean bedHoldLetterSent;
	
	@Column("Hospital_Discharge_Diagnosis")
	private String hospitalDischargeDiagnosis;

	@Override
	public Long getId() {
		return id;
	}

	public Long getResNumber() {
		return resNumber;
	}

	public void setResNumber(Long resNUmber) {
		this.resNumber = resNUmber;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Time getFromTime() {
		return fromTime;
	}

	public void setFromTime(Time fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Time getToTime() {
		return toTime;
	}

	public void setToTime(Time toTime) {
		this.toTime = toTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getWhoRequested() {
		return whoRequested;
	}

	public void setWhoRequested(String whoRequested) {
		this.whoRequested = whoRequested;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Long getFromWhen() {
		return fromWhen;
	}

	public void setFromWhen(Long fromWhen) {
		this.fromWhen = fromWhen;
	}

	public Long getToWhenFuture() {
		return toWhenFuture;
	}

	public void setToWhenFuture(Long toWhenFuture) {
		this.toWhenFuture = toWhenFuture;
	}

	public Boolean getServiceOnHold() {
		return serviceOnHold;
	}

	public void setServiceOnHold(Boolean serviceOnHold) {
		this.serviceOnHold = serviceOnHold;
	}

	public Boolean getOnLeave() {
		return onLeave;
	}

	public void setOnLeave(Boolean onLeave) {
		this.onLeave = onLeave;
	}

	public Boolean getMedsOnHold() {
		return medsOnHold;
	}

	public void setMedsOnHold(Boolean medsOnHold) {
		this.medsOnHold = medsOnHold;
	}

	public Boolean getPrePourMeds() {
		return prePourMeds;
	}

	public void setPrePourMeds(Boolean prePourMeds) {
		this.prePourMeds = prePourMeds;
	}

	public Long getLoaReasonId() {
		return loaReasonId;
	}

	public void setLoaReasonId(Long loaReasonId) {
		this.loaReasonId = loaReasonId;
	}

	public String getHospitalVisitLocation() {
		return hospitalVisitLocation;
	}

	public void setHospitalVisitLocation(String hospitalVisitLocation) {
		this.hospitalVisitLocation = hospitalVisitLocation;
	}

	public String getHospitalVisitReason() {
		return hospitalVisitReason;
	}

	public void setHospitalVisitReason(String hospitalVisitReason) {
		this.hospitalVisitReason = hospitalVisitReason;
	}

	public String getHospitalVisitOutcome() {
		return hospitalVisitOutcome;
	}

	public void setHospitalVisitOutcome(String hospitalVisitOutcome) {
		this.hospitalVisitOutcome = hospitalVisitOutcome;
	}

	public Boolean getBedHoldLetterSent() {
		return bedHoldLetterSent;
	}

	public void setBedHoldLetterSent(Boolean bedHoldLetterSent) {
		this.bedHoldLetterSent = bedHoldLetterSent;
	}

	public String getHospitalDischargeDiagnosis() {
		return hospitalDischargeDiagnosis;
	}

	public void setHospitalDischargeDiagnosis(String hospitalDischargeDiagnosis) {
		this.hospitalDischargeDiagnosis = hospitalDischargeDiagnosis;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
