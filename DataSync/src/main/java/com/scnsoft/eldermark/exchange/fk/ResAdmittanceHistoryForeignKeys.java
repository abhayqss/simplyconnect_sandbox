package com.scnsoft.eldermark.exchange.fk;

public class ResAdmittanceHistoryForeignKeys implements ResidentIdAware {
    private Long salesRepEmployeeId;
    private Long residentId;
    private Long facilityId;
    private Long livingStatusId;

    public Long getSalesRepEmployeeId() {
        return salesRepEmployeeId;
    }

    public void setSalesRepEmployeeId(Long salesRepEmployeeId) {
        this.salesRepEmployeeId = salesRepEmployeeId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getLivingStatusId() {
        return livingStatusId;
    }

    public void setLivingStatusId(Long livingStatusId) {
        this.livingStatusId = livingStatusId;
    }
}
