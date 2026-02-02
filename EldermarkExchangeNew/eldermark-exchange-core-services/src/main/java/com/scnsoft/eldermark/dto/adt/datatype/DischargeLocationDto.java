package com.scnsoft.eldermark.dto.adt.datatype;

import java.util.Date;

public class DischargeLocationDto {

    //if renaming of any field is needed - please make sure to do the same changes to #displayDL in eventNotificationSecureEmail.vm
    private String dischargeLocation;
    private Long effectiveDate;

    public String getDischargeLocation() {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation) {
        this.dischargeLocation = dischargeLocation;
    }

    public Long getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Long effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

}
