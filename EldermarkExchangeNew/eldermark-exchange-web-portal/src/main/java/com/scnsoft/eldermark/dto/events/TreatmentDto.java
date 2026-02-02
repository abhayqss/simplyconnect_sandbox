package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.event.TreatmentViewData;

import javax.validation.Valid;

public class TreatmentDto implements TreatmentViewData<PhysicianDto, HospitalDto> {

    private boolean hasPhysician;

    @Valid
    private PhysicianDto physician;

    private boolean hasHospital;

    @Valid
    private HospitalDto hospital;

    @Override
    public PhysicianDto getPhysician() {
        return physician;
    }

    @Override
    public void setPhysician(PhysicianDto physician) {
        this.physician = physician;
    }

    @Override
    public HospitalDto getHospital() {
        return hospital;
    }

    @Override
    public void setHospital(HospitalDto hospital) {
        this.hospital = hospital;
    }

    public boolean getHasPhysician() {
        return hasPhysician;
    }

    public void setHasPhysician(boolean hasPhysician) {
        this.hasPhysician = hasPhysician;
    }

    public boolean getHasHospital() {
        return hasHospital;
    }

    public void setHasHospital(boolean hasHospital) {
        this.hasHospital = hasHospital;
    }

}
