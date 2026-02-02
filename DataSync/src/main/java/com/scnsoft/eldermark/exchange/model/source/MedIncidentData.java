package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedIncidentData.TABLE_NAME)
public class MedIncidentData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Med_Incident";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Facility")
	private String facility;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Unit_Number")
	private String unitNumber;
	
	@Column("Incident_Date")
	private Date incidentDate;
	
	@Column("Incident_Time")
	private String incidentTime;
	
	@Column("Type_of_Incident")
	private String typeOfIncident;
	
	@Column("Sentinel_Event_YN")
	private String sentinelEventYn;
	
	@Column("Med_Name")
	private String medName;
	
	@Column("Med_Dose")
	private String medDose;
	
	@Column("PersonDiscoverIncident_ID")
	private String personDiscoverIncidentId;
	
	@Column("PersonDiscoverIncident_Name")
	private String personDiscoverIncidentName;
	
	@Column("PersonDiscoverIncident_Title")
	private String personDiscoverIncidentTitle;
	
	@Column("Final_Resident_Outcome")
	private String finalResidentOutcome;
	
	@Column("Person_Completing_Report_Name")
	private String personCompletingReportName;
	
	@Column("Person_Completing_Report_Date")
	private Date personCompletingReportDate;
	
	@Column("Person_Completing_Report_Time")
	private String personCompletingReportTime;
	
	@Column("Possible_Contributing_Factors")
	private String possibleContributingFactors;
	
	@Column("Corrective_Action_Taken")
	private String correctiveActionTaken;
	
	@Column("Sign_HlthSrvsDir_Name")
	private String signHlthSrvsDirName;
	
	@Column("Sign_HlthSrvsDir_ID")
	private String signHlthSrvsDirId;
	
	@Column("Sign_HlthSrvsDir_Date")
	private Date signHlthSrvsDirDate;
	
	@Column("Sign_HlthSrvsDir_Time")
	private String signHlthSrvsDirTime;
	
	@Column("Sign_ExecutiveDirector_Name")
	private String signExecutiveDirectorName;
	
	@Column("Sign_ExecutiveDirector_ID")
	private String signExecutiveDirectorId;
	
	@Column("Sign_ExecutiveDirector_Date")
	private Date signExecutiveDirectorDate;
	
	@Column("Sign_ExecutiveDirector_Time")
	private String signExecutiveDirectorTime;
	
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

	public void setId(long id) {
		this.id = id;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public Date getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(Date incidentDate) {
		this.incidentDate = incidentDate;
	}

	public String getIncidentTime() {
		return incidentTime;
	}

	public void setIncidentTime(String incidentTime) {
		this.incidentTime = incidentTime;
	}

	public String getTypeOfIncident() {
		return typeOfIncident;
	}

	public void setTypeOfIncident(String typeOfIncident) {
		this.typeOfIncident = typeOfIncident;
	}

	public String getSentinelEventYn() {
		return sentinelEventYn;
	}

	public void setSentinelEventYn(String sentinelEventYn) {
		this.sentinelEventYn = sentinelEventYn;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getMedDose() {
		return medDose;
	}

	public void setMedDose(String medDose) {
		this.medDose = medDose;
	}

	public String getPersonDiscoverIncidentId() {
		return personDiscoverIncidentId;
	}

	public void setPersonDiscoverIncidentId(String personDiscoverIncidentId) {
		this.personDiscoverIncidentId = personDiscoverIncidentId;
	}

	public String getPersonDiscoverIncidentName() {
		return personDiscoverIncidentName;
	}

	public void setPersonDiscoverIncidentName(String personDiscoverIncidentName) {
		this.personDiscoverIncidentName = personDiscoverIncidentName;
	}

	public String getPersonDiscoverIncidentTitle() {
		return personDiscoverIncidentTitle;
	}

	public void setPersonDiscoverIncidentTitle(String personDiscoverIncidentTitle) {
		this.personDiscoverIncidentTitle = personDiscoverIncidentTitle;
	}

	public String getFinalResidentOutcome() {
		return finalResidentOutcome;
	}

	public void setFinalResidentOutcome(String finalResidentOutcome) {
		this.finalResidentOutcome = finalResidentOutcome;
	}

	public String getPersonCompletingReportName() {
		return personCompletingReportName;
	}

	public void setPersonCompletingReportName(String personCompletingReportName) {
		this.personCompletingReportName = personCompletingReportName;
	}

	public Date getPersonCompletingReportDate() {
		return personCompletingReportDate;
	}

	public void setPersonCompletingReportDate(Date personCompletingReportDate) {
		this.personCompletingReportDate = personCompletingReportDate;
	}

	public String getPersonCompletingReportTime() {
		return personCompletingReportTime;
	}

	public void setPersonCompletingReportTime(String personCompletingReportTime) {
		this.personCompletingReportTime = personCompletingReportTime;
	}

	public String getPossibleContributingFactors() {
		return possibleContributingFactors;
	}

	public void setPossibleContributingFactors(String possibleContributingFactors) {
		this.possibleContributingFactors = possibleContributingFactors;
	}

	public String getCorrectiveActionTaken() {
		return correctiveActionTaken;
	}

	public void setCorrectiveActionTaken(String correctiveActionTaken) {
		this.correctiveActionTaken = correctiveActionTaken;
	}

	public String getSignHlthSrvsDirName() {
		return signHlthSrvsDirName;
	}

	public void setSignHlthSrvsDirName(String signHlthSrvsDirName) {
		this.signHlthSrvsDirName = signHlthSrvsDirName;
	}

	public String getSignHlthSrvsDirId() {
		return signHlthSrvsDirId;
	}

	public void setSignHlthSrvsDirId(String signHlthSrvsDirId) {
		this.signHlthSrvsDirId = signHlthSrvsDirId;
	}

	public Date getSignHlthSrvsDirDate() {
		return signHlthSrvsDirDate;
	}

	public void setSignHlthSrvsDirDate(Date signHlthSrvsDirDate) {
		this.signHlthSrvsDirDate = signHlthSrvsDirDate;
	}

	public String getSignHlthSrvsDirTime() {
		return signHlthSrvsDirTime;
	}

	public void setSignHlthSrvsDirTime(String signHlthSrvsDirTime) {
		this.signHlthSrvsDirTime = signHlthSrvsDirTime;
	}

	public String getSignExecutiveDirectorName() {
		return signExecutiveDirectorName;
	}

	public void setSignExecutiveDirectorName(String signExecutiveDirectorName) {
		this.signExecutiveDirectorName = signExecutiveDirectorName;
	}

	public String getSignExecutiveDirectorId() {
		return signExecutiveDirectorId;
	}

	public void setSignExecutiveDirectorId(String signExecutiveDirectorId) {
		this.signExecutiveDirectorId = signExecutiveDirectorId;
	}

	public Date getSignExecutiveDirectorDate() {
		return signExecutiveDirectorDate;
	}

	public void setSignExecutiveDirectorDate(Date signExecutiveDirectorDate) {
		this.signExecutiveDirectorDate = signExecutiveDirectorDate;
	}

	public String getSignExecutiveDirectorTime() {
		return signExecutiveDirectorTime;
	}

	public void setSignExecutiveDirectorTime(String signExecutiveDirectorTime) {
		this.signExecutiveDirectorTime = signExecutiveDirectorTime;
	}
	
}
