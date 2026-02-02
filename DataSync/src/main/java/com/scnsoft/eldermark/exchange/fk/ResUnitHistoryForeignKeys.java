package com.scnsoft.eldermark.exchange.fk;

public class ResUnitHistoryForeignKeys {
    private Long organizationId;
    private Long unitId;
    private Long resAdmittanceHistoryId;
    private Long residentId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getResAdmittanceHistoryId() {
        return resAdmittanceHistoryId;
    }

    public void setResAdmittanceHistoryId(Long resAdmittanceHistoryId) {
        this.resAdmittanceHistoryId = resAdmittanceHistoryId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }
}
