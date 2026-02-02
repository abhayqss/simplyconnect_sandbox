package com.scnsoft.eldermark.shared.carecoordination.adt.datatype;

import java.util.Date;

public class DLDDischargeLocationDto {
    private String dischargeLocation;
    private Date effectiveDate;

    public String getDischargeLocation() {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation) {
        this.dischargeLocation = dischargeLocation;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
