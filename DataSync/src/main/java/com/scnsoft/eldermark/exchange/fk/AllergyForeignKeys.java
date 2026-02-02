package com.scnsoft.eldermark.exchange.fk;

public class AllergyForeignKeys implements ResidentIdAware {
    private Long facilityOrganizationId;
    private Long residentId;
    private Long allergyCodeId;
    private Long allergyTypeCodeId;
    private Long observationStatusCodeId;
    private Long severityCodeId;

    public Long getFacilityOrganizationId() {
        return facilityOrganizationId;
    }

    public void setFacilityOrganizationId(Long facilityOrganizationId) {
        this.facilityOrganizationId = facilityOrganizationId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getAllergyCodeId() {
        return allergyCodeId;
    }

    public void setAllergyCodeId(Long allergyCodeId) {
        this.allergyCodeId = allergyCodeId;
    }

	public Long getAllergyTypeCodeId() {
		return allergyTypeCodeId;
	}

	public void setAllergyTypeCodeId(Long allergyTypeCodeId) {
		this.allergyTypeCodeId = allergyTypeCodeId;
	}

	public Long getObservationStatusCodeId() {
		return observationStatusCodeId;
	}

	public void setObservationStatusCodeId(Long observationStatusCodeId) {
		this.observationStatusCodeId = observationStatusCodeId;
	}

	public Long getSeverityCodeId() {
		return severityCodeId;
	}

	public void setSeverityCodeId(Long severityCodeId) {
		this.severityCodeId = severityCodeId;
	}
	
}