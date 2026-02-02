package com.scnsoft.eldermark.exchange.fk;

public class ResIncidentForeignKeys {

	private Long residentId;

	private Long facilityOrganizationId;
	
	private Long personCompletingReportId;
	
	private Long signHlthSrvsDirId;
	
	private Long signExecutiveDirectorId;

	public Long getResidentId() {
		return residentId;
	}

	public void setResidentId(Long residentId) {
		this.residentId = residentId;
	}

	public Long getFacilityOrganizationId() {
		return facilityOrganizationId;
	}

	public void setFacilityOrganizationId(Long facilityOrganizationId) {
		this.facilityOrganizationId = facilityOrganizationId;
	}

	public Long getPersonCompletingReportId() {
		return personCompletingReportId;
	}

	public void setPersonCompletingReportId(Long personCompletingReportId) {
		this.personCompletingReportId = personCompletingReportId;
	}

	public Long getSignHlthSrvsDirId() {
		return signHlthSrvsDirId;
	}

	public void setSignHlthSrvsDirId(Long signHlthSrvsDirId) {
		this.signHlthSrvsDirId = signHlthSrvsDirId;
	}

	public Long getSignExecutiveDirectorId() {
		return signExecutiveDirectorId;
	}

	public void setSignExecutiveDirectorId(Long signExecutiveDirectorId) {
		this.signExecutiveDirectorId = signExecutiveDirectorId;
	}
	
}
