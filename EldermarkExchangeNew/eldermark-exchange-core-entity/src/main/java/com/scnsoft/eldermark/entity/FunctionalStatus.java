package com.scnsoft.eldermark.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.assessment.AssessmentScaleObservation;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.StatusProblemObservation;
import com.scnsoft.eldermark.entity.document.ccd.CaregiverCharacteristic;
import com.scnsoft.eldermark.entity.document.ccd.HighestPressureUlcerStage;
import com.scnsoft.eldermark.entity.document.ccd.NonMedicinalSupplyActivity;
import com.scnsoft.eldermark.entity.document.ccd.NumberOfPressureUlcersObservation;
import com.scnsoft.eldermark.entity.document.ccd.PressureUlcerObservation;
import com.scnsoft.eldermark.entity.document.ccd.StatusResultObservation;
import com.scnsoft.eldermark.entity.document.ccd.StatusResultOrganizer;

@Entity
public class FunctionalStatus extends BasicEntity {
    private static final long serialVersionUID = 1L;

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
    private Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
