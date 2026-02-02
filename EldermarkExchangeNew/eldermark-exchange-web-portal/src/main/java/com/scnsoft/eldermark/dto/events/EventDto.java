package com.scnsoft.eldermark.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.security.projection.dto.EventSecurityFieldsAware;
import com.scnsoft.eldermark.dto.adt.segment.*;
import com.scnsoft.eldermark.dto.basic.PersonDto;
import com.scnsoft.eldermark.dto.event.EventViewData;

import javax.validation.Valid;
import java.util.List;

public class EventDto implements EventSecurityFieldsAware, EventViewData<ClientSummaryDto,
        EventEssentialsDto,
        EventDescriptionDto,
        PhysicianDto,
        HospitalDto,
        TreatmentDto,
        PersonDto,
        PatientVisitDto> {

    private Long id;
    private ClientSummaryDto client;

    private EventEssentialsDto essentials;

    @Valid
    private EventDescriptionDto description;

    @Valid
    private TreatmentDto treatment;

    private boolean hasResponsibleManager;

    @Valid
    private PersonDto responsibleManager;

    private boolean hasRegisteredNurse;

    @Valid
    private PersonDto registeredNurse;

    private PatientVisitDto patientVisit;

    private boolean canHaveIncidentReport;
    private boolean canAddIncidentReport;
    private boolean canViewIncidentReport;
    private Long incidentReportId;

    private List<AdtInsuranceDto> insurances;
    private List<AdtGuarantorDto> guarantors;
    private List<AdtProcedureDto> procedures;
    private List<AdtDiagnosisDto> diagnoses;
    private List<AdtAllergyDto> allergies;

    private boolean canViewClient;

    private Long assessmentId;
    private String assessmentTypeName;
    private boolean canViewAssessment;

    private EventDocumentSignatureDto documentSignature;

    private boolean canViewAppointment;
    private Long appointmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ClientSummaryDto getClient() {
        return client;
    }

    @Override
    public void setClient(ClientSummaryDto client) {
        this.client = client;
    }

    @Override
    public EventEssentialsDto getEssentials() {
        return essentials;
    }

    @Override
    public void setEssentials(EventEssentialsDto essentials) {
        this.essentials = essentials;
    }

    @Override
    public EventDescriptionDto getDescription() {
        return description;
    }

    @Override
    public void setDescription(EventDescriptionDto description) {
        this.description = description;
    }

    @Override
    public TreatmentDto getTreatment() {
        return treatment;
    }

    @Override
    public void setTreatment(TreatmentDto treatment) {
        this.treatment = treatment;
    }

    public boolean getHasResponsibleManager() {
        return hasResponsibleManager;
    }

    public void setHasResponsibleManager(boolean hasResponsibleManager) {
        this.hasResponsibleManager = hasResponsibleManager;
    }

    @Override
    public PersonDto getResponsibleManager() {
        return responsibleManager;
    }

    @Override
    public void setResponsibleManager(PersonDto responsibleManager) {
        this.responsibleManager = responsibleManager;
    }

    public boolean getHasRegisteredNurse() {
        return hasRegisteredNurse;
    }

    public void setHasRegisteredNurse(boolean hasRegisteredNurse) {
        this.hasRegisteredNurse = hasRegisteredNurse;
    }

    @Override
    public PersonDto getRegisteredNurse() {
        return registeredNurse;
    }

    @Override
    public void setRegisteredNurse(PersonDto registeredNurse) {
        this.registeredNurse = registeredNurse;
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
    public void setDiagnoses(List<AdtDiagnosisDto> diagnosis) {
        this.diagnoses = diagnosis;
    }

    @Override
    public List<AdtAllergyDto> getAllergies() {
        return allergies;
    }

    @Override
    public void setAllergies(List<AdtAllergyDto> allergies) {
        this.allergies = allergies;
    }

    @Override
    public PatientVisitDto getPatientVisit() {
        return patientVisit;
    }

    @Override
    public void setPatientVisit(PatientVisitDto clientVisit) {
        this.patientVisit = clientVisit;
    }

    public boolean getCanHaveIncidentReport() {
        return canHaveIncidentReport;
    }

    public void setCanHaveIncidentReport(boolean canHaveIncidentReport) {
        this.canHaveIncidentReport = canHaveIncidentReport;
    }

    public boolean getCanAddIncidentReport() {
        return canAddIncidentReport;
    }

    public void setCanAddIncidentReport(boolean canCreateIncidentReport) {
        this.canAddIncidentReport = canCreateIncidentReport;
    }

    public boolean getCanViewIncidentReport() {
        return canViewIncidentReport;
    }

    public void setCanViewIncidentReport(boolean canViewIncidentReport) {
        this.canViewIncidentReport = canViewIncidentReport;
    }

    public Long getIncidentReportId() {
        return incidentReportId;
    }

    public void setIncidentReportId(Long incidentReportId) {
        this.incidentReportId = incidentReportId;
    }

    public boolean getCanViewClient() {
        return canViewClient;
    }

    public void setCanViewClient(boolean canViewClient) {
        this.canViewClient = canViewClient;
    }

    @Override
    @JsonIgnore
    public Long getClientId() {
        return getClient() != null ? getClient().getId() : null;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentTypeName() {
        return assessmentTypeName;
    }

    public void setAssessmentTypeName(String assessmentTypeName) {
        this.assessmentTypeName = assessmentTypeName;
    }

    public boolean getCanViewAssessment() {
        return canViewAssessment;
    }

    public void setCanViewAssessment(boolean canViewAssessment) {
        this.canViewAssessment = canViewAssessment;
    }

    public EventDocumentSignatureDto getDocumentSignature() {
        return documentSignature;
    }

    public void setDocumentSignature(EventDocumentSignatureDto documentSignature) {
        this.documentSignature = documentSignature;
    }

    @Override
    public Long getEventTypeId() {
        return getEssentials().getTypeId();
    }

    public boolean getCanViewAppointment() {
        return canViewAppointment;
    }

    public void setCanViewAppointment(boolean canViewAppointment) {
        this.canViewAppointment = canViewAppointment;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}
