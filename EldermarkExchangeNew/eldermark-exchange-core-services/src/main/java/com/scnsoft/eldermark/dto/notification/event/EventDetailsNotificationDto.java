package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.adt.segment.*;
import com.scnsoft.eldermark.dto.event.EventViewData;

import java.util.List;

public class EventDetailsNotificationDto implements EventViewData<ClientInfoNotificationDto,
        EventEssentialsNotificationDto,
        EventDescriptionNotificationDto,
        TreatingPhysicianMailDto,
        TreatingHospitalMailDto,
        TreatmentDetailsNotificationDto,
        PersonNotificationDto,
        PatientVisitNotificationDto> {

    private ClientInfoNotificationDto client;
    private EventEssentialsNotificationDto essentials;
    private EventDescriptionNotificationDto description;
    private TreatmentDetailsNotificationDto treatment;
    private PersonNotificationDto responsibleManager;
    private PersonNotificationDto registeredNurse;
    private PatientVisitNotificationDto patientVisit;
    private List<AdtInsuranceDto> insurances;
    private List<AdtGuarantorDto> guarantors;
    private List<AdtProcedureDto> procedures;
    private List<AdtDiagnosisDto> diagnoses;
    private List<AdtAllergyDto> allergies;

    @Override
    public ClientInfoNotificationDto getClient() {
        return client;
    }

    @Override
    public void setClient(ClientInfoNotificationDto client) {
        this.client = client;
    }

    @Override
    public EventEssentialsNotificationDto getEssentials() {
        return essentials;
    }

    @Override
    public void setEssentials(EventEssentialsNotificationDto essentials) {
        this.essentials = essentials;
    }

    @Override
    public EventDescriptionNotificationDto getDescription() {
        return description;
    }

    @Override
    public void setDescription(EventDescriptionNotificationDto description) {
        this.description = description;
    }

    @Override
    public TreatmentDetailsNotificationDto getTreatment() {
        return treatment;
    }

    @Override
    public void setTreatment(TreatmentDetailsNotificationDto treatment) {
        this.treatment = treatment;
    }

    @Override
    public PersonNotificationDto getResponsibleManager() {
        return responsibleManager;
    }

    @Override
    public void setResponsibleManager(PersonNotificationDto responsibleManager) {
        this.responsibleManager = responsibleManager;
    }

    @Override
    public PersonNotificationDto getRegisteredNurse() {
        return registeredNurse;
    }

    @Override
    public void setRegisteredNurse(PersonNotificationDto registeredNurse) {
        this.registeredNurse = registeredNurse;
    }

    @Override
    public PatientVisitNotificationDto getPatientVisit() {
        return patientVisit;
    }

    @Override
    public void setPatientVisit(PatientVisitNotificationDto patientVisit) {
        this.patientVisit = patientVisit;
    }

    @Override
    public List<AdtInsuranceDto> getInsurances() {
        return insurances;
    }

    @Override
    public void setInsurances(List<AdtInsuranceDto> insurances) {
        this.insurances = insurances;
    }

    @Override
    public List<AdtGuarantorDto> getGuarantors() {
        return guarantors;
    }

    @Override
    public void setGuarantors(List<AdtGuarantorDto> guarantors) {
        this.guarantors = guarantors;
    }

    @Override
    public List<AdtProcedureDto> getProcedures() {
        return procedures;
    }

    @Override
    public void setProcedures(List<AdtProcedureDto> procedures) {
        this.procedures = procedures;
    }

    @Override
    public List<AdtDiagnosisDto> getDiagnoses() {
        return diagnoses;
    }

    @Override
    public void setDiagnoses(List<AdtDiagnosisDto> diagnoses) {
        this.diagnoses = diagnoses;
    }

    @Override
    public List<AdtAllergyDto> getAllergies() {
        return allergies;
    }

    @Override
    public void setAllergies(List<AdtAllergyDto> allergies) {
        this.allergies = allergies;
    }
}
