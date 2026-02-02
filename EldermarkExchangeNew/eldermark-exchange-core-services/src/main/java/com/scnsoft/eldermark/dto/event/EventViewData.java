package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.adt.segment.*;

import java.util.List;

public interface EventViewData<C extends ClientSummaryViewData,
        E extends EventEssentialsViewData,
        D extends EventDescriptionViewData,
        TP extends TreatingPhysicianViewData,
        TH extends TreatingHospitalViewData,
        T extends TreatmentViewData<TP, TH>,
        P extends PersonViewData,
        PV extends PatientVisitViewData> {

    C getClient();

    void setClient(C client);

    E getEssentials();

    void setEssentials(E essentials);

    D getDescription();

    void setDescription(D description);

    T getTreatment();

    void setTreatment(T treatment);

    P getResponsibleManager();

    void setResponsibleManager(P responsibleManager);

    P getRegisteredNurse();

    void setRegisteredNurse(P registeredNurse);

    PV getPatientVisit();

    void setPatientVisit(PV clientVisit);

    List<AdtInsuranceDto> getInsurances();

    void setInsurances(List<AdtInsuranceDto> insurances);

    List<AdtGuarantorDto> getGuarantors();

    void setGuarantors(List<AdtGuarantorDto> guarantors);

    List<AdtProcedureDto> getProcedures();

    void setProcedures(List<AdtProcedureDto> procedures);

    List<AdtDiagnosisDto> getDiagnoses();

    void setDiagnoses(List<AdtDiagnosisDto> diagnosis);

    List<AdtAllergyDto> getAllergies();

    void setAllergies(List<AdtAllergyDto> allergies);
}
