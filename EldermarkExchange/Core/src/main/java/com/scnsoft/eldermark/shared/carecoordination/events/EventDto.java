package com.scnsoft.eldermark.shared.carecoordination.events;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.adt.*;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * This DTO is intended to represent events. [NOTE] `responsible` represents Details of Registered Nurse (RN); `treatingPhysician` represents Details of Treating Physician
 * Created by pzhurba on 01-Oct-15.
 */
public class EventDto implements Serializable {

    private Note note;
    private PatientDto patient;
    private EmployeeDto employee;
    private boolean includeManager;
    private ManagerDto manager;
    private boolean includeResponsible;
    private NameWithAddressDto responsible;
    private EventDetailsDto eventDetails;
    private boolean includeTreatingPhysician;

    private NameWithAddressDto treatingPhysician;
    private boolean includeHospital;
    private HospitalDto treatingHospital;
    private ProcedureDto procedure;
    private PatientVisitDto patientVisit;
    private InsuranceDto insurance;

    private AdtEventDto adtEvent;

    //ADT segments
    private EVNEventTypeSegmentDto adtSegmentEVN;
    private PIDPatientIdentificationSegmentDto adtSegmentPID;
    private PV1PatientVisitSegmentDto adtSegmentPV1;
    private List<DG1DiagnosisSegmentDto> adtSegmentsDG1;
    private List<GT1GuarantorSegmentDto> adtSegmentsGT1;
    private List<PR1ProcedureSegmentDto> adtSegmentsPR1;
    private List<IN1InsuranceSegmentDto> adtSegmentsIN1;
    private List<AL1AllergySegmentDto> adtSegmentsAL1;
    private PD1AdditionalDemographicSegmentDto adtSegmentPD1;

    private List<RelatedNoteItemDto> relatedNotes;
    private Boolean isIrRequired;
    private Long irId;
    private Long irDate;


