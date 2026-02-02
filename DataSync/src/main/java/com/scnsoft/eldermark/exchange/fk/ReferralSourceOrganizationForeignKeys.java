package com.scnsoft.eldermark.exchange.fk;

public class ReferralSourceOrganizationForeignKeys {

	private Long referralSourceId;
    private Long organizationId;
    
    public Long getReferralSourceId() {
		return referralSourceId;
	}

	public void setReferralSourceId(Long referralSourceId) {
		this.referralSourceId = referralSourceId;
	}

	public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
}
