package com.scnsoft.eldermark.beans.reports.enums;

import com.scnsoft.eldermark.beans.reports.constants.ReportConstants;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;

import java.util.function.Function;

public enum AdlReportField {
    BATHING(ComprehensiveAssessment::getBathingADL, "Bathing"),
    DRESSING(ComprehensiveAssessment::getDressingADL, "Dressing"),
    TOILETING(ComprehensiveAssessment::getToiletingADL, "Toileting"),
    EATING(ComprehensiveAssessment::getEatingADL, "Eating"),
    MEDICATIONS(ComprehensiveAssessment::getMedicationsIADL, "Medications"),
    HOUSEKEEPING(ComprehensiveAssessment::getHousekeepingIADL, "Housekeeping"),
    MEALS(ComprehensiveAssessment::getMealsIADL, "Meals"),
    LAUNDRY(ComprehensiveAssessment::getLaundryIADL, "Laundry"),
    TELEPHONE(ComprehensiveAssessment::getTelephoneIADL, "Telephone"),
    COOKING(ComprehensiveAssessment::getCookingConcerns, "Cooking/baking"),
    SHOPPING(ComprehensiveAssessment::getShoppingConcerns, "Shopping"),
    PAYING(ComprehensiveAssessment::getPayingBills, "Paying bills/finances"),
    MEDICATION_APPOINTMENT(ComprehensiveAssessment::getAppointmentsAssistance, "Assistance with medical appointments"),
    MEDICAL_HISTORY_CARDIAC(ComprehensiveAssessment::getMedicalHistoryCardiac, ReportConstants.MEDICAL_HISTORY_CARDIAC),
    MEDICAL_HISTORY_PULMONARY(ComprehensiveAssessment::getMedicalHistoryPulmonary, ReportConstants.MEDICAL_HISTORY_PULMONARY),
    MEDICAL_HISTORY_DIABETIC(ComprehensiveAssessment::getMedicalHistoryDiabetic, ReportConstants.MEDICAL_HISTORY_DIABETIC),
    MEDICAL_HISTORY_NEUROLOGICAL(ComprehensiveAssessment::getMedicalHistoryNeurological, ReportConstants.MEDICAL_HISTORY_NEUROLOGICAL),
    MEDICAL_HISTORY_GASTROINTESTINA(ComprehensiveAssessment::getMedicalHistoryGastrointestina, ReportConstants.MEDICAL_HISTORY_GASTROINTESTINA),
    MEDICAL_HISTORY_MUSCULOSKELETAL(ComprehensiveAssessment::getMedicalHistoryMusculoskeletal, ReportConstants.MEDICAL_HISTORY_MUSCULOSKELETAL),
    MEDICAL_HISTORY_GYN_URINARY(ComprehensiveAssessment::getMedicalHistoryGynUrinary, ReportConstants.MEDICAL_HISTORY_GYN_URINARY),
    MEDICAL_HISTORY_INFECTIOUS_DISEASE(ComprehensiveAssessment::getMedicalHistoryInfectiousDisease, ReportConstants.MEDICAL_HISTORY_INFECTIOUS_DISEASE),
    MEDICAL_HISTORY_IMMUNE_DISORDERS(ComprehensiveAssessment::getMedicalHistoryImmuneDisorders, ReportConstants.MEDICAL_HISTORY_IMMUNE_DISORDERS),
    MEDICAL_HISTORY_BEHAVIORAL_HEALTH(ComprehensiveAssessment::getMedicalHistoryBehavioralHealth, ReportConstants.MEDICAL_HISTORY_BEHAVIORAL_HEALTH),
    MEDICAL_HISTORY_WOUNDS(ComprehensiveAssessment::getMedicalHistoryWounds, ReportConstants.MEDICAL_HISTORY_WOUNDS),
    MEDICAL_HISTORY_VISION_HEARING_DENTAL(ComprehensiveAssessment::getMedicalHistoryVisionHearingDental, ReportConstants.MEDICAL_HISTORY_VISION_HEARING_DENTAL),
    MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION(ComprehensiveAssessment::getMedicalHistoryChronicPainLocation, ReportConstants.MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION),
    MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS(ComprehensiveAssessment::getMedicalHistoryChronicPainAgitators, ReportConstants.MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS),
    MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY(ComprehensiveAssessment::getMedicalHistoryChronicPainSeverity, ReportConstants.MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY),
    MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME(ComprehensiveAssessment::getMedicalHistoryChronicPainLengthOfTime, ReportConstants.MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME),
    MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS(ComprehensiveAssessment::getMedicalHistoryChronicPainRelievingFactors, ReportConstants.MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS),
    MEDICAL_HISTORY_CHRONIC_PAIN_COMMENT(ComprehensiveAssessment::getMedicalHistoryChronicPainComment, "Comment");

    private final Function<ComprehensiveAssessment<?>, Object> method;
    private final String displayName;

    AdlReportField(Function<ComprehensiveAssessment<?>, Object> method, String displayName) {
        this.method = method;
        this.displayName = displayName;
    }

    public Function<ComprehensiveAssessment<?>, Object> getMethod() {
        return method;
    }

    public String getDisplayName() {
        return displayName;
    }
}
