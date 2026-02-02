package com.scnsoft.eldermark.dump.model;

import com.scnsoft.eldermark.dump.bean.ComprehensiveAssessment;

import java.util.function.Function;

public enum MedicalDiagnosisField {
    MEDICAL_HISTORY_CARDIAC(ComprehensiveAssessment::getMedicalHistoryCardiac, "Cardiac"),
    MEDICAL_HISTORY_PULMONARY(ComprehensiveAssessment::getMedicalHistoryPulmonary, "Pulmonary"),
    MEDICAL_HISTORY_DIABETIC(ComprehensiveAssessment::getMedicalHistoryDiabetic, "Diabetic"),
    MEDICAL_HISTORY_NEUROLOGICAL(ComprehensiveAssessment::getMedicalHistoryNeurological, "Neurological"),
    MEDICAL_HISTORY_GASTROINTESTINA(ComprehensiveAssessment::getMedicalHistoryGastrointestina, "Gastrointestina"),
    MEDICAL_HISTORY_MUSCULOSKELETAL(ComprehensiveAssessment::getMedicalHistoryMusculoskeletal, "Musculoskeletal"),
    MEDICAL_HISTORY_GYN_URINARY(ComprehensiveAssessment::getMedicalHistoryGynUrinary, "Gyn/urinary"),
    MEDICAL_HISTORY_INFECTIOUS_DISEASE(ComprehensiveAssessment::getMedicalHistoryInfectiousDisease, "Infectious disease"),
    MEDICAL_HISTORY_IMMUNE_DISORDERS(ComprehensiveAssessment::getMedicalHistoryImmuneDisorders, "Immune disorders"),
    MEDICAL_HISTORY_BEHAVIORAL_HEALTH(ComprehensiveAssessment::getMedicalHistoryBehavioralHealth, "Behavioral health"),
    MEDICAL_HISTORY_WOUNDS(ComprehensiveAssessment::getMedicalHistoryWounds, "Wounds"),
    MEDICAL_HISTORY_VISION_HEARING_DENTAL(ComprehensiveAssessment::getMedicalHistoryVisionHearingDental, "Vision & Hearing & Dental"),
    MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION(ComprehensiveAssessment::getMedicalHistoryChronicPainLocation, "Location"),
    MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS(ComprehensiveAssessment::getMedicalHistoryChronicPainAgitators, "Agitators"),
    MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY(ComprehensiveAssessment::getMedicalHistoryChronicPainSeverity, "Severity"),
    MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME(ComprehensiveAssessment::getMedicalHistoryChronicPainLengthOfTime, "Length of time"),
    MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS(ComprehensiveAssessment::getMedicalHistoryChronicPainRelievingFactors, "Relieving factors/interventions"),
    MEDICAL_HISTORY_CHRONIC_PAIN_COMMENT(ComprehensiveAssessment::getMedicalHistoryChronicPainComment, "Comment");

    private final Function<ComprehensiveAssessment, Object> method;
    private final String displayName;

    MedicalDiagnosisField(Function<ComprehensiveAssessment, Object> method, String displayName) {
        this.method = method;
        this.displayName = displayName;
    }

    public Function<ComprehensiveAssessment, Object> getMethod() {
        return method;
    }

    public String getDisplayName() {
        return displayName;
    }
}
