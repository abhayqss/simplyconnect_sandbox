package com.scnsoft.eldermark.beans.reports.model.assessment.housing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HousingAssessment {

    @JsonProperty("Program")
    private String program;

    @JsonProperty("Assessment_Type")
    private String assessmentType;

    @JsonProperty("Have_you_ever_been_on_lease")
    private String leaseQuestion;

    @JsonProperty("have_you_lived_in_subsidized_housing_before")
    private String subsidizedHousingQuestion;

    @JsonProperty("have_Housing_Voucher")
    private String housingVoucherQuestion;

    @JsonProperty("do_have_any_evictions")
    private String evictionsQuestion;

    @JsonProperty("Do_you_have_any_accessibility_needs")
    private String accessibilityNeedsQuestion;

    @JsonProperty("have_any_pets?")
    private String petsQuestion;

    @JsonProperty("Do_you_currently_receive_income?")
    private String incomeQuestion;

    @JsonProperty("Do_you_have_any_savings_for_moving?")
    private String savingsQuestion;

    @JsonProperty("Credit_Status")
    private String creditStatus;

    @JsonProperty("Do_you_owe_any_utilities_eviction_costs")
    private String owingQuestion;

    @JsonProperty("Do_you_have_any_of_the_following?")
    private CriminalQuestions criminalQuestions;

    @JsonProperty("Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.")
    private String batheQuestion;

    @JsonProperty("Dress_and_groom_as_in_selecting_clothes.")
    private String dressAndGroomQuestion;

    @JsonProperty("Toileting_as_in_getting_to_and_from_the_toilet.")
    private String toiletingQuestion;

    @JsonProperty("Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.")
    private String eatingQuestion;

    @JsonProperty("Medications_which_covers_obtaining_medications.")
    private String medicationQuestion;

    @JsonProperty("Housekeeping_cleaning_kitchens_after_eating_keeping_tidy")
    private String housekeepingQuestion;

    @JsonProperty("Meal_Preparation_Cooking")
    private String cookingQuestion;

    @JsonProperty("Laundry")
    private String laundryQuestion;

    @JsonProperty("Telephone")
    private String telephoneQuestion;

    @JsonProperty("Shopping")
    private String shoppingQuestion;

    @JsonProperty("Finances_paying_bills_and_managing_financial_assets.")
    private String financesQuestion;

    @JsonProperty("Transportation_via_driving_other_means_of_transport.")
    private String transportationQuestion;

    @JsonProperty("Make_Medical_Appointments")
    private String makeMedicalAppointmentsQuestion;

    @JsonProperty("Mobility_walking_inside_outside_home")
    private String mobilityQuestion;

    @JsonProperty("Have_family_member_or_caregiver_assisting_you?")
    private String familyMemberQuestion;

    public static class CriminalQuestions {
        @JsonProperty("Pending_Legal_Case")
        private String pendingLegalCaseQuestion;

        @JsonProperty("Criminal_Convictions")
        private String criminalConvictionsQuestion;

        @JsonProperty("Open_Legal_Case")
        private String openLegalCaseQuestion;

        @JsonProperty("Registered_290")
        private String registered290Question;

        public String getPendingLegalCaseQuestion() {
            return pendingLegalCaseQuestion;
        }

        public void setPendingLegalCaseQuestion(String pendingLegalCaseQuestion) {
            this.pendingLegalCaseQuestion = pendingLegalCaseQuestion;
        }

        public String getCriminalConvictionsQuestion() {
            return criminalConvictionsQuestion;
        }

        public void setCriminalConvictionsQuestion(String criminalConvictionsQuestion) {
            this.criminalConvictionsQuestion = criminalConvictionsQuestion;
        }

        public String getOpenLegalCaseQuestion() {
            return openLegalCaseQuestion;
        }

        public void setOpenLegalCaseQuestion(String openLegalCaseQuestion) {
            this.openLegalCaseQuestion = openLegalCaseQuestion;
        }

        public String getRegistered290Question() {
            return registered290Question;
        }

        public void setRegistered290Question(String registered290Question) {
            this.registered290Question = registered290Question;
        }
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getLeaseQuestion() {
        return leaseQuestion;
    }

    public void setLeaseQuestion(String leaseQuestion) {
        this.leaseQuestion = leaseQuestion;
    }

    public String getSubsidizedHousingQuestion() {
        return subsidizedHousingQuestion;
    }

    public void setSubsidizedHousingQuestion(String subsidizedHousingQuestion) {
        this.subsidizedHousingQuestion = subsidizedHousingQuestion;
    }

    public String getHousingVoucherQuestion() {
        return housingVoucherQuestion;
    }

    public void setHousingVoucherQuestion(String housingVoucherQuestion) {
        this.housingVoucherQuestion = housingVoucherQuestion;
    }

    public String getEvictionsQuestion() {
        return evictionsQuestion;
    }

    public void setEvictionsQuestion(String evictionsQuestion) {
        this.evictionsQuestion = evictionsQuestion;
    }

    public String getAccessibilityNeedsQuestion() {
        return accessibilityNeedsQuestion;
    }

    public void setAccessibilityNeedsQuestion(String accessibilityNeedsQuestion) {
        this.accessibilityNeedsQuestion = accessibilityNeedsQuestion;
    }

    public String getPetsQuestion() {
        return petsQuestion;
    }

    public void setPetsQuestion(String petsQuestion) {
        this.petsQuestion = petsQuestion;
    }

    public String getIncomeQuestion() {
        return incomeQuestion;
    }

    public void setIncomeQuestion(String incomeQuestion) {
        this.incomeQuestion = incomeQuestion;
    }

    public String getSavingsQuestion() {
        return savingsQuestion;
    }

    public void setSavingsQuestion(String savingsQuestion) {
        this.savingsQuestion = savingsQuestion;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getOwingQuestion() {
        return owingQuestion;
    }

    public void setOwingQuestion(String owingQuestion) {
        this.owingQuestion = owingQuestion;
    }

    public CriminalQuestions getCriminalQuestions() {
        return criminalQuestions;
    }

    public void setCriminalQuestions(CriminalQuestions criminalQuestions) {
        this.criminalQuestions = criminalQuestions;
    }

    public String getBatheQuestion() {
        return batheQuestion;
    }

    public void setBatheQuestion(String batheQuestion) {
        this.batheQuestion = batheQuestion;
    }

    public String getDressAndGroomQuestion() {
        return dressAndGroomQuestion;
    }

    public void setDressAndGroomQuestion(String dressAndGroomQuestion) {
        this.dressAndGroomQuestion = dressAndGroomQuestion;
    }

    public String getToiletingQuestion() {
        return toiletingQuestion;
    }

    public void setToiletingQuestion(String toiletingQuestion) {
        this.toiletingQuestion = toiletingQuestion;
    }

    public String getEatingQuestion() {
        return eatingQuestion;
    }

    public void setEatingQuestion(String eatingQuestion) {
        this.eatingQuestion = eatingQuestion;
    }

    public String getMedicationQuestion() {
        return medicationQuestion;
    }

    public void setMedicationQuestion(String medicationQuestion) {
        this.medicationQuestion = medicationQuestion;
    }

    public String getHousekeepingQuestion() {
        return housekeepingQuestion;
    }

    public void setHousekeepingQuestion(String housekeepingQuestion) {
        this.housekeepingQuestion = housekeepingQuestion;
    }

    public String getCookingQuestion() {
        return cookingQuestion;
    }

    public void setCookingQuestion(String cookingQuestion) {
        this.cookingQuestion = cookingQuestion;
    }

    public String getLaundryQuestion() {
        return laundryQuestion;
    }

    public void setLaundryQuestion(String laundryQuestion) {
        this.laundryQuestion = laundryQuestion;
    }

    public String getTelephoneQuestion() {
        return telephoneQuestion;
    }

    public void setTelephoneQuestion(String telephoneQuestion) {
        this.telephoneQuestion = telephoneQuestion;
    }

    public String getShoppingQuestion() {
        return shoppingQuestion;
    }

    public void setShoppingQuestion(String shoppingQuestion) {
        this.shoppingQuestion = shoppingQuestion;
    }

    public String getFinancesQuestion() {
        return financesQuestion;
    }

    public void setFinancesQuestion(String financesQuestion) {
        this.financesQuestion = financesQuestion;
    }

    public String getTransportationQuestion() {
        return transportationQuestion;
    }

    public void setTransportationQuestion(String transportationQuestion) {
        this.transportationQuestion = transportationQuestion;
    }

    public String getMakeMedicalAppointmentsQuestion() {
        return makeMedicalAppointmentsQuestion;
    }

    public void setMakeMedicalAppointmentsQuestion(String makeMedicalAppointmentsQuestion) {
        this.makeMedicalAppointmentsQuestion = makeMedicalAppointmentsQuestion;
    }

    public String getMobilityQuestion() {
        return mobilityQuestion;
    }

    public void setMobilityQuestion(String mobilityQuestion) {
        this.mobilityQuestion = mobilityQuestion;
    }

    public String getFamilyMemberQuestion() {
        return familyMemberQuestion;
    }

    public void setFamilyMemberQuestion(String familyMemberQuestion) {
        this.familyMemberQuestion = familyMemberQuestion;
    }
}
