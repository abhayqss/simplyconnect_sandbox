package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;
import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedDeliveryData.TABLE_NAME)
public class MedDeliveryData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Med_Delivery";
	public static final String ID_COLUMN = "Med_Delivery_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Facility")
	private String facility;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Res_Med_ID")
	private Long resMedId;
	
	@Column("Scheduled_Date")
	private Date scheduledDate;
	
	@Column("Scheduled_Time")
	private Time scheduledTime;
	
	@Column("Given")
	private Boolean given;
	
	@Column("On_Hold")
	private Boolean onHold;
	
	@Column("Not_Given_Reason")
	private String notGivenReason;
	
	@Column("PRN")
	private Boolean prn;

	@Column("Scheduled_Latest_When")
	private Long scheduledLatestWhen;
	
	@Column("Attempts_Last_When")
	private Long attemptsLastWhen;
	
	@Column("Poured_When")
	private Long pouredWhen;
	
	@Column("Scheduled_Earliest_When")
	private Long scheduledEarliestWhen;

	@Column("Given_or_Recorded_Person_ID")
	private String givenOrRecordedPersonId;
	
	@Column("PRN_Reason_Given")
	private String prnReasonGiven;
	
	@Column("PRN_Results")
	private String prnResults;
	
	@Column("Given_or_Recorded_Date")
	private Date givenOrRecordedDate;
	
	@Column("Given_or_Recorded_Time")
	private Time givenOrRecordedTime;
	
	@Override
	public Long getId() {
		return id;
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

	public Long getResMedId() {
		return resMedId;
	}

	public void setResMedId(Long resMedId) {
		this.resMedId = resMedId;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Time getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Time scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getGiven() {
		return given;
	}

	public void setGiven(Boolean given) {
		this.given = given;
	}

	public Boolean getOnHold() {
		return onHold;
	}

	public void setOnHold(Boolean onHold) {
		this.onHold = onHold;
	}

	public String getNotGivenReason() {
		return notGivenReason;
	}

	public void setNotGivenReason(String notGivenReason) {
		this.notGivenReason = notGivenReason;
	}

	public Boolean getPrn() {
		return prn;
	}

	public void setPrn(Boolean prn) {
		this.prn = prn;
	}

	public Long getScheduledLatestWhen() {
		return scheduledLatestWhen;
	}

	public void setScheduledLatestWhen(Long scheduledLatestWhen) {
		this.scheduledLatestWhen = scheduledLatestWhen;
	}

	public Long getAttemptsLastWhen() {
		return attemptsLastWhen;
	}

	public void setAttemptsLastWhen(Long attemptsLastWhen) {
		this.attemptsLastWhen = attemptsLastWhen;
	}

	public Long getPouredWhen() {
		return pouredWhen;
	}

	public void setPouredWhen(Long pouredWhen) {
		this.pouredWhen = pouredWhen;
	}

	public Long getScheduledEarliestWhen() {
		return scheduledEarliestWhen;
	}

	public void setScheduledEarliestWhen(Long scheduledEarliestWhen) {
		this.scheduledEarliestWhen = scheduledEarliestWhen;
	}

	public String getGivenOrRecordedPersonId() {
		return givenOrRecordedPersonId;
	}

	public void setGivenOrRecordedPersonId(String givenOrRecordedPersonId) {
		this.givenOrRecordedPersonId = givenOrRecordedPersonId;
	}

	public String getPrnReasonGiven() {
		return prnReasonGiven;
	}

	public void setPrnReasonGiven(String prnReasonGiven) {
		this.prnReasonGiven = prnReasonGiven;
	}

	public String getPrnResults() {
		return prnResults;
	}

	public void setPrnResults(String prnResults) {
		this.prnResults = prnResults;
	}

	public Date getGivenOrRecordedDate() {
		return givenOrRecordedDate;
	}

	public void setGivenOrRecordedDate(Date givenOrRecordedDate) {
		this.givenOrRecordedDate = givenOrRecordedDate;
	}

	public Time getGivenOrRecordedTime() {
		return givenOrRecordedTime;
	}

	public void setGivenOrRecordedTime(Time givenOrRecordedTime) {
		this.givenOrRecordedTime = givenOrRecordedTime;
	}
	
}
