package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "ResPharmacy")
public class ResidentPharmacy extends LegacyIdAwareEntity {

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Organization organization;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
	private Resident resident;

    @Column(name = "rank")
    private Integer rank;

    public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
