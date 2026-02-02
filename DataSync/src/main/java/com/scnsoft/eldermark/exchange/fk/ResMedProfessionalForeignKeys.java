package com.scnsoft.eldermark.exchange.fk;


public class ResMedProfessionalForeignKeys {

	private Long residentId;
	
	private Long facilityOrganizationId;
	
	private Long medProfessionalId;
	
	private Long medicalProfessionalRoleId;

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

	public Long getMedProfessionalId() {
		return medProfessionalId;
	}

	public void setMedProfessionalId(Long medProfessionalId) {
		this.medProfessionalId = medProfessionalId;
	}

	public Long getMedicalProfessionalRoleId() {
		return medicalProfessionalRoleId;
	}

	public void setMedicalProfessionalRoleId(Long medicalProfessionalRoleId) {
		this.medicalProfessionalRoleId = medicalProfessionalRoleId;
	}
	
}