    public EmployeeDto getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDto employee) {
        this.employee = employee;
    }

    @NotNull
    public boolean isIncludeResponsible() {
        return includeResponsible;
    }

    public void setIncludeResponsible(boolean includeResponsible) {
        this.includeResponsible = includeResponsible;
    }

    public NameWithAddressDto getResponsible() {
        return responsible;
    }

    public void setResponsible(NameWithAddressDto responsible) {
        this.responsible = responsible;
    }

    public EventDetailsDto getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetailsDto eventDetails) {
        this.eventDetails = eventDetails;
    }

    @NotNull
    public boolean isIncludeTreatingPhysician() {
        return includeTreatingPhysician;
    }

    public void setIncludeTreatingPhysician(boolean includeTreatingPhysician) {
        this.includeTreatingPhysician = includeTreatingPhysician;
    }

    public NameWithAddressDto getTreatingPhysician() {
        return treatingPhysician;
    }

    public void setTreatingPhysician(NameWithAddressDto treatingPhysician) {
        this.treatingPhysician = treatingPhysician;
    }

    @NotNull
    public boolean isIncludeHospital() {
        return includeHospital;
    }

    public void setIncludeHospital(boolean includeHospital) {
        this.includeHospital = includeHospital;
    }

    public HospitalDto getTreatingHospital() {
        return treatingHospital;
    }

    public void setTreatingHospital(HospitalDto treatingHospital) {
        this.treatingHospital = treatingHospital;
    }

    public PatientDto getPatient() {
        return patient;
    }

    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }

    public ManagerDto getManager() {
        return manager;
    }

    public void setManager(ManagerDto manager) {
        this.manager = manager;
    }

    @NotNull
    public boolean isIncludeManager() {
        return includeManager;
    }

    public void setIncludeManager(boolean includeManager) {
        this.includeManager = includeManager;
    }

    public ProcedureDto getProcedure() {
        return procedure;
    }

    public void setProcedure(ProcedureDto procedure) {
        this.procedure = procedure;
    }

    public PatientVisitDto getPatientVisit() {
        return patientVisit;
    }

    public void setPatientVisit(PatientVisitDto patientVisit) {
        this.patientVisit = patientVisit;
    }

    public InsuranceDto getInsurance() {
        return insurance;
    }

    public void setInsurance(InsuranceDto insurance) {
        this.insurance = insurance;
    }

    public AdtEventDto getAdtEvent() {
        return adtEvent;
    }

    public void setAdtEvent(AdtEventDto adtEvent) {
        this.adtEvent = adtEvent;
    }

    public List<RelatedNoteItemDto> getRelatedNotes() {
        return relatedNotes;
    }

    public void setRelatedNotes(List<RelatedNoteItemDto> relatedNotes) {
        this.relatedNotes = relatedNotes;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public EVNEventTypeSegmentDto getAdtSegmentEVN() {
        return adtSegmentEVN;
    }

    public void setAdtSegmentEVN(EVNEventTypeSegmentDto adtSegmentEVN) {
        this.adtSegmentEVN = adtSegmentEVN;
    }

    public PIDPatientIdentificationSegmentDto getAdtSegmentPID() {
        return adtSegmentPID;
    }

    public void setAdtSegmentPID(PIDPatientIdentificationSegmentDto adtSegmentPID) {
        this.adtSegmentPID = adtSegmentPID;
    }

    public PV1PatientVisitSegmentDto getAdtSegmentPV1() {
        return adtSegmentPV1;
    }

    public void setAdtSegmentPV1(PV1PatientVisitSegmentDto adtSegmentPV1) {
        this.adtSegmentPV1 = adtSegmentPV1;
    }

    public List<DG1DiagnosisSegmentDto> getAdtSegmentsDG1() {
        return adtSegmentsDG1;
    }

    public void setAdtSegmentsDG1(List<DG1DiagnosisSegmentDto> adtSegmentsDG1) {
        this.adtSegmentsDG1 = adtSegmentsDG1;
    }

    public List<GT1GuarantorSegmentDto> getAdtSegmentsGT1() {
        return adtSegmentsGT1;
    }

    public void setAdtSegmentsGT1(List<GT1GuarantorSegmentDto> adtSegmentsGT1) {
        this.adtSegmentsGT1 = adtSegmentsGT1;
    }

    public List<PR1ProcedureSegmentDto> getAdtSegmentsPR1() {
        return adtSegmentsPR1;
    }

    public void setAdtSegmentsPR1(List<PR1ProcedureSegmentDto> adtSegmentsPR1) {
        this.adtSegmentsPR1 = adtSegmentsPR1;
    }

    public List<IN1InsuranceSegmentDto> getAdtSegmentsIN1() {
        return adtSegmentsIN1;
    }

    public void setAdtSegmentsIN1(List<IN1InsuranceSegmentDto> adtSegmentsIN1) {
        this.adtSegmentsIN1 = adtSegmentsIN1;
    }

    public List<AL1AllergySegmentDto> getAdtSegmentsAL1() {
        return adtSegmentsAL1;
    }

    public void setAdtSegmentsAL1(List<AL1AllergySegmentDto> adtSegmentsAL1) {
        this.adtSegmentsAL1 = adtSegmentsAL1;
    }

    public PD1AdditionalDemographicSegmentDto getAdtSegmentPD1() {
        return adtSegmentPD1;
    }

    public void setAdtSegmentPD1(PD1AdditionalDemographicSegmentDto adtSegmentPD1) {
        this.adtSegmentPD1 = adtSegmentPD1;
    }

    public Boolean getIsIrRequired() {
        return isIrRequired;
    }

    public void setIsIrRequired(Boolean irRequired) {
        isIrRequired = irRequired;
    }

    public Long getIrId() {
        return irId;
    }

    public void setIrId(Long irId) {
        this.irId = irId;
    }

	public Long getIrDate() {
		return irDate;
	}

	public void setIrDate(Long irDate) {
		this.irDate = irDate;
	}
}
