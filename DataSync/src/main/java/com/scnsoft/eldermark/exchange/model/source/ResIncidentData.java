package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResIncidentData.TABLE_NAME)
public class ResIncidentData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Res_Incident";
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
	
	@Column("Location_of_Incident_General")
	private String locationOfIncidentGeneral;
	
	@Column("Location_of_Incident_Specific")
	private String locationOfIncidentSpecific;
	
	@Column("Witness_YN")
	private String witnessYn;
	
	@Column("Injuires_YN")
	private String injuiresYn;
	
	@Column("Notify_EmergSrvs_YN")
	private String notifyEmergSrvsYn;
	
	@Column("Notify_EmergSrvs_Time")
	private String notifyEmergSrvsTime;
	
	@Column("Notify_EmergSrvs_ArrivedAtTime")
	private String notifyEmergSrvsArrivedAtTime;
	
	@Column("Received_Medical_Care_YN")
	private String receivedMedicalCareYn;

	@Column("Person_Completing_Report_Name")
	private String personCompletingReportName;
	
	@Column("Person_Completing_Report_ID")
	private String personCompletingReportId;
	
	@Column("Person_Completing_Report_Date")
	private Date personCompletingReportDate;
	
	@Column("Person_Completing_Report_Time")
	private String personCompletingReportTime;
	
	@Column("Person_Completing_Report_Sign")
	private Boolean personCompletingReportSign;
	
	@Column("Contrib_Factors_Environmental")
	private String contribFactorsEnvironmental;
	
	@Column("Contrib_Factors_Resident")
	private String contribFactorsResident;
	
	@Column("Contrib_Factors_Medical")
	private String contribFactorsMedical;
	
	@Column("Sign_HlthSrvsDir_Name")
	private String signHlthSrvsDirName;
	
	@Column("Sign_HlthSrvsDir_ID")
	private String signHlthSrvsDirId;
	
	@Column("Sign_HlthSrvsDir_Date")
	private Date signHlthSrvsDirDate;
	
	@Column("Sign_HlthSrvsDir_Time")
	private String signHlthSrvsDirTime;
	
	@Column("_Sign_HlthSrvsDir_Signed")
	private Boolean signHlthSrvsDirSigned;
	
	@Column("Sign_HlthSrvsDir_Logged_YN")
	private String signHlthSrvsDirLoggedYn;
	
	@Column("Sign_ExecutiveDirector_Name")
	private String signExecutiveDirectorName;
	
	@Column("Sign_ExecutiveDirector_ID")
	private String signExecutiveDirectorId;
	
	@Column("Sign_ExecutiveDirector_Date")
	private Date signExecutiveDirectorDate;
	
	@Column("Sign_ExecutiveDirector_Time")
	private String signExecutiveDirectorTime;
	
	@Column("_Sign_ExecutiveDirector_Signed")
	private Boolean signExecutiveDirectorSigned;
	
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

	public String getLocationOfIncidentGeneral() {
		return locationOfIncidentGeneral;
	}

	public void setLocationOfIncidentGeneral(String locationOfIncidentGeneral) {
		this.locationOfIncidentGeneral = locationOfIncidentGeneral;
	}

	public String getLocationOfIncidentSpecific() {
		return locationOfIncidentSpecific;
	}

	public void setLocationOfIncidentSpecific(String locationOfIncidentSpecific) {
		this.locationOfIncidentSpecific = locationOfIncidentSpecific;
	}

	public String getWitnessYn() {
		return witnessYn;
	}

	public void setWitnessYn(String witnessYn) {
		this.witnessYn = witnessYn;
	}

	public String getInjuiresYn() {
		return injuiresYn;
	}

	public void setInjuiresYn(String injuiresYn) {
		this.injuiresYn = injuiresYn;
	}

	public String getNotifyEmergSrvsYn() {
		return notifyEmergSrvsYn;
	}

	public void setNotifyEmergSrvsYn(String notifyEmergSrvsYn) {
		this.notifyEmergSrvsYn = notifyEmergSrvsYn;
	}

	public String getNotifyEmergSrvsTime() {
		return notifyEmergSrvsTime;
	}

	public void setNotifyEmergSrvsTime(String notifyEmergSrvsTime) {
		this.notifyEmergSrvsTime = notifyEmergSrvsTime;
	}

	public String getNotifyEmergSrvsArrivedAtTime() {
		return notifyEmergSrvsArrivedAtTime;
	}

	public void setNotifyEmergSrvsArrivedAtTime(String notifyEmergSrvsArrivedAtTime) {
		this.notifyEmergSrvsArrivedAtTime = notifyEmergSrvsArrivedAtTime;
	}

	public String getReceivedMedicalCareYn() {
		return receivedMedicalCareYn;
	}

	public void setReceivedMedicalCareYn(String receivedMedicalCareYn) {
		this.receivedMedicalCareYn = receivedMedicalCareYn;
	}

	public String getPersonCompletingReportName() {
		return personCompletingReportName;
	}

	public void setPersonCompletingReportName(String personCompletingReportName) {
		this.personCompletingReportName = personCompletingReportName;
	}

	public String getPersonCompletingReportId() {
		return personCompletingReportId;
	}

	public void setPersonCompletingReportId(String personCompletingReportId) {
		this.personCompletingReportId = personCompletingReportId;
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

	public Boolean getPersonCompletingReportSign() {
		return personCompletingReportSign;
	}

	public void setPersonCompletingReportSign(Boolean personCompletingReportSign) {
		this.personCompletingReportSign = personCompletingReportSign;
	}

	public String getContribFactorsEnvironmental() {
		return contribFactorsEnvironmental;
	}

	public void setContribFactorsEnvironmental(String contribFactorsEnvironmental) {
		this.contribFactorsEnvironmental = contribFactorsEnvironmental;
	}

	public String getContribFactorsResident() {
		return contribFactorsResident;
	}

	public void setContribFactorsResident(String contribFactorsResident) {
		this.contribFactorsResident = contribFactorsResident;
	}

	public String getContribFactorsMedical() {
		return contribFactorsMedical;
	}

	public void setContribFactorsMedical(String contribFactorsMedical) {
		this.contribFactorsMedical = contribFactorsMedical;
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

	public Boolean getSignHlthSrvsDirSigned() {
		return signHlthSrvsDirSigned;
	}

	public void setSignHlthSrvsDirSigned(Boolean signHlthSrvsDirSigned) {
		this.signHlthSrvsDirSigned = signHlthSrvsDirSigned;
	}

	public String getSignHlthSrvsDirLoggedYn() {
		return signHlthSrvsDirLoggedYn;
	}

	public void setSignHlthSrvsDirLoggedYn(String signHlthSrvsDirLoggedYn) {
		this.signHlthSrvsDirLoggedYn = signHlthSrvsDirLoggedYn;
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

	public Boolean getSignExecutiveDirectorSigned() {
		return signExecutiveDirectorSigned;
	}

	public void setSignExecutiveDirectorSigned(Boolean signExecutiveDirectorSigned) {
		this.signExecutiveDirectorSigned = signExecutiveDirectorSigned;
	}
	
}
