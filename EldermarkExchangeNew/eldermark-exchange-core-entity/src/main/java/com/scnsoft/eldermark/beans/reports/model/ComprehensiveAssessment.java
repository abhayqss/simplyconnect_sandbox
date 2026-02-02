package com.scnsoft.eldermark.beans.reports.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scnsoft.eldermark.beans.reports.model.assessment.EmergencyContactsAware;

import java.util.List;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprehensiveAssessment<T> implements EmergencyContactsAware {

    @JsonIgnore
    private T assessmentDbRecord;

    @JsonProperty(GENDER)
    private String gender;

    @JsonProperty(RACE)
    private String race;

    @JsonProperty(MONTHLY_INCOME)
    private String monthlyIncome;

    @JsonProperty(STREET_COMPREHENSIVE_JSON_KEY)
    private String street;

    @JsonProperty(CITY_COMPREHENSIVE_JSON_KEY)
    private String city;

    @JsonProperty(STATE_COMPREHENSIVE_JSON_KEY)
    private String state;

    @JsonProperty(ZIPCODE_COMPREHENSIVE_JSON_KEY)
    private String zipCode;

    @JsonProperty(VETERAN_STATUS)
    private String veteranStatus;

    @JsonProperty(MARITAL_STATUS)
    private String maritalStatus;

    @JsonProperty(MEALS_ON_WHEELS)
    private String mealsOnWheels;

    @JsonProperty(FOOD_SUPPORT)
    private String foodSupport;

    @JsonProperty(BATHING_ADL)
    private String bathingADL;

    @JsonProperty(DRESSING_ADL)
    private String dressingADL;

    @JsonProperty(TOILETING_ADL)
    private String toiletingADL;

    @JsonProperty(EATING_ADL)
    private String eatingADL;

    @JsonProperty(MEDICATOINS_IADL)
    private String medicationsIADL;

    @JsonProperty(HOUSEKEEPING_IADL)
    private String housekeepingIADL;

    @JsonProperty(MEALS_IADL)
    private String mealsIADL;

    @JsonProperty(LAUNDRY_IADL)
    private String laundryIADL;

    @JsonProperty(TELEPHONE_IADL)
    private String telephoneIADL;

    @JsonProperty(COOKING_IADL)
    private String cookingConcerns;

    @JsonProperty(SHOPPING_IADL)
    private String shoppingConcerns;

    @JsonProperty(PAYING_BILLS_IADL)
    private String payingBills;

    @JsonProperty(APPOINTMENTS_ASSISTANCE_IADL)
    private String appointmentsAssistance;

    @JsonProperty(DATE_OF_BIRTH)
    private String dateOfBirth;

    @JsonProperty(LIVING_CONDITIONS)
    private List<String> livingConditions;

    @JsonProperty(TRANSPORTATION_ASSISTANCE)
    private String transportationAssistance;

    @JsonProperty(HOME_SERVICES)
    private List<String> homeServices;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_FIRST_NAME)
    private String primaryCarePhysicianFirstName;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_LAST_NAME)
    private String primaryCarePhysicianLastName;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_PHONE_NUMBER)
    private String primaryCarePhysicianPhoneNumber;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_ADDRESS_STREET)
    private String primaryCarePhysicianAddressStreet;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_ADDRESS_CITY)
    private String primaryCarePhysicianAddressCity;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_ADDRESS_STATE)
    private String primaryCarePhysicianAddressState;

    @JsonProperty(PRIMARY_CARE_PHYSICIAN_ADDRESS_ZIP_CODE)
    private String primaryCarePhysicianAddressZipCode;

    @JsonProperty(SPECIALITY_PHYSICIAN_FIRST_NAME)
    private String specialtyPhysicianFirstName;

    @JsonProperty(SPECIALITY_PHYSICIAN_LAST_NAME)
    private String specialtyPhysicianLastName;

    @JsonProperty(SPECIALITY_PHYSICIAN_SPECIALITY)
    private String specialtyPhysicianSpecialty;

    @JsonProperty(SPECIALITY_PHYSICIAN_PHONE_NUMBER)
    private String specialtyPhysicianPhoneNumber;

    @JsonProperty(SPECIALITY_PHYSICIAN_ADDRESS_STREET)
    private String specialtyPhysicianAddressStreet;

    @JsonProperty(SPECIALITY_PHYSICIAN_ADDRESS_CITY)
    private String specialtyPhysicianAddressCity;

    @JsonProperty(SPECIALITY_PHYSICIAN_ADDRESS_STATE)
    private String specialtyPhysicianAddressState;

    @JsonProperty(SPECIALITY_PHYSICIAN_ADDRESS_ZIP_CODE)
    private String specialtyPhysicianAddressZipCode;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_1)
    private EmergencyContact emergencyContact1;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_2)
    private EmergencyContact emergencyContact2;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_3)
    private EmergencyContact emergencyContact3;

    @JsonProperty(PHARMACY_NAME)
    private String pharmacyName;

    @JsonProperty(PHARMACY_PHONE_NUMBER)
    private String pharmacyPhoneNumber;

    @JsonProperty(PHARMACY_ADDRESS_STREET)
    private String pharmacyAddressStreet;

    @JsonProperty(PHARMACY_ADDRESS_CITY)
    private String pharmacyAddressCity;

    @JsonProperty(PHARMACY_ADDRESS_STATE)
    private String pharmacyAddressState;

    @JsonProperty(PHARMACY_ADDRESS_ZIP_CODE)
    private String pharmacyAddressZipCode;

    @JsonProperty(MEDICAL_HISTORY_CARDIAC)
    private List<String> medicalHistoryCardiac;

    @JsonProperty(MEDICAL_HISTORY_PULMONARY)
    private List<String> medicalHistoryPulmonary;

    @JsonProperty(MEDICAL_HISTORY_DIABETIC)
    private List<String> medicalHistoryDiabetic;

    @JsonProperty(MEDICAL_HISTORY_NEUROLOGICAL)
    private List<String> medicalHistoryNeurological;

    @JsonProperty(MEDICAL_HISTORY_GASTROINTESTINA)
    private List<String> medicalHistoryGastrointestina;

    @JsonProperty(MEDICAL_HISTORY_MUSCULOSKELETAL)
    private List<String> medicalHistoryMusculoskeletal;

    @JsonProperty(MEDICAL_HISTORY_GYN_URINARY)
    private List<String> medicalHistoryGynUrinary;

    @JsonProperty(MEDICAL_HISTORY_INFECTIOUS_DISEASE)
    private List<String> medicalHistoryInfectiousDisease;

    @JsonProperty(MEDICAL_HISTORY_IMMUNE_DISORDERS)
    private List<String> medicalHistoryImmuneDisorders;

    @JsonProperty(MEDICAL_HISTORY_BEHAVIORAL_HEALTH)
    private List<String> medicalHistoryBehavioralHealth;

    @JsonProperty(MEDICAL_HISTORY_WOUNDS)
    private List<String> medicalHistoryWounds;

    @JsonProperty(MEDICAL_HISTORY_VISION_HEARING_DENTAL)
    private List<String> medicalHistoryVisionHearingDental;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION)
    private String medicalHistoryChronicPainLocation;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS)
    private String medicalHistoryChronicPainAgitators;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY)
    private String medicalHistoryChronicPainSeverity;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME)
    private String medicalHistoryChronicPainLengthOfTime;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS)
    private String medicalHistoryChronicPainRelievingFactors;

    @JsonProperty(MEDICAL_HISTORY_CHRONIC_PAIN_COMMENT)
    private String medicalHistoryChronicPainComment;

    @JsonProperty(LAST_HOSPITAL_ADMISSION_DATE)
    private String lastHospitalAdmissionDate;

    @JsonProperty(REASON_FOR_ADMISSION)
    private String reasonForAdmission;

    public T getAssessmentDbRecord() {
        return assessmentDbRecord;
    }

    public void setAssessmentDbRecord(T assessmentDbRecord) {
        this.assessmentDbRecord = assessmentDbRecord;
    }

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

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getVeteranStatus() {
        return veteranStatus;
    }

    public void setVeteranStatus(String veteranStatus) {
        this.veteranStatus = veteranStatus;
    }

    public String getMealsOnWheels() {
        return mealsOnWheels;
    }

    public void setMealsOnWheels(String mealsOnWheels) {
        this.mealsOnWheels = mealsOnWheels;
    }

    public String getFoodSupport() {
        return foodSupport;
    }

    public void setFoodSupport(String foodSupport) {
        this.foodSupport = foodSupport;
    }

    public String getBathingADL() {
        return bathingADL;
    }

    public void setBathingADL(String bathingADL) {
        this.bathingADL = bathingADL;
    }

    public String getDressingADL() {
        return dressingADL;
    }

    public void setDressingADL(String dressingADL) {
        this.dressingADL = dressingADL;
    }

    public String getToiletingADL() {
        return toiletingADL;
    }

    public void setToiletingADL(String toiletingADL) {
        this.toiletingADL = toiletingADL;
    }

    public String getEatingADL() {
        return eatingADL;
    }

    public void setEatingADL(String eatingADL) {
        this.eatingADL = eatingADL;
    }

    public String getMedicationsIADL() {
        return medicationsIADL;
    }

    public void setMedicationsIADL(String medicationsIADL) {
        this.medicationsIADL = medicationsIADL;
    }

    public String getHousekeepingIADL() {
        return housekeepingIADL;
    }

    public void setHousekeepingIADL(String housekeepingIADL) {
        this.housekeepingIADL = housekeepingIADL;
    }

    public String getMealsIADL() {
        return mealsIADL;
    }

    public void setMealsIADL(String mealsIADL) {
        this.mealsIADL = mealsIADL;
    }

    public String getLaundryIADL() {
        return laundryIADL;
    }

    public void setLaundryIADL(String laundryIADL) {
        this.laundryIADL = laundryIADL;
    }

    public String getTelephoneIADL() {
        return telephoneIADL;
    }

    public void setTelephoneIADL(String telephoneIADL) {
        this.telephoneIADL = telephoneIADL;
    }

    public String getCookingConcerns() {
        return cookingConcerns;
    }

    public void setCookingConcerns(String cookingConcerns) {
        this.cookingConcerns = cookingConcerns;
    }

    public String getShoppingConcerns() {
        return shoppingConcerns;
    }

    public void setShoppingConcerns(String shoppingConcerns) {
        this.shoppingConcerns = shoppingConcerns;
    }

    public String getPayingBills() {
        return payingBills;
    }

    public void setPayingBills(String payingBills) {
        this.payingBills = payingBills;
    }

    public String getAppointmentsAssistance() {
        return appointmentsAssistance;
    }

    public void setAppointmentsAssistance(String appointmentsAssistance) {
        this.appointmentsAssistance = appointmentsAssistance;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getLivingConditions() {
        return livingConditions;
    }

    public void setLivingConditions(List<String> livingConditions) {
        this.livingConditions = livingConditions;
    }

    public String getTransportationAssistance() {
        return transportationAssistance;
    }

    public void setTransportationAssistance(String transportationAssistance) {
        this.transportationAssistance = transportationAssistance;
    }

    public List<String> getHomeServices() {
        return homeServices;
    }

    public void setHomeServices(List<String> homeServices) {
        this.homeServices = homeServices;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPrimaryCarePhysicianFirstName() {
        return primaryCarePhysicianFirstName;
    }

    public void setPrimaryCarePhysicianFirstName(String primaryCarePhysicianFirstName) {
        this.primaryCarePhysicianFirstName = primaryCarePhysicianFirstName;
    }

    public String getPrimaryCarePhysicianLastName() {
        return primaryCarePhysicianLastName;
    }

    public void setPrimaryCarePhysicianLastName(String primaryCarePhysicianLastName) {
        this.primaryCarePhysicianLastName = primaryCarePhysicianLastName;
    }

    public String getPrimaryCarePhysicianPhoneNumber() {
        return primaryCarePhysicianPhoneNumber;
    }

    public void setPrimaryCarePhysicianPhoneNumber(String primaryCarePhysicianPhoneNumber) {
        this.primaryCarePhysicianPhoneNumber = primaryCarePhysicianPhoneNumber;
    }

    public String getPrimaryCarePhysicianAddressStreet() {
        return primaryCarePhysicianAddressStreet;
    }

    public void setPrimaryCarePhysicianAddressStreet(String primaryCarePhysicianAddressStreet) {
        this.primaryCarePhysicianAddressStreet = primaryCarePhysicianAddressStreet;
    }

    public String getPrimaryCarePhysicianAddressCity() {
        return primaryCarePhysicianAddressCity;
    }

    public void setPrimaryCarePhysicianAddressCity(String primaryCarePhysicianAddressCity) {
        this.primaryCarePhysicianAddressCity = primaryCarePhysicianAddressCity;
    }

    public String getPrimaryCarePhysicianAddressState() {
        return primaryCarePhysicianAddressState;
    }

    public void setPrimaryCarePhysicianAddressState(String primaryCarePhysicianAddressState) {
        this.primaryCarePhysicianAddressState = primaryCarePhysicianAddressState;
    }

    public String getPrimaryCarePhysicianAddressZipCode() {
        return primaryCarePhysicianAddressZipCode;
    }

    public void setPrimaryCarePhysicianAddressZipCode(String primaryCarePhysicianAddressZipCode) {
        this.primaryCarePhysicianAddressZipCode = primaryCarePhysicianAddressZipCode;
    }

    public String getSpecialtyPhysicianFirstName() {
        return specialtyPhysicianFirstName;
    }

    public void setSpecialtyPhysicianFirstName(String specialtyPhysicianFirstName) {
        this.specialtyPhysicianFirstName = specialtyPhysicianFirstName;
    }

    public String getSpecialtyPhysicianLastName() {
        return specialtyPhysicianLastName;
    }

    public void setSpecialtyPhysicianLastName(String specialtyPhysicianLastName) {
        this.specialtyPhysicianLastName = specialtyPhysicianLastName;
    }

    public String getSpecialtyPhysicianSpecialty() {
        return specialtyPhysicianSpecialty;
    }

    public void setSpecialtyPhysicianSpecialty(String specialtyPhysicianSpecialty) {
        this.specialtyPhysicianSpecialty = specialtyPhysicianSpecialty;
    }

    public String getSpecialtyPhysicianPhoneNumber() {
        return specialtyPhysicianPhoneNumber;
    }

    public void setSpecialtyPhysicianPhoneNumber(String specialtyPhysicianPhoneNumber) {
        this.specialtyPhysicianPhoneNumber = specialtyPhysicianPhoneNumber;
    }

    public String getSpecialtyPhysicianAddressStreet() {
        return specialtyPhysicianAddressStreet;
    }

    public void setSpecialtyPhysicianAddressStreet(String specialtyPhysicianAddressStreet) {
        this.specialtyPhysicianAddressStreet = specialtyPhysicianAddressStreet;
    }

    public String getSpecialtyPhysicianAddressCity() {
        return specialtyPhysicianAddressCity;
    }

    public void setSpecialtyPhysicianAddressCity(String specialtyPhysicianAddressCity) {
        this.specialtyPhysicianAddressCity = specialtyPhysicianAddressCity;
    }

    public String getSpecialtyPhysicianAddressState() {
        return specialtyPhysicianAddressState;
    }

    public void setSpecialtyPhysicianAddressState(String specialtyPhysicianAddressState) {
        this.specialtyPhysicianAddressState = specialtyPhysicianAddressState;
    }

    public String getSpecialtyPhysicianAddressZipCode() {
        return specialtyPhysicianAddressZipCode;
    }

    public void setSpecialtyPhysicianAddressZipCode(String specialtyPhysicianAddressZipCode) {
        this.specialtyPhysicianAddressZipCode = specialtyPhysicianAddressZipCode;
    }

    public EmergencyContact getEmergencyContact1() {
        return emergencyContact1;
    }

    public void setEmergencyContact1(EmergencyContact emergencyContact1) {
        this.emergencyContact1 = emergencyContact1;
    }

    public EmergencyContact getEmergencyContact2() {
        return emergencyContact2;
    }

    public void setEmergencyContact2(EmergencyContact emergencyContact2) {
        this.emergencyContact2 = emergencyContact2;
    }

    public EmergencyContact getEmergencyContact3() {
        return emergencyContact3;
    }

    public void setEmergencyContact3(EmergencyContact emergencyContact3) {
        this.emergencyContact3 = emergencyContact3;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyPhoneNumber() {
        return pharmacyPhoneNumber;
    }

    public void setPharmacyPhoneNumber(String pharmacyPhoneNumber) {
        this.pharmacyPhoneNumber = pharmacyPhoneNumber;
    }

    public String getPharmacyAddressStreet() {
        return pharmacyAddressStreet;
    }

    public void setPharmacyAddressStreet(String pharmacyAddressStreet) {
        this.pharmacyAddressStreet = pharmacyAddressStreet;
    }

    public String getPharmacyAddressCity() {
        return pharmacyAddressCity;
    }

    public void setPharmacyAddressCity(String pharmacyAddressCity) {
        this.pharmacyAddressCity = pharmacyAddressCity;
    }

    public String getPharmacyAddressState() {
        return pharmacyAddressState;
    }

    public void setPharmacyAddressState(String pharmacyAddressState) {
        this.pharmacyAddressState = pharmacyAddressState;
    }

    public String getPharmacyAddressZipCode() {
        return pharmacyAddressZipCode;
    }

    public void setPharmacyAddressZipCode(String pharmacyAddressZipCode) {
        this.pharmacyAddressZipCode = pharmacyAddressZipCode;
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
}
