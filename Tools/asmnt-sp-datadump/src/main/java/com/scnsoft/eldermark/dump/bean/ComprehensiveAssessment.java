package com.scnsoft.eldermark.dump.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprehensiveAssessment {

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Race")
    private String race;

    @JsonProperty("Cardiac")
    private List<String> medicalHistoryCardiac;

    @JsonProperty("Pulmonary")
    private List<String> medicalHistoryPulmonary;

    @JsonProperty("Diabetic")
    private List<String> medicalHistoryDiabetic;

    @JsonProperty("Neurological")
    private List<String> medicalHistoryNeurological;

    @JsonProperty("Gastrointestina")
    private List<String> medicalHistoryGastrointestina;

    @JsonProperty("Musculoskeletal")
    private List<String> medicalHistoryMusculoskeletal;

    @JsonProperty("Gyn/urinary")
    private List<String> medicalHistoryGynUrinary;

    @JsonProperty("Infectious disease")
    private List<String> medicalHistoryInfectiousDisease;

    @JsonProperty("Immune disorders")
    private List<String> medicalHistoryImmuneDisorders;

    @JsonProperty("Behavioral health")
    private List<String> medicalHistoryBehavioralHealth;

    @JsonProperty("Wounds")
    private List<String> medicalHistoryWounds;

    @JsonProperty("Vision & Hearing & Dental")
    private List<String> medicalHistoryVisionHearingDental;

    @JsonProperty("Location")
    private String medicalHistoryChronicPainLocation;

    @JsonProperty("Agitators")
    private String medicalHistoryChronicPainAgitators;

    @JsonProperty("Severity")
    private String medicalHistoryChronicPainSeverity;

    @JsonProperty("Length of time")
    private String medicalHistoryChronicPainLengthOfTime;

    @JsonProperty("Relieving factors/interventions")
    private String medicalHistoryChronicPainRelievingFactors;

    @JsonProperty("Comment8")
    private String medicalHistoryChronicPainComment;

    @JsonProperty("Last hospital admission date")
    private String lastHospitalAdmissionDate;

    @JsonProperty("Reason for admission")
    private String reasonForAdmission;

    @JsonProperty("Last ED visit date")
    private String lastEDVisitDate;

    @JsonProperty("Comment14")
    private String medicalHistoryAdmissionsComment;

    @JsonProperty("# of ED visits in the last six months")
    private Long numberOfEdVisits6Months;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public List<String> getMedicalHistoryCardiac() {
        return medicalHistoryCardiac;
    }

    public void setMedicalHistoryCardiac(List<String> medicalHistoryCardiac) {
        this.medicalHistoryCardiac = medicalHistoryCardiac;
    }

    public List<String> getMedicalHistoryPulmonary() {
        return medicalHistoryPulmonary;
    }

    public void setMedicalHistoryPulmonary(List<String> medicalHistoryPulmonary) {
        this.medicalHistoryPulmonary = medicalHistoryPulmonary;
    }

    public List<String> getMedicalHistoryDiabetic() {
        return medicalHistoryDiabetic;
    }

    public void setMedicalHistoryDiabetic(List<String> medicalHistoryDiabetic) {
        this.medicalHistoryDiabetic = medicalHistoryDiabetic;
    }

    public List<String> getMedicalHistoryNeurological() {
        return medicalHistoryNeurological;
    }

    public void setMedicalHistoryNeurological(List<String> medicalHistoryNeurological) {
        this.medicalHistoryNeurological = medicalHistoryNeurological;
    }

    public List<String> getMedicalHistoryGastrointestina() {
        return medicalHistoryGastrointestina;
    }

    public void setMedicalHistoryGastrointestina(List<String> medicalHistoryGastrointestina) {
        this.medicalHistoryGastrointestina = medicalHistoryGastrointestina;
    }

    public List<String> getMedicalHistoryMusculoskeletal() {
        return medicalHistoryMusculoskeletal;
    }

    public void setMedicalHistoryMusculoskeletal(List<String> medicalHistoryMusculoskeletal) {
        this.medicalHistoryMusculoskeletal = medicalHistoryMusculoskeletal;
    }

    public List<String> getMedicalHistoryGynUrinary() {
        return medicalHistoryGynUrinary;
    }

    public void setMedicalHistoryGynUrinary(List<String> medicalHistoryGynUrinary) {
        this.medicalHistoryGynUrinary = medicalHistoryGynUrinary;
    }

    public List<String> getMedicalHistoryInfectiousDisease() {
        return medicalHistoryInfectiousDisease;
    }

    public void setMedicalHistoryInfectiousDisease(List<String> medicalHistoryInfectiousDisease) {
        this.medicalHistoryInfectiousDisease = medicalHistoryInfectiousDisease;
    }

    public List<String> getMedicalHistoryImmuneDisorders() {
        return medicalHistoryImmuneDisorders;
    }

    public void setMedicalHistoryImmuneDisorders(List<String> medicalHistoryImmuneDisorders) {
        this.medicalHistoryImmuneDisorders = medicalHistoryImmuneDisorders;
    }

    public List<String> getMedicalHistoryBehavioralHealth() {
        return medicalHistoryBehavioralHealth;
    }

    public void setMedicalHistoryBehavioralHealth(List<String> medicalHistoryBehavioralHealth) {
        this.medicalHistoryBehavioralHealth = medicalHistoryBehavioralHealth;
    }

    public List<String> getMedicalHistoryWounds() {
        return medicalHistoryWounds;
    }

    public void setMedicalHistoryWounds(List<String> medicalHistoryWounds) {
        this.medicalHistoryWounds = medicalHistoryWounds;
    }

    public List<String> getMedicalHistoryVisionHearingDental() {
        return medicalHistoryVisionHearingDental;
    }

    public void setMedicalHistoryVisionHearingDental(List<String> medicalHistoryVisionHearingDental) {
        this.medicalHistoryVisionHearingDental = medicalHistoryVisionHearingDental;
    }

    public String getMedicalHistoryChronicPainLocation() {
        return medicalHistoryChronicPainLocation;
    }

    public void setMedicalHistoryChronicPainLocation(String medicalHistoryChronicPainLocation) {
        this.medicalHistoryChronicPainLocation = medicalHistoryChronicPainLocation;
    }

    public String getMedicalHistoryChronicPainAgitators() {
        return medicalHistoryChronicPainAgitators;
    }

    public void setMedicalHistoryChronicPainAgitators(String medicalHistoryChronicPainAgitators) {
        this.medicalHistoryChronicPainAgitators = medicalHistoryChronicPainAgitators;
    }

    public String getMedicalHistoryChronicPainSeverity() {
        return medicalHistoryChronicPainSeverity;
    }

    public void setMedicalHistoryChronicPainSeverity(String medicalHistoryChronicPainSeverity) {
        this.medicalHistoryChronicPainSeverity = medicalHistoryChronicPainSeverity;
    }

    public String getMedicalHistoryChronicPainLengthOfTime() {
        return medicalHistoryChronicPainLengthOfTime;
    }

    public void setMedicalHistoryChronicPainLengthOfTime(String medicalHistoryChronicPainLengthOfTime) {
        this.medicalHistoryChronicPainLengthOfTime = medicalHistoryChronicPainLengthOfTime;
    }

    public String getMedicalHistoryChronicPainRelievingFactors() {
        return medicalHistoryChronicPainRelievingFactors;
    }

    public void setMedicalHistoryChronicPainRelievingFactors(String medicalHistoryChronicPainRelievingFactors) {
        this.medicalHistoryChronicPainRelievingFactors = medicalHistoryChronicPainRelievingFactors;
    }

    public String getMedicalHistoryChronicPainComment() {
        return medicalHistoryChronicPainComment;
    }

    public void setMedicalHistoryChronicPainComment(String medicalHistoryChronicPainComment) {
        this.medicalHistoryChronicPainComment = medicalHistoryChronicPainComment;
    }

    public String getLastHospitalAdmissionDate() {
        return lastHospitalAdmissionDate;
    }

    public void setLastHospitalAdmissionDate(String lastHospitalAdmissionDate) {
        this.lastHospitalAdmissionDate = lastHospitalAdmissionDate;
    }

    public String getReasonForAdmission() {
        return reasonForAdmission;
    }

    public void setReasonForAdmission(String reasonForAdmission) {
        this.reasonForAdmission = reasonForAdmission;
    }

    public String getLastEDVisitDate() {
        return lastEDVisitDate;
    }

    public void setLastEDVisitDate(String lastEDVisitDate) {
        this.lastEDVisitDate = lastEDVisitDate;
    }

    public String getMedicalHistoryAdmissionsComment() {
        return medicalHistoryAdmissionsComment;
    }

    public void setMedicalHistoryAdmissionsComment(String medicalHistoryAdmissionsComment) {
        this.medicalHistoryAdmissionsComment = medicalHistoryAdmissionsComment;
    }

    public Long getNumberOfEdVisits6Months() {
        return numberOfEdVisits6Months;
    }

    public void setNumberOfEdVisits6Months(Long numberOfEdVisits6Months) {
        this.numberOfEdVisits6Months = numberOfEdVisits6Months;
    }
}
