package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class FunctionalStatus extends BasicEntity {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_FunctionalStatusResultOrganizer",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "result_organizer_id"))
    private List<StatusResultOrganizer> functionalStatusResultOrganizers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_CognitiveStatusResultOrganizer",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "result_organizer_id"))
    private List<StatusResultOrganizer> cognitiveStatusResultOrganizers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_FunctionalStatusResultObservation",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "result_observation_id"))
    private List<StatusResultObservation> functionalStatusResultObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_CognitiveStatusResultObservation",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "result_observation_id"))
    private List<StatusResultObservation> cognitiveStatusResultObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_FunctionalStatusProblemObservation",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "problem_observation_id"))
    private List<StatusProblemObservation> functionalStatusProblemObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_CognitiveStatusProblemObservation",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "problem_observation_id"))
    private List<StatusProblemObservation> cognitiveStatusProblemObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_CaregiverCharacteristic",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "caregiver_characteristic_id"))
    private List<CaregiverCharacteristic> caregiverCharacteristics;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_AssessmentScaleObservation",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "observation_id"))
    private List<AssessmentScaleObservation> assessmentScaleObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "FunctionalStatus_NonMedicinalSupplyActivity",
            joinColumns = @JoinColumn(name = "functional_status_id"),
            inverseJoinColumns = @JoinColumn(name = "supply_activity_id"))
    private List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "functionalStatus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PressureUlcerObservation> pressureUlcerObservations;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "functionalStatus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NumberOfPressureUlcersObservation> numberOfPressureUlcersObservations;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "functionalStatus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HighestPressureUlcerStage> highestPressureUlcerStages;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    public List<StatusResultOrganizer> getFunctionalStatusResultOrganizers() {
        return functionalStatusResultOrganizers;
    }

    public void setFunctionalStatusResultOrganizers(List<StatusResultOrganizer> functionalStatusResultOrganizers) {
        this.functionalStatusResultOrganizers = functionalStatusResultOrganizers;
    }

    public List<StatusResultOrganizer> getCognitiveStatusResultOrganizers() {
        return cognitiveStatusResultOrganizers;
    }

    public void setCognitiveStatusResultOrganizers(List<StatusResultOrganizer> cognitiveStatusResultOrganizers) {
        this.cognitiveStatusResultOrganizers = cognitiveStatusResultOrganizers;
    }

    public List<StatusResultObservation> getFunctionalStatusResultObservations() {
        return functionalStatusResultObservations;
    }

    public void setFunctionalStatusResultObservations(List<StatusResultObservation> functionalStatusResultObservations) {
        this.functionalStatusResultObservations = functionalStatusResultObservations;
    }

    public List<StatusResultObservation> getCognitiveStatusResultObservations() {
        return cognitiveStatusResultObservations;
    }

    public void setCognitiveStatusResultObservations(List<StatusResultObservation> cognitiveStatusResultObservations) {
        this.cognitiveStatusResultObservations = cognitiveStatusResultObservations;
    }

    public List<StatusProblemObservation> getFunctionalStatusProblemObservations() {
        return functionalStatusProblemObservations;
    }

    public void setFunctionalStatusProblemObservations(List<StatusProblemObservation> functionalStatusProblemObservations) {
        this.functionalStatusProblemObservations = functionalStatusProblemObservations;
    }

    public List<StatusProblemObservation> getCognitiveStatusProblemObservations() {
        return cognitiveStatusProblemObservations;
    }

    public void setCognitiveStatusProblemObservations(List<StatusProblemObservation> cognitiveStatusProblemObservations) {
        this.cognitiveStatusProblemObservations = cognitiveStatusProblemObservations;
    }

    public List<CaregiverCharacteristic> getCaregiverCharacteristics() {
        return caregiverCharacteristics;
    }

    public void setCaregiverCharacteristics(List<CaregiverCharacteristic> caregiverCharacteristics) {
        this.caregiverCharacteristics = caregiverCharacteristics;
    }

    public List<AssessmentScaleObservation> getAssessmentScaleObservations() {
        return assessmentScaleObservations;
    }

    public void setAssessmentScaleObservations(List<AssessmentScaleObservation> assessmentScaleObservations) {
        this.assessmentScaleObservations = assessmentScaleObservations;
    }

    public List<NonMedicinalSupplyActivity> getNonMedicinalSupplyActivities() {
        return nonMedicinalSupplyActivities;
    }

    public void setNonMedicinalSupplyActivities(List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities) {
        this.nonMedicinalSupplyActivities = nonMedicinalSupplyActivities;
    }

    public List<PressureUlcerObservation> getPressureUlcerObservations() {
        return pressureUlcerObservations;
    }

    public void setPressureUlcerObservations(List<PressureUlcerObservation> pressureUlcerObservations) {
        this.pressureUlcerObservations = pressureUlcerObservations;
    }

    public List<NumberOfPressureUlcersObservation> getNumberOfPressureUlcersObservations() {
        return numberOfPressureUlcersObservations;
    }

    public void setNumberOfPressureUlcersObservations(List<NumberOfPressureUlcersObservation> numberOfPressureUlcersObservations) {
        this.numberOfPressureUlcersObservations = numberOfPressureUlcersObservations;
    }

    public List<HighestPressureUlcerStage> getHighestPressureUlcerStages() {
        return highestPressureUlcerStages;
    }

    public void setHighestPressureUlcerStages(List<HighestPressureUlcerStage> highestPressureUlcerStages) {
        this.highestPressureUlcerStages = highestPressureUlcerStages;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
