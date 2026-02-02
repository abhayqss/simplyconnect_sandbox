package com.scnsoft.eldermark.entity.inbound.therap.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Calendar;

public class TherapIdfCSV {

    private static final String DATE_PATTERN = "MM/dd/yyyy";

    @CsvBindByName(column = "IDFFORMID")
    private String idFormId;

    @CsvBindByName(column = "PROVIDERCODE")
    private String providerCode;

    @CsvBindByName(column = "FIRSTNAME")
    private String firstName;

    @CsvBindByName(column = "LASTNAME")
    private String lastName;

    @CsvBindByName(column = "MIDDLENAME")
    private String middleName;

    @CsvBindByName(column = "SUFFIX")
    private String suffix;

    @CsvBindByName(column = "STATUS")
    private String status;

    @CsvBindByName(column = "GOESBY")
    private String goesBy;

    @CsvBindByName(column = "IDTYPE")
    private String idType;

    @CsvBindByName(column = "IDNUMBER")
    private String idNumber;

    @CsvBindByName(column = "ADDITIONALIDTYPE")
    private String additionalIdType;

    @CsvBindByName(column = "ADDITIONALIDNUMBER")
    private String additionalIdNumber;

    @CsvBindByName(column = "SSN")
    private String ssn;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "BIRTHDATE")
    private Calendar birthDate;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "ADMISSIONDATE")
    private Calendar admissionDate;

    @CsvBindByName(column = "ATTENTION")
    private String attention;

    @CsvBindByName(column = "STREET1")
    private String street1;

    @CsvBindByName(column = "STREET2")
    private String street2;

    @CsvBindByName(column = "CITY")
    private String city;

    @CsvBindByName(column = "COUNTY")
    private String county;

    @CsvBindByName(column = "STATE")
    private String state;

    @CsvBindByName(column = "ZIP")
    private String zip;

    @CsvBindByName(column = "COUNTRY")
    private String country;

    @CsvBindByName(column = "BIRTHPLACECITY")
    private String birthPlaceCity;

    @CsvBindByName(column = "BIRTHPLACESTATE")
    private String birthPlaceState;

    @CsvBindByName(column = "BIRTHPLACECOUNTRY")
    private String birthplaceCountry;

    @CsvBindByName(column = "CITIZENSHIP")
    private String citizenship;

    @CsvBindByName(column = "GENDER")
    private String gender;

    @CsvBindByName(column = "ETHNICITY")
    private String ethnicity;

    @CsvBindByName(column = "MARITALSTATUS")
    private String maritalStatus;

    @CsvBindByName(column = "MARITALSTATUSDATE")
    private String maritalStatusDate;

    @CsvBindByName(column = "RACE")
    private String race;

    @CsvBindByName(column = "HEIGHTINCH")
    private float heightInch;

    @CsvBindByName(column = "HEIGHTFEET")
    private float heightFeet;

    @CsvBindByName(column = "WEIGHTRANGETO")
    private float weightRangeTo;

    @CsvBindByName(column = "WEIGHTRANGEFROM")
    private float weightRangeFrom;

    @CsvBindByName(column = "HAIRCOLOR")
    private String hairColor;

    @CsvBindByName(column = "EYECOLOR")
    private String eyeColor;

    @CsvBindByName(column = "CHARACTERISTICS")
    private String characteristics;

    @CsvBindByName(column = "COMMUNICATIONMODALITY")
    private String communicationModality;

    @CsvBindByName(column = "COMMUNICATIONCOMMENTS")
    private String communicationComments;

    @CsvBindByName(column = "ORALLANGUAGE")
    private String oralLanguage;

    @CsvBindByName(column = "WRITTENLANGUAGE")
    private String writtenLanguage;

    @CsvBindByName(column = "INTERPRETER")
    private String interpreter;

    @CsvBindByName(column = "RELIGION")
    private String religion;

    @CsvBindByName(column = "LIVINGARRANGEMENT")
    private String livingArrangement;

    @CsvBindByName(column = "PRIMARYCAREPHYSICIAN")
    private String primaryCarePhysician;

    @CsvBindByName(column = "MEALTIMESTATUS")
    private String mealTimeStatus;

    @CsvBindByName(column = "DIETARYGUIDELINES")
    private String deitaryGuidelines;

    @CsvBindByName(column = "FOODTEXTURE")
    private String foodTexture;

    @CsvBindByName(column = "FEEDINGGUIDELINES")
    private String feedingGuidelines;

    @CsvBindByName(column = "MOBILITY")
    private String mobility;

    @CsvBindByName(column = "MOBILITYCOMMENTS")
    private String mobilityComments;

    @CsvBindByName(column = "SUPERVISION")
    private String supervision;

    @CsvBindByName(column = "SUPERVISIONCOMMENTS")
    private String supervisionComments;

    @CsvBindByName(column = "REFERRALSOURCE")
    private String referralSource;

    @CsvBindByName(column = "TOILETINGSTATUS")
    private String toiletingStatus;

    @CsvBindByName(column = "BATHINGSTATUS")
    private String bathingStatus;

    @CsvBindByName(column = "GUARDIANOFSELF")
    private String guardianOnSelf;

    @CsvBindByName(column = "BLOODTYPE")
    private String bloodType;

    @CsvBindByName(column = "EMERGENCYORDERS")
    private String emergencyOrders;

    @CsvBindByName(column = "ALLERGIES")
    private String allergies;

    @CsvBindByName(column = "ALLERGIESCOMMENTS")
    private String allergiesComments;

    @CsvBindByName(column = "ADAPTIVEEQUIPMENT")
    private String adaptiveEquipments;

    @CsvBindByName(column = "MEDICAIDNUMBER")
    private String medicaidNumber;

    @CsvBindByName(column = "MEDICARENUMBER")
    private String medicareNumber;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "MEDICAREEFFECTIVEDATE")
    private Calendar medicareEffectiveDate;

    @CsvBindByName(column = "MEDICARESECTION")
    private String medicareSection;

    @CsvBindByName(column = "MEDPLANID")
    private String medPlanId;

    @CsvBindByName(column = "MEDPLANPLANNAME")
    private String medPlanName;

    @CsvBindByName(column = "MEDPLANISSUER")
    private String medPlanIssuer;

    @CsvBindByName(column = "MEDPLANRXBIN")
    private String medPlanRxBin;

    @CsvBindByName(column = "MEDPLANRXPCN")
    private String medPlanRxPcn;

    @CsvBindByName(column = "MEDPLANRXGRP")
    private String medPlanRxGrp;

    @CsvBindByName(column = "OTHERBENEFITS")
    private String otherBenefits;

    @CsvBindByName(column = "INSURANCECOMPANY")
    private String insuranceCompany;

    @CsvBindByName(column = "INSURANCEGROUP")
    private String insuranceGroup;

    @CsvBindByName(column = "INSURANCEPOLICYNUMBER")
    private String insurancePolicyNumber;

    @CsvBindByName(column = "INSURANCEPOLICYHOLDER")
    private String insurancePolicyHolder;

    @CsvBindByName(column = "BEHAVIORMANAGEMENT")
    private String behaviourManagement;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "CREATED")
    private Calendar created;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "UPDATED")
    private Calendar updated;

    @CsvBindByName(column = "TIMEZONE")
    private String timezone;

    @CsvBindByName(column = "UNSUBSCRIBE")
    private boolean unsubscribe;

    public String getIdFormId() {
        return idFormId;
    }

    public void setIdFormId(String idFormId) {
        this.idFormId = idFormId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGoesBy() {
        return goesBy;
    }

    public void setGoesBy(String goesBy) {
        this.goesBy = goesBy;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAdditionalIdType() {
        return additionalIdType;
    }

    public void setAdditionalIdType(String additionalIdType) {
        this.additionalIdType = additionalIdType;
    }

    public String getAdditionalIdNumber() {
        return additionalIdNumber;
    }

    public void setAdditionalIdNumber(String additionalIdNumber) {
        this.additionalIdNumber = additionalIdNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Calendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Calendar birthDate) {
        this.birthDate = birthDate;
    }

    public Calendar getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Calendar admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBirthPlaceCity() {
        return birthPlaceCity;
    }

    public void setBirthPlaceCity(String birthPlaceCity) {
        this.birthPlaceCity = birthPlaceCity;
    }

    public String getBirthPlaceState() {
        return birthPlaceState;
    }

    public void setBirthPlaceState(String birthPlaceState) {
        this.birthPlaceState = birthPlaceState;
    }

    public String getBirthplaceCountry() {
        return birthplaceCountry;
    }

    public void setBirthplaceCountry(String birthplaceCountry) {
        this.birthplaceCountry = birthplaceCountry;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatusDate() {
        return maritalStatusDate;
    }

    public void setMaritalStatusDate(String maritalStatusDate) {
        this.maritalStatusDate = maritalStatusDate;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public float getHeightInch() {
        return heightInch;
    }

    public void setHeightInch(float heightInch) {
        this.heightInch = heightInch;
    }

    public float getHeightFeet() {
        return heightFeet;
    }

    public void setHeightFeet(float heightFeet) {
        this.heightFeet = heightFeet;
    }

    public float getWeightRangeTo() {
        return weightRangeTo;
    }

    public void setWeightRangeTo(float weightRangeTo) {
        this.weightRangeTo = weightRangeTo;
    }

    public float getWeightRangeFrom() {
        return weightRangeFrom;
    }

    public void setWeightRangeFrom(float weightRangeFrom) {
        this.weightRangeFrom = weightRangeFrom;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getCommunicationModality() {
        return communicationModality;
    }

    public void setCommunicationModality(String communicationModality) {
        this.communicationModality = communicationModality;
    }

    public String getCommunicationComments() {
        return communicationComments;
    }

    public void setCommunicationComments(String communicationComments) {
        this.communicationComments = communicationComments;
    }

    public String getOralLanguage() {
        return oralLanguage;
    }

    public void setOralLanguage(String oralLanguage) {
        this.oralLanguage = oralLanguage;
    }

    public String getWrittenLanguage() {
        return writtenLanguage;
    }

    public void setWrittenLanguage(String writtenLanguage) {
        this.writtenLanguage = writtenLanguage;
    }

    public String getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getLivingArrangement() {
        return livingArrangement;
    }

    public void setLivingArrangement(String livingArrangement) {
        this.livingArrangement = livingArrangement;
    }

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public String getMealTimeStatus() {
        return mealTimeStatus;
    }

    public void setMealTimeStatus(String mealTimeStatus) {
        this.mealTimeStatus = mealTimeStatus;
    }

    public String getDeitaryGuidelines() {
        return deitaryGuidelines;
    }

    public void setDeitaryGuidelines(String deitaryGuidelines) {
        this.deitaryGuidelines = deitaryGuidelines;
    }

    public String getFoodTexture() {
        return foodTexture;
    }

    public void setFoodTexture(String foodTexture) {
        this.foodTexture = foodTexture;
    }

    public String getFeedingGuidelines() {
        return feedingGuidelines;
    }

    public void setFeedingGuidelines(String feedingGuidelines) {
        this.feedingGuidelines = feedingGuidelines;
    }

    public String getMobility() {
        return mobility;
    }

    public void setMobility(String mobility) {
        this.mobility = mobility;
    }

    public String getMobilityComments() {
        return mobilityComments;
    }

    public void setMobilityComments(String mobilityComments) {
        this.mobilityComments = mobilityComments;
    }

    public String getSupervision() {
        return supervision;
    }

    public void setSupervision(String supervision) {
        this.supervision = supervision;
    }

    public String getSupervisionComments() {
        return supervisionComments;
    }

    public void setSupervisionComments(String supervisionComments) {
        this.supervisionComments = supervisionComments;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getToiletingStatus() {
        return toiletingStatus;
    }

    public void setToiletingStatus(String toiletingStatus) {
        this.toiletingStatus = toiletingStatus;
    }

    public String getBathingStatus() {
        return bathingStatus;
    }

    public void setBathingStatus(String bathingStatus) {
        this.bathingStatus = bathingStatus;
    }

    public String getGuardianOnSelf() {
        return guardianOnSelf;
    }

    public void setGuardianOnSelf(String guardianOnSelf) {
        this.guardianOnSelf = guardianOnSelf;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getEmergencyOrders() {
        return emergencyOrders;
    }

    public void setEmergencyOrders(String emergencyOrders) {
        this.emergencyOrders = emergencyOrders;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getAllergiesComments() {
        return allergiesComments;
    }

    public void setAllergiesComments(String allergiesComments) {
        this.allergiesComments = allergiesComments;
    }

    public String getAdaptiveEquipments() {
        return adaptiveEquipments;
    }

    public void setAdaptiveEquipments(String adaptiveEquipments) {
        this.adaptiveEquipments = adaptiveEquipments;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public Calendar getMedicareEffectiveDate() {
        return medicareEffectiveDate;
    }

    public void setMedicareEffectiveDate(Calendar medicareEffectiveDate) {
        this.medicareEffectiveDate = medicareEffectiveDate;
    }

    public String getMedicareSection() {
        return medicareSection;
    }

    public void setMedicareSection(String medicareSection) {
        this.medicareSection = medicareSection;
    }

    public String getMedPlanId() {
        return medPlanId;
    }

    public void setMedPlanId(String medPlanId) {
        this.medPlanId = medPlanId;
    }

    public String getMedPlanName() {
        return medPlanName;
    }

    public void setMedPlanName(String medPlanName) {
        this.medPlanName = medPlanName;
    }

    public String getMedPlanIssuer() {
        return medPlanIssuer;
    }

    public void setMedPlanIssuer(String medPlanIssuer) {
        this.medPlanIssuer = medPlanIssuer;
    }

    public String getMedPlanRxBin() {
        return medPlanRxBin;
    }

    public void setMedPlanRxBin(String medPlanRxBin) {
        this.medPlanRxBin = medPlanRxBin;
    }

    public String getMedPlanRxPcn() {
        return medPlanRxPcn;
    }

    public void setMedPlanRxPcn(String medPlanRxPcn) {
        this.medPlanRxPcn = medPlanRxPcn;
    }

    public String getMedPlanRxGrp() {
        return medPlanRxGrp;
    }

    public void setMedPlanRxGrp(String medPlanRxGrp) {
        this.medPlanRxGrp = medPlanRxGrp;
    }

    public String getOtherBenefits() {
        return otherBenefits;
    }

    public void setOtherBenefits(String otherBenefits) {
        this.otherBenefits = otherBenefits;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceGroup() {
        return insuranceGroup;
    }

    public void setInsuranceGroup(String insuranceGroup) {
        this.insuranceGroup = insuranceGroup;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public String getInsurancePolicyHolder() {
        return insurancePolicyHolder;
    }

    public void setInsurancePolicyHolder(String insurancePolicyHolder) {
        this.insurancePolicyHolder = insurancePolicyHolder;
    }

    public String getBehaviourManagement() {
        return behaviourManagement;
    }

    public void setBehaviourManagement(String behaviourManagement) {
        this.behaviourManagement = behaviourManagement;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(boolean unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    @Override
    public String toString() {
        return "TherapIdfCSV{" +
                "idFormId='" + idFormId + '\'' +
                ", providerCode='" + providerCode + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", status='" + status + '\'' +
                ", goesBy='" + goesBy + '\'' +
                ", idType='" + idType + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", additionalIdType='" + additionalIdType + '\'' +
                ", additionalIdNumber='" + additionalIdNumber + '\'' +
                ", ssn='" + ssn + '\'' +
                ", birthDate=" + birthDate +
                ", admissionDate=" + admissionDate +
                ", attention='" + attention + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", birthPlaceCity='" + birthPlaceCity + '\'' +
                ", birthPlaceState='" + birthPlaceState + '\'' +
                ", birthplaceCountry='" + birthplaceCountry + '\'' +
                ", citizenship='" + citizenship + '\'' +
                ", gender='" + gender + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", maritalStatusDate='" + maritalStatusDate + '\'' +
                ", race='" + race + '\'' +
                ", heightInch=" + heightInch +
                ", heightFeet=" + heightFeet +
                ", weightRangeTo=" + weightRangeTo +
                ", weightRangeFrom=" + weightRangeFrom +
                ", hairColor='" + hairColor + '\'' +
                ", eyeColor='" + eyeColor + '\'' +
                ", characteristics='" + characteristics + '\'' +
                ", communicationModality='" + communicationModality + '\'' +
                ", communicationComments='" + communicationComments + '\'' +
                ", oralLanguage='" + oralLanguage + '\'' +
                ", writtenLanguage='" + writtenLanguage + '\'' +
                ", interpreter='" + interpreter + '\'' +
                ", religion='" + religion + '\'' +
                ", livingArrangement='" + livingArrangement + '\'' +
                ", primaryCarePhysician='" + primaryCarePhysician + '\'' +
                ", mealTimeStatus='" + mealTimeStatus + '\'' +
                ", deitaryGuidelines='" + deitaryGuidelines + '\'' +
                ", foodTexture='" + foodTexture + '\'' +
                ", feedingGuidelines='" + feedingGuidelines + '\'' +
                ", mobility='" + mobility + '\'' +
                ", mobilityComments='" + mobilityComments + '\'' +
                ", supervision='" + supervision + '\'' +
                ", supervisionComments='" + supervisionComments + '\'' +
                ", referralSource='" + referralSource + '\'' +
                ", toiletingStatus='" + toiletingStatus + '\'' +
                ", bathingStatus='" + bathingStatus + '\'' +
                ", guardianOnSelf='" + guardianOnSelf + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", emergencyOrders='" + emergencyOrders + '\'' +
                ", allergies='" + allergies + '\'' +
                ", allergiesComments='" + allergiesComments + '\'' +
                ", adaptiveEquipments='" + adaptiveEquipments + '\'' +
                ", medicaidNumber='" + medicaidNumber + '\'' +
                ", medicareNumber='" + medicareNumber + '\'' +
                ", medicareEffectiveDate=" + medicareEffectiveDate +
                ", medicareSection='" + medicareSection + '\'' +
                ", medPlanId='" + medPlanId + '\'' +
                ", medPlanName='" + medPlanName + '\'' +
                ", medPlanIssuer='" + medPlanIssuer + '\'' +
                ", medPlanRxBin='" + medPlanRxBin + '\'' +
                ", medPlanRxPcn='" + medPlanRxPcn + '\'' +
                ", medPlanRxGrp='" + medPlanRxGrp + '\'' +
                ", otherBenefits='" + otherBenefits + '\'' +
                ", insuranceCompany='" + insuranceCompany + '\'' +
                ", insuranceGroup='" + insuranceGroup + '\'' +
                ", insurancePolicyNumber='" + insurancePolicyNumber + '\'' +
                ", insurancePolicyHolder='" + insurancePolicyHolder + '\'' +
                ", behaviourManagement='" + behaviourManagement + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", timezone='" + timezone + '\'' +
                ", unsubscribe=" + unsubscribe +
                '}';
    }
}
