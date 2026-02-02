package com.scnsoft.eldermark.dto.event;

public interface TreatmentViewData<TP extends TreatingPhysicianViewData, TH extends TreatingHospitalViewData> {

    TP getPhysician();

    void setPhysician(TP physician);

    TH getHospital();

    void setHospital(TH hospital);
}
