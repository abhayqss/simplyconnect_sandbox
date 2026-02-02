package com.scnsoft.eldermark.exchange.fk;

public class MedProviderScheduleForeignKeys {

	private Long medProviderId;
    private Long checkedOutByEmpId;
    
	public Long getMedProviderId() {
		return medProviderId;
	}
	public void setMedProviderId(Long medProviderId) {
		this.medProviderId = medProviderId;
	}
	public Long getCheckedOutByEmpId() {
		return checkedOutByEmpId;
	}
	public void setCheckedOutByEmpId(Long checkedOutByEmpId) {
		this.checkedOutByEmpId = checkedOutByEmpId;
	}

}
