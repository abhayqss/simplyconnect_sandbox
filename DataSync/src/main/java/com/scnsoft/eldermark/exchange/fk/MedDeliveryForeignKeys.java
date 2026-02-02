package com.scnsoft.eldermark.exchange.fk;

public class MedDeliveryForeignKeys {

	private Long residentId;
	
	private Long facilityOrganizationId;
	
	private Long medicationId;
	
	private Long givenOrRecordedPersonId;

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

	public Long getMedicationId() {
		return medicationId;
	}

	public void setMedicationId(Long medicationId) {
		this.medicationId = medicationId;
	}

	public Long getGivenOrRecordedPersonId() {
		return givenOrRecordedPersonId;
	}

	public void setGivenOrRecordedPersonId(Long givenOrRecordedPersonId) {
		this.givenOrRecordedPersonId = givenOrRecordedPersonId;
	}

}
