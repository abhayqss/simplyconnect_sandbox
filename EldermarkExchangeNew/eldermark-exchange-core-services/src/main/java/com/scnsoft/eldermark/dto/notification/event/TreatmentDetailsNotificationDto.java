package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.event.TreatmentViewData;

public class TreatmentDetailsNotificationDto implements TreatmentViewData<TreatingPhysicianMailDto, TreatingHospitalMailDto> {

    private TreatingPhysicianMailDto physician;
    private TreatingHospitalMailDto hospital;

    @Override
    public TreatingPhysicianMailDto getPhysician() {
        return physician;
    }

    @Override
    public void setPhysician(TreatingPhysicianMailDto physician) {
        this.physician = physician;
    }

    @Override
    public TreatingHospitalMailDto getHospital() {
        return hospital;
    }

    @Override
    public void setHospital(TreatingHospitalMailDto hospital) {
        this.hospital = hospital;
    }
}
