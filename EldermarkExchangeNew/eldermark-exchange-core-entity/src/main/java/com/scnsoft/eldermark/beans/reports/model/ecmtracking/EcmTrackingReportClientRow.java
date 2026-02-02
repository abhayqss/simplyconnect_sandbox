package com.scnsoft.eldermark.beans.reports.model.ecmtracking;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class EcmTrackingReportClientRow {
    private String clientCareCoordinatorName;

    private String insuranceName;

    private Long clientId;
    private String clientStatus;
    private String clientFirstName;
    private String clientLastName;
    private LocalDate dateOfBirth;
    private String medicaid;
    private String phoneNumber;

    private String housingStatus;
    private Instant lastArizonaAssessmentCompleteDate;
    private Long totalNumberCompletedArizonaAssessment;
    private Instant lastHmisAssessmentCompleteDate;
    private Long totalNumberCompletedHmisAssessment;
    private Instant lastNorCalAssessmentCompleteDate;
    private Long totalNumberCompletedNorCalAssessment;

    private List<EcmTrackingReportInsuranceAuthorizationDto> insuranceAuthorizations;

    private Instant intakeSpecialistFirstEncounterDate;
    private Instant ecmFaceToFaceFirstEncounterDate;

    private List<Instant> disenrollmentDates;
    private List<ClientDeactivationReason> disenrollmentReasons;

    private Instant lastCaseNoteDate;
    private Long totalNumberCaseNotes;

    private Instant lastServicePlanDate;
    private Long totalNumberServicePlansCompleted;

    private Instant lastEventDate;
    private Long totalNumberEventNotesCompleted;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMedicaid() {
        return medicaid;
    }

    public void setMedicaid(String medicaid) {
        this.medicaid = medicaid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientCareCoordinatorName() {
        return clientCareCoordinatorName;
    }

    public void setClientCareCoordinatorName(String clientCareCoordinatorName) {
        this.clientCareCoordinatorName = clientCareCoordinatorName;
    }

    public String getHousingStatus() {
        return housingStatus;
    }

    public void setHousingStatus(String housingStatus) {
        this.housingStatus = housingStatus;
    }

    public Instant getLastArizonaAssessmentCompleteDate() {
        return lastArizonaAssessmentCompleteDate;
    }

    public void setLastArizonaAssessmentCompleteDate(Instant lastArizonaAssessmentCompleteDate) {
        this.lastArizonaAssessmentCompleteDate = lastArizonaAssessmentCompleteDate;
    }

    public Long getTotalNumberCompletedArizonaAssessment() {
        return totalNumberCompletedArizonaAssessment;
    }

    public void setTotalNumberCompletedArizonaAssessment(Long totalNumberCompletedArizonaAssessment) {
        this.totalNumberCompletedArizonaAssessment = totalNumberCompletedArizonaAssessment;
    }

    public Instant getLastHmisAssessmentCompleteDate() {
        return lastHmisAssessmentCompleteDate;
    }

    public void setLastHmisAssessmentCompleteDate(Instant lastHmisAssessmentCompleteDate) {
        this.lastHmisAssessmentCompleteDate = lastHmisAssessmentCompleteDate;
    }

    public Long getTotalNumberCompletedHmisAssessment() {
        return totalNumberCompletedHmisAssessment;
    }

    public void setTotalNumberCompletedHmisAssessment(Long totalNumberCompletedHmisAssessment) {
        this.totalNumberCompletedHmisAssessment = totalNumberCompletedHmisAssessment;
    }

    public Instant getLastNorCalAssessmentCompleteDate() {
        return lastNorCalAssessmentCompleteDate;
    }

    public void setLastNorCalAssessmentCompleteDate(Instant lastNorCalAssessmentCompleteDate) {
        this.lastNorCalAssessmentCompleteDate = lastNorCalAssessmentCompleteDate;
    }

    public Long getTotalNumberCompletedNorCalAssessment() {
        return totalNumberCompletedNorCalAssessment;
    }

    public void setTotalNumberCompletedNorCalAssessment(Long totalNumberCompletedNorCalAssessment) {
        this.totalNumberCompletedNorCalAssessment = totalNumberCompletedNorCalAssessment;
    }

    public Instant getIntakeSpecialistFirstEncounterDate() {
        return intakeSpecialistFirstEncounterDate;
    }

    public void setIntakeSpecialistFirstEncounterDate(Instant intakeSpecialistFirstEncounterDate) {
        this.intakeSpecialistFirstEncounterDate = intakeSpecialistFirstEncounterDate;
    }

    public Instant getEcmFaceToFaceFirstEncounterDate() {
        return ecmFaceToFaceFirstEncounterDate;
    }

    public void setEcmFaceToFaceFirstEncounterDate(Instant ecmFaceToFaceFirstEncounterDate) {
        this.ecmFaceToFaceFirstEncounterDate = ecmFaceToFaceFirstEncounterDate;
    }

    public List<Instant> getDisenrollmentDates() {
        return disenrollmentDates;
    }

    public void setDisenrollmentDates(List<Instant> disenrollmentDates) {
        this.disenrollmentDates = disenrollmentDates;
    }

    public List<ClientDeactivationReason> getDisenrollmentReasons() {
        return disenrollmentReasons;
    }

    public void setDisenrollmentReasons(List<ClientDeactivationReason> disenrollmentReasons) {
        this.disenrollmentReasons = disenrollmentReasons;
    }

    public Instant getLastCaseNoteDate() {
        return lastCaseNoteDate;
    }

    public void setLastCaseNoteDate(Instant lastCaseNoteDate) {
        this.lastCaseNoteDate = lastCaseNoteDate;
    }

    public Long getTotalNumberCaseNotes() {
        return totalNumberCaseNotes;
    }

    public void setTotalNumberCaseNotes(Long totalNumberCaseNotes) {
        this.totalNumberCaseNotes = totalNumberCaseNotes;
    }

    public Instant getLastServicePlanDate() {
        return lastServicePlanDate;
    }

    public void setLastServicePlanDate(Instant lastServicePlanDate) {
        this.lastServicePlanDate = lastServicePlanDate;
    }

    public Long getTotalNumberServicePlansCompleted() {
        return totalNumberServicePlansCompleted;
    }

    public void setTotalNumberServicePlansCompleted(Long totalNumberServicePlansCompleted) {
        this.totalNumberServicePlansCompleted = totalNumberServicePlansCompleted;
    }

    public Instant getLastEventDate() {
        return lastEventDate;
    }

    public void setLastEventDate(Instant lastEventDate) {
        this.lastEventDate = lastEventDate;
    }

    public Long getTotalNumberEventNotesCompleted() {
        return totalNumberEventNotesCompleted;
    }

    public void setTotalNumberEventNotesCompleted(Long totalNumberEventNotesCompleted) {
        this.totalNumberEventNotesCompleted = totalNumberEventNotesCompleted;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public List<EcmTrackingReportInsuranceAuthorizationDto> getInsuranceAuthorizations() {
        return insuranceAuthorizations;
    }

    public void setInsuranceAuthorizations(List<EcmTrackingReportInsuranceAuthorizationDto> insuranceAuthorizations) {
        this.insuranceAuthorizations = insuranceAuthorizations;
    }
}