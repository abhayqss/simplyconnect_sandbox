package com.scnsoft.eldermark.exchange.fk;

public class UnitForeignKeys {
    private Long organizationId;
    private Long unitTypePrivateCurrentId;
    private Long unitTypeSemiPrivateACrntId;
    private Long unitTypeSemiPrivateBCrntId;
    private Long unitStationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUnitTypePrivateCurrentId() {
        return unitTypePrivateCurrentId;
    }

    public void setUnitTypePrivateCurrentId(Long unitTypePrivateCurrentId) {
        this.unitTypePrivateCurrentId = unitTypePrivateCurrentId;
    }

    public Long getUnitTypeSemiPrivateACrntId() {
        return unitTypeSemiPrivateACrntId;
    }

    public void setUnitTypeSemiPrivateACrntId(Long unitTypeSemiPrivateACrntId) {
        this.unitTypeSemiPrivateACrntId = unitTypeSemiPrivateACrntId;
    }

    public Long getUnitTypeSemiPrivateBCrntId() {
        return unitTypeSemiPrivateBCrntId;
    }

    public void setUnitTypeSemiPrivateBCrntId(Long unitTypeSemiPrivateBCrntId) {
        this.unitTypeSemiPrivateBCrntId = unitTypeSemiPrivateBCrntId;
    }

	public Long getUnitStationId() {
		return unitStationId;
	}

	public void setUnitStationId(Long unitStationId) {
		this.unitStationId = unitStationId;
	}
    
}
