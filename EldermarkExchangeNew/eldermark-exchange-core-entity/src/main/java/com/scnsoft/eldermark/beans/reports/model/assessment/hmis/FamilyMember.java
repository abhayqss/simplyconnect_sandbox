package com.scnsoft.eldermark.beans.reports.model.assessment.hmis;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ADDRESS_CITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ADDRESS_STATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ADDRESS_STREET;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_AFGHANISTAN_OPERATIONS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ALIAS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ALIMONY_SPOUSAL_SUPPORT_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_BARRIERS_TO_ENROLLING_SCHOOL;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_BARRIERS_TO_ENROLLING_SCHOOL_OTHER;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_CASH_INCOME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_CHILD_SUPPORT_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_CHRONIC_HEALTH_CONDITION;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_COVERED_SOURCE_HEALTH_INSURANCE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DEV_DISABILITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DISABLING_CONDITION;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DISCHARGE_STATUS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DISCHARGE_STATUS_VERIFIED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DOB;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DOMESTIC_VIOLENCE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DOMESTIC_VIOLENCE_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_DUE_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_EMAIL;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_EMPLOYED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_EMPLOYMENT_INCOME_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_EMPLOYMENT_TENURE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_EPISODE_START_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ETHNICITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_FIRSTNAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_FLEEING;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_GA_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_GENDER;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_GENDER_IDENTITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_GENDER_NON_CONFORMING;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_GENERAL_HEALTH;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HEALTH_INSURANCE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HIGHEST_DEGREE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HIGHEST_LEVEL;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HIV_AIDS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HOMELESS_REASON;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HOURS_WORKED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_HUD_HOMELESS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IF_NOT_ENROLLED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IF_SO_MOVE_IN_DATE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IMPAIRS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IMPAIRS_2;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IMPAIRS_ABILITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IMPAIRS_ABILITY_2;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IRAQ_NEW;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_IRAQ_OPERATIONS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_KOREAN_WAR;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_LASTNAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_LENGTH_OF_STAYING;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_MENTAL_HEALTH;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_MIDDLENAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_MILITARY_BRANCH;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_MOVED_PERMANENT_HOUSING;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_NON_CASH_BENEFITS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_ECARE_SCHOOL_LUNCH;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_HOMELESS_REASON;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_KIDS_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_OPERATIONS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_RESIDENCE_PRIOR;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_SOURCE_HEALTH_INSURANCE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_OTHER_TANF_BENEFITS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PENSION_FROM_FORMER_FOB_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PERSIAN_GULF_WAR;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PHONE_NUMBER;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PHYSICAL_DISABILITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PREGNANT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PRIMARY_LANG;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_PRIVATE_DISABILITY_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_QUALITY_OF_DOB;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_QUALITY_OF_NAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_QUALITY_OF_SSN;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_QUALITY_OF_ZIP;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_RACE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_RELATIONSHIP_TO_HOUSEHOLD;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_RESIDENCE_PRIOR;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SCHOOL_ENROLLED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SCHOOL_NAME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_CHC;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_DEV_DISABILITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_HIV_AIDS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_MH;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_PHYSICAL_DISABILITY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SERVICES_SA;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SEXUAL_ORIENTATION;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SOURCES_NON_CASH_BENEFITS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SOURCE_CASH_INCOME;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SSA_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SSDI_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SSI_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SSN;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SUBSTANCE_ABUSE;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_SUFFIX;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_TANF_CAL_WORKS_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_TIMES_HOMELESS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_TOTAL_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_TOTAL_MONTHS_HOMELESS;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_TYPE_OF_SCHOOL;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_UNEMPLOYED_SEEKING;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_UNEMPLOYMENT_INS_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_US_CITIZEN;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_VAD_DISABILITY_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_VA_PENSION_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_VETERAN;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_VIETNAM_WAR;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_VOCATIONAL_ENROLLED;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_WORKERS_COMP_AMOUNT;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_WW2;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_YEAR_ENTERED_MILITARY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_YEAR_SEPARATED_MILITARY;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.FAMILY_MEMBER_ZIP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyMember {
    @JsonProperty(FAMILY_MEMBER_RELATIONSHIP_TO_HOUSEHOLD)
    private String relationshipToHouseHold;
    @JsonProperty(FAMILY_MEMBER_SSN)
    private String ssn;
    @JsonProperty(FAMILY_MEMBER_QUALITY_OF_SSN)
    private String qualityOfSSN;
    @JsonProperty(FAMILY_MEMBER_DOB)
    private String dateOfBirthday;
    @JsonProperty(FAMILY_MEMBER_QUALITY_OF_DOB)
    private String qualityOfDOB;
    @JsonProperty(FAMILY_MEMBER_LASTNAME)
    private String lastName;
    @JsonProperty(FAMILY_MEMBER_FIRSTNAME)
    private String firstName;
    @JsonProperty(FAMILY_MEMBER_MIDDLENAME)
    private String middleName;
    @JsonProperty(FAMILY_MEMBER_QUALITY_OF_NAME)
    private String qualityOfName;
    @JsonProperty(FAMILY_MEMBER_SUFFIX)
    private String suffix;
    @JsonProperty(FAMILY_MEMBER_GENDER)
    private String gender;
    @JsonProperty(FAMILY_MEMBER_RACE)
    private String race;
    @JsonProperty(FAMILY_MEMBER_ETHNICITY)
    private String ethnicity;
    @JsonProperty(FAMILY_MEMBER_ZIP)
    private String zip;
    @JsonProperty(FAMILY_MEMBER_QUALITY_OF_ZIP)
    private String qualityOfZip;
    @JsonProperty(FAMILY_MEMBER_PRIMARY_LANG)
    private String primaryLanguage;
    @JsonProperty(FAMILY_MEMBER_US_CITIZEN)
    private String UsCitizen;
    @JsonProperty(FAMILY_MEMBER_VETERAN)
    private String veteran;
    @JsonProperty(FAMILY_MEMBER_YEAR_ENTERED_MILITARY)
    private String yearEnteredMilitary;
    @JsonProperty(FAMILY_MEMBER_YEAR_SEPARATED_MILITARY)
    private String yearSeparatedMilitary;
    @JsonProperty(FAMILY_MEMBER_WW2)
    private String worldWarParticipant;
    @JsonProperty(FAMILY_MEMBER_KOREAN_WAR)
    private String koreanWarParticipant;
    @JsonProperty(FAMILY_MEMBER_VIETNAM_WAR)
    private String vietnamWarParticipant;
    @JsonProperty(FAMILY_MEMBER_PERSIAN_GULF_WAR)
    private String persianGulfWar;
    @JsonProperty(FAMILY_MEMBER_AFGHANISTAN_OPERATIONS)
    private String afghanistanOperationsParticipant;
    @JsonProperty(FAMILY_MEMBER_IRAQ_OPERATIONS)
    private String iraqOperations;
    @JsonProperty(FAMILY_MEMBER_IRAQ_NEW)
    private String iraqNewOperations;
    @JsonProperty(FAMILY_MEMBER_OTHER_OPERATIONS)
    private String otherOperations;
    @JsonProperty(FAMILY_MEMBER_MILITARY_BRANCH)
    private String militaryBranch;
    @JsonProperty(FAMILY_MEMBER_DISCHARGE_STATUS)
    private String dischargeStatus;
    @JsonProperty(FAMILY_MEMBER_DISCHARGE_STATUS_VERIFIED)
    private String dischargeStatusVerified;
    @JsonProperty(FAMILY_MEMBER_MOVED_PERMANENT_HOUSING)
    private String isPermanentHousing;
    @JsonProperty(FAMILY_MEMBER_HOMELESS_REASON)
    private String homelessReason;
    @JsonProperty(FAMILY_MEMBER_RESIDENCE_PRIOR)
    private String residencePrior;
    @JsonProperty(FAMILY_MEMBER_EPISODE_START_DATE)
    private String episodeStartDate;
    @JsonProperty(FAMILY_MEMBER_TIMES_HOMELESS)
    private String timesHomeless;
    @JsonProperty(FAMILY_MEMBER_PHYSICAL_DISABILITY)
    private String hasPhysicalDisability;
    @JsonProperty(FAMILY_MEMBER_SERVICES_PHYSICAL_DISABILITY)
    private String hasServicesPhysicalDisability;
    @JsonProperty(FAMILY_MEMBER_IMPAIRS)
    private String impairs;
    @JsonProperty(FAMILY_MEMBER_SERVICES_DEV_DISABILITY)
    private String hasServicesDevDisability;
    @JsonProperty(FAMILY_MEMBER_DEV_DISABILITY)
    private String devDisability;
    @JsonProperty(FAMILY_MEMBER_CHRONIC_HEALTH_CONDITION)
    private String chronicHealthCondition;
    @JsonProperty(FAMILY_MEMBER_SERVICES_CHC)
    private String hasServicesCHC;
    @JsonProperty(FAMILY_MEMBER_IMPAIRS_2)
    private String impairs2;
    @JsonProperty(FAMILY_MEMBER_HIV_AIDS)
    private String hivAids;
    @JsonProperty(FAMILY_MEMBER_SERVICES_HIV_AIDS)
    private String hasServicesHivAids;
    @JsonProperty(FAMILY_MEMBER_MENTAL_HEALTH)
    private String mentalHealth;
    @JsonProperty(FAMILY_MEMBER_SERVICES_MH)
    private String hasServicesMH;
    @JsonProperty(FAMILY_MEMBER_IMPAIRS_ABILITY)
    private String impairsAbility;
    @JsonProperty(FAMILY_MEMBER_SUBSTANCE_ABUSE)
    private String substanceAbuse;
    @JsonProperty(FAMILY_MEMBER_SERVICES_SA)
    private String hasServicesSA;
    @JsonProperty(FAMILY_MEMBER_IMPAIRS_ABILITY_2)
    private String impairsAbility2;
    @JsonProperty(FAMILY_MEMBER_DISABLING_CONDITION)
    private String disablingCondition;
    @JsonProperty(FAMILY_MEMBER_DOMESTIC_VIOLENCE)
    private String domesticViolence;
    @JsonProperty(FAMILY_MEMBER_FLEEING)
    private String fleeing;
    @JsonProperty(FAMILY_MEMBER_CASH_INCOME)
    private String cashIncome;
    @JsonProperty(FAMILY_MEMBER_NON_CASH_BENEFITS)
    private String nonCashBenefits;
    @JsonProperty(FAMILY_MEMBER_HEALTH_INSURANCE)
    private String healthInsurance;
    @JsonProperty(FAMILY_MEMBER_EMPLOYED)
    private String employed;
    @JsonProperty(FAMILY_MEMBER_EMPLOYMENT_TENURE)
    private String employmentTenure;
    @JsonProperty(FAMILY_MEMBER_HOURS_WORKED)
    private String hoursWorked;
    @JsonProperty(FAMILY_MEMBER_SCHOOL_ENROLLED)
    private String schoolEnrolled;
    @JsonProperty(FAMILY_MEMBER_IF_NOT_ENROLLED)
    private String ifNotEnrolled;
    @JsonProperty(FAMILY_MEMBER_VOCATIONAL_ENROLLED)
    private String vocationalEnrolled;
    @JsonProperty(FAMILY_MEMBER_HIGHEST_LEVEL)
    private String highestLevel;
    @JsonProperty(FAMILY_MEMBER_HIGHEST_DEGREE)
    private String highestDegree;
    @JsonProperty(FAMILY_MEMBER_SCHOOL_NAME)
    private String schoolName;
    @JsonProperty(FAMILY_MEMBER_HUD_HOMELESS)
    private String hudHomeless;
    @JsonProperty(FAMILY_MEMBER_TYPE_OF_SCHOOL)
    private String typeOfSchool;
    @JsonProperty(FAMILY_MEMBER_IF_SO_MOVE_IN_DATE)
    private String moveInDate;
    @JsonProperty(FAMILY_MEMBER_LENGTH_OF_STAYING)
    private String lengthOfStaying;
    @JsonProperty(FAMILY_MEMBER_TOTAL_MONTHS_HOMELESS)
    private String totalMonthsHomeless;
    @JsonProperty(FAMILY_MEMBER_PREGNANT)
    private String pregnant;
    @JsonProperty(FAMILY_MEMBER_DUE_DATE)
    private String dueDate;
    @JsonProperty(FAMILY_MEMBER_GENERAL_HEALTH)
    private String generalHealth;
    @JsonProperty(FAMILY_MEMBER_BARRIERS_TO_ENROLLING_SCHOOL)
    private List<String> barriersToEnrollingChildInSchool;
    @JsonProperty(FAMILY_MEMBER_ALIAS)
    private String alias;
    @JsonProperty(FAMILY_MEMBER_SEXUAL_ORIENTATION)
    private String sexualOrientation;
    @JsonProperty(FAMILY_MEMBER_GENDER_IDENTITY)
    private String genderIdentity;
    @JsonProperty(FAMILY_MEMBER_GENDER_NON_CONFORMING)
    private String genderNonConforming;
    @JsonProperty(FAMILY_MEMBER_OTHER_HOMELESS_REASON)
    private String otherHomelessReason;
    @JsonProperty(FAMILY_MEMBER_OTHER_RESIDENCE_PRIOR)
    private String otherResidencePrior;
    @JsonProperty(FAMILY_MEMBER_DOMESTIC_VIOLENCE_DATE)
    private String domesticViolenceDate;
    @JsonProperty(FAMILY_MEMBER_SOURCE_CASH_INCOME)
    private List<String> cashIncomeSource;
    @JsonProperty(FAMILY_MEMBER_EMPLOYMENT_INCOME_AMOUNT)
    private String employmentIncomeAmount;
    @JsonProperty(FAMILY_MEMBER_UNEMPLOYMENT_INS_AMOUNT)
    private String unemploymentInsAmount;
    @JsonProperty(FAMILY_MEMBER_WORKERS_COMP_AMOUNT)
    private String workersCompAmount;
    @JsonProperty(FAMILY_MEMBER_PRIVATE_DISABILITY_AMOUNT)
    private String privateDisabilityInsAmount;
    @JsonProperty(FAMILY_MEMBER_VAD_DISABILITY_AMOUNT)
    private String vaDisabilityAmount;
    @JsonProperty(FAMILY_MEMBER_SSDI_AMOUNT)
    private String ssdiAmount;
    @JsonProperty(FAMILY_MEMBER_SSI_AMOUNT)
    private String ssiAmount;
    @JsonProperty(FAMILY_MEMBER_SSA_AMOUNT)
    private String ssaAmount;
    @JsonProperty(FAMILY_MEMBER_VA_PENSION_AMOUNT)
    private String vaPensionAmount;
    @JsonProperty(FAMILY_MEMBER_PENSION_FROM_FORMER_FOB_AMOUNT)
    private String pensionFromFormerFobAmount;
    @JsonProperty(FAMILY_MEMBER_TANF_CAL_WORKS_AMOUNT)
    private String tanfCalWorksAmount;
    @JsonProperty(FAMILY_MEMBER_GA_AMOUNT)
    private String gaAmount;
    @JsonProperty(FAMILY_MEMBER_ALIMONY_SPOUSAL_SUPPORT_AMOUNT)
    private String alimonySpousalSupportAmount;
    @JsonProperty(FAMILY_MEMBER_CHILD_SUPPORT_AMOUNT)
    private String childSupportAmount;
    @JsonProperty(FAMILY_MEMBER_OTHER_KIDS_AMOUNT)
    private String otherKidsAmount;
    @JsonProperty(FAMILY_MEMBER_TOTAL_AMOUNT)
    private String totalAmount;
    @JsonProperty(FAMILY_MEMBER_SOURCES_NON_CASH_BENEFITS)
    private String sourcesNonCashBenefits;
    @JsonProperty(FAMILY_MEMBER_OTHER_TANF_BENEFITS)
    private String otherTanfBenefits;
    @JsonProperty(FAMILY_MEMBER_OTHER_ECARE_SCHOOL_LUNCH)
    private String otherExIHSSParatransitPG;
    @JsonProperty(FAMILY_MEMBER_COVERED_SOURCE_HEALTH_INSURANCE)
    private String coveredSourceHealthInsurance;
    @JsonProperty(FAMILY_MEMBER_OTHER_SOURCE_HEALTH_INSURANCE)
    private String otherSourceHealthInsurance;
    @JsonProperty(FAMILY_MEMBER_UNEMPLOYED_SEEKING)
    private String unemployedSeeking;
    @JsonProperty(FAMILY_MEMBER_PHONE_NUMBER)
    private String phoneNumber;
    @JsonProperty(FAMILY_MEMBER_ADDRESS_STREET)
    private String streetAddress;
    @JsonProperty(FAMILY_MEMBER_EMAIL)
    private String email;
    @JsonProperty(FAMILY_MEMBER_ADDRESS_CITY)
    private String city;
    @JsonProperty(FAMILY_MEMBER_ADDRESS_STATE)
    private String state;
    @JsonProperty(FAMILY_MEMBER_BARRIERS_TO_ENROLLING_SCHOOL_OTHER)
    private List<String> barriersToEnrollingChildInSchoolOther;

    public String getRelationshipToHouseHold() {
        return relationshipToHouseHold;
    }

    public void setRelationshipToHouseHold(String relationshipToHouseHold) {
        this.relationshipToHouseHold = relationshipToHouseHold;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getQualityOfSSN() {
        return qualityOfSSN;
    }

    public void setQualityOfSSN(String qualityOfSSN) {
        this.qualityOfSSN = qualityOfSSN;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(String dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public String getQualityOfDOB() {
        return qualityOfDOB;
    }

    public void setQualityOfDOB(String qualityOfDOB) {
        this.qualityOfDOB = qualityOfDOB;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getQualityOfName() {
        return qualityOfName;
    }

    public void setQualityOfName(String qualityOfName) {
        this.qualityOfName = qualityOfName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getQualityOfZip() {
        return qualityOfZip;
    }

    public void setQualityOfZip(String qualityOfZip) {
        this.qualityOfZip = qualityOfZip;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getUsCitizen() {
        return UsCitizen;
    }

    public void setUsCitizen(String usCitizen) {
        UsCitizen = usCitizen;
    }

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }

    public String getYearEnteredMilitary() {
        return yearEnteredMilitary;
    }

    public void setYearEnteredMilitary(String yearEnteredMilitary) {
        this.yearEnteredMilitary = yearEnteredMilitary;
    }

    public String getYearSeparatedMilitary() {
        return yearSeparatedMilitary;
    }

    public void setYearSeparatedMilitary(String yearSeparatedMilitary) {
        this.yearSeparatedMilitary = yearSeparatedMilitary;
    }

    public String getWorldWarParticipant() {
        return worldWarParticipant;
    }

    public void setWorldWarParticipant(String worldWarParticipant) {
        this.worldWarParticipant = worldWarParticipant;
    }

    public String getKoreanWarParticipant() {
        return koreanWarParticipant;
    }

    public void setKoreanWarParticipant(String koreanWarParticipant) {
        this.koreanWarParticipant = koreanWarParticipant;
    }

    public String getVietnamWarParticipant() {
        return vietnamWarParticipant;
    }

    public void setVietnamWarParticipant(String vietnamWarParticipant) {
        this.vietnamWarParticipant = vietnamWarParticipant;
    }

    public String getPersianGulfWar() {
        return persianGulfWar;
    }

    public void setPersianGulfWar(String persianGulfWar) {
        this.persianGulfWar = persianGulfWar;
    }

    public String getAfghanistanOperationsParticipant() {
        return afghanistanOperationsParticipant;
    }

    public void setAfghanistanOperationsParticipant(String afghanistanOperationsParticipant) {
        this.afghanistanOperationsParticipant = afghanistanOperationsParticipant;
    }

    public String getIraqOperations() {
        return iraqOperations;
    }

    public void setIraqOperations(String iraqOperations) {
        this.iraqOperations = iraqOperations;
    }

    public String getIraqNewOperations() {
        return iraqNewOperations;
    }

    public void setIraqNewOperations(String iraqNewOperations) {
        this.iraqNewOperations = iraqNewOperations;
    }

    public String getOtherOperations() {
        return otherOperations;
    }

    public void setOtherOperations(String otherOperations) {
        this.otherOperations = otherOperations;
    }

    public String getMilitaryBranch() {
        return militaryBranch;
    }

    public void setMilitaryBranch(String militaryBranch) {
        this.militaryBranch = militaryBranch;
    }

    public String getDischargeStatus() {
        return dischargeStatus;
    }

    public void setDischargeStatus(String dischargeStatus) {
        this.dischargeStatus = dischargeStatus;
    }

    public String getDischargeStatusVerified() {
        return dischargeStatusVerified;
    }

    public void setDischargeStatusVerified(String dischargeStatusVerified) {
        this.dischargeStatusVerified = dischargeStatusVerified;
    }

    public String getIsPermanentHousing() {
        return isPermanentHousing;
    }

    public void setIsPermanentHousing(String isPermanentHousing) {
        this.isPermanentHousing = isPermanentHousing;
    }

    public String getHomelessReason() {
        return homelessReason;
    }

    public void setHomelessReason(String homelessReason) {
        this.homelessReason = homelessReason;
    }

    public String getResidencePrior() {
        return residencePrior;
    }

    public void setResidencePrior(String residencePrior) {
        this.residencePrior = residencePrior;
    }

    public String getEpisodeStartDate() {
        return episodeStartDate;
    }

    public void setEpisodeStartDate(String episodeStartDate) {
        this.episodeStartDate = episodeStartDate;
    }

    public String getTimesHomeless() {
        return timesHomeless;
    }

    public void setTimesHomeless(String timesHomeless) {
        this.timesHomeless = timesHomeless;
    }

    public String getHasPhysicalDisability() {
        return hasPhysicalDisability;
    }

    public void setHasPhysicalDisability(String hasPhysicalDisability) {
        this.hasPhysicalDisability = hasPhysicalDisability;
    }

    public String getHasServicesPhysicalDisability() {
        return hasServicesPhysicalDisability;
    }

    public void setHasServicesPhysicalDisability(String hasServicesPhysicalDisability) {
        this.hasServicesPhysicalDisability = hasServicesPhysicalDisability;
    }

    public String getImpairs() {
        return impairs;
    }

    public void setImpairs(String impairs) {
        this.impairs = impairs;
    }

    public String getHasServicesDevDisability() {
        return hasServicesDevDisability;
    }

    public void setHasServicesDevDisability(String hasServicesDevDisability) {
        this.hasServicesDevDisability = hasServicesDevDisability;
    }

    public String getDevDisability() {
        return devDisability;
    }

    public void setDevDisability(String devDisability) {
        this.devDisability = devDisability;
    }

    public String getChronicHealthCondition() {
        return chronicHealthCondition;
    }

    public void setChronicHealthCondition(String chronicHealthCondition) {
        this.chronicHealthCondition = chronicHealthCondition;
    }

    public String getHasServicesCHC() {
        return hasServicesCHC;
    }

    public void setHasServicesCHC(String hasServicesCHC) {
        this.hasServicesCHC = hasServicesCHC;
    }

    public String getImpairs2() {
        return impairs2;
    }

    public void setImpairs2(String impairs2) {
        this.impairs2 = impairs2;
    }

    public String getHivAids() {
        return hivAids;
    }

    public void setHivAids(String hivAids) {
        this.hivAids = hivAids;
    }

    public String getHasServicesHivAids() {
        return hasServicesHivAids;
    }

    public void setHasServicesHivAids(String hasServicesHivAids) {
        this.hasServicesHivAids = hasServicesHivAids;
    }

    public String getMentalHealth() {
        return mentalHealth;
    }

    public void setMentalHealth(String mentalHealth) {
        this.mentalHealth = mentalHealth;
    }

    public String getHasServicesMH() {
        return hasServicesMH;
    }

    public void setHasServicesMH(String hasServicesMH) {
        this.hasServicesMH = hasServicesMH;
    }

    public String getImpairsAbility() {
        return impairsAbility;
    }

    public void setImpairsAbility(String impairsAbility) {
        this.impairsAbility = impairsAbility;
    }

    public String getSubstanceAbuse() {
        return substanceAbuse;
    }

    public void setSubstanceAbuse(String substanceAbuse) {
        this.substanceAbuse = substanceAbuse;
    }

    public String getHasServicesSA() {
        return hasServicesSA;
    }

    public void setHasServicesSA(String hasServicesSA) {
        this.hasServicesSA = hasServicesSA;
    }

    public String getImpairsAbility2() {
        return impairsAbility2;
    }

    public void setImpairsAbility2(String impairsAbility2) {
        this.impairsAbility2 = impairsAbility2;
    }

    public String getDisablingCondition() {
        return disablingCondition;
    }

    public void setDisablingCondition(String disablingCondition) {
        this.disablingCondition = disablingCondition;
    }

    public String getDomesticViolence() {
        return domesticViolence;
    }

    public void setDomesticViolence(String domesticViolence) {
        this.domesticViolence = domesticViolence;
    }

    public String getFleeing() {
        return fleeing;
    }

    public void setFleeing(String fleeing) {
        this.fleeing = fleeing;
    }

    public String getCashIncome() {
        return cashIncome;
    }

    public void setCashIncome(String cashIncome) {
        this.cashIncome = cashIncome;
    }

    public String getNonCashBenefits() {
        return nonCashBenefits;
    }

    public void setNonCashBenefits(String nonCashBenefits) {
        this.nonCashBenefits = nonCashBenefits;
    }

    public String getHealthInsurance() {
        return healthInsurance;
    }

    public void setHealthInsurance(String healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public String getEmployed() {
        return employed;
    }

    public void setEmployed(String employed) {
        this.employed = employed;
    }

    public String getEmploymentTenure() {
        return employmentTenure;
    }

    public void setEmploymentTenure(String employmentTenure) {
        this.employmentTenure = employmentTenure;
    }

    public String getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getSchoolEnrolled() {
        return schoolEnrolled;
    }

    public void setSchoolEnrolled(String schoolEnrolled) {
        this.schoolEnrolled = schoolEnrolled;
    }

    public String getIfNotEnrolled() {
        return ifNotEnrolled;
    }

    public void setIfNotEnrolled(String ifNotEnrolled) {
        this.ifNotEnrolled = ifNotEnrolled;
    }

    public String getVocationalEnrolled() {
        return vocationalEnrolled;
    }

    public void setVocationalEnrolled(String vocationalEnrolled) {
        this.vocationalEnrolled = vocationalEnrolled;
    }

    public String getHighestLevel() {
        return highestLevel;
    }

    public void setHighestLevel(String highestLevel) {
        this.highestLevel = highestLevel;
    }

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getHudHomeless() {
        return hudHomeless;
    }

    public void setHudHomeless(String hudHomeless) {
        this.hudHomeless = hudHomeless;
    }

    public String getTypeOfSchool() {
        return typeOfSchool;
    }

    public void setTypeOfSchool(String typeOfSchool) {
        this.typeOfSchool = typeOfSchool;
    }

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    public String getLengthOfStaying() {
        return lengthOfStaying;
    }

    public void setLengthOfStaying(String lengthOfStaying) {
        this.lengthOfStaying = lengthOfStaying;
    }

    public String getTotalMonthsHomeless() {
        return totalMonthsHomeless;
    }

    public void setTotalMonthsHomeless(String totalMonthsHomeless) {
        this.totalMonthsHomeless = totalMonthsHomeless;
    }

    public String getPregnant() {
        return pregnant;
    }

    public void setPregnant(String pregnant) {
        this.pregnant = pregnant;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGeneralHealth() {
        return generalHealth;
    }

    public void setGeneralHealth(String generalHealth) {
        this.generalHealth = generalHealth;
    }

    public List<String> getBarriersToEnrollingChildInSchool() {
        return barriersToEnrollingChildInSchool;
    }

    public void setBarriersToEnrollingChildInSchool(List<String> barriersToEnrollingChildInSchool) {
        this.barriersToEnrollingChildInSchool = barriersToEnrollingChildInSchool;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public String getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(String genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public String getGenderNonConforming() {
        return genderNonConforming;
    }

    public void setGenderNonConforming(String genderNonConforming) {
        this.genderNonConforming = genderNonConforming;
    }

    public String getOtherHomelessReason() {
        return otherHomelessReason;
    }

    public void setOtherHomelessReason(String otherHomelessReason) {
        this.otherHomelessReason = otherHomelessReason;
    }

    public String getOtherResidencePrior() {
        return otherResidencePrior;
    }

    public void setOtherResidencePrior(String otherResidencePrior) {
        this.otherResidencePrior = otherResidencePrior;
    }

    public String getDomesticViolenceDate() {
        return domesticViolenceDate;
    }

    public void setDomesticViolenceDate(String domesticViolenceDate) {
        this.domesticViolenceDate = domesticViolenceDate;
    }

    public List<String> getCashIncomeSource() {
        return cashIncomeSource;
    }

    public void setCashIncomeSource(List<String> cashIncomeSource) {
        this.cashIncomeSource = cashIncomeSource;
    }

    public String getEmploymentIncomeAmount() {
        return employmentIncomeAmount;
    }

    public void setEmploymentIncomeAmount(String employmentIncomeAmount) {
        this.employmentIncomeAmount = employmentIncomeAmount;
    }

    public String getUnemploymentInsAmount() {
        return unemploymentInsAmount;
    }

    public void setUnemploymentInsAmount(String unemploymentInsAmount) {
        this.unemploymentInsAmount = unemploymentInsAmount;
    }

    public String getWorkersCompAmount() {
        return workersCompAmount;
    }

    public void setWorkersCompAmount(String workersCompAmount) {
        this.workersCompAmount = workersCompAmount;
    }

    public String getPrivateDisabilityInsAmount() {
        return privateDisabilityInsAmount;
    }

    public void setPrivateDisabilityInsAmount(String privateDisabilityInsAmount) {
        this.privateDisabilityInsAmount = privateDisabilityInsAmount;
    }

    public String getVaDisabilityAmount() {
        return vaDisabilityAmount;
    }

    public void setVaDisabilityAmount(String vaDisabilityAmount) {
        this.vaDisabilityAmount = vaDisabilityAmount;
    }

    public String getSsdiAmount() {
        return ssdiAmount;
    }

    public void setSsdiAmount(String ssdiAmount) {
        this.ssdiAmount = ssdiAmount;
    }

    public String getSsiAmount() {
        return ssiAmount;
    }

    public void setSsiAmount(String ssiAmount) {
        this.ssiAmount = ssiAmount;
    }

    public String getSsaAmount() {
        return ssaAmount;
    }

    public void setSsaAmount(String ssaAmount) {
        this.ssaAmount = ssaAmount;
    }

    public String getVaPensionAmount() {
        return vaPensionAmount;
    }

    public void setVaPensionAmount(String vaPensionAmount) {
        this.vaPensionAmount = vaPensionAmount;
    }

    public String getPensionFromFormerFobAmount() {
        return pensionFromFormerFobAmount;
    }

    public void setPensionFromFormerFobAmount(String pensionFromFormerFobAmount) {
        this.pensionFromFormerFobAmount = pensionFromFormerFobAmount;
    }

    public String getTanfCalWorksAmount() {
        return tanfCalWorksAmount;
    }

    public void setTanfCalWorksAmount(String tanfCalWorksAmount) {
        this.tanfCalWorksAmount = tanfCalWorksAmount;
    }

    public String getGaAmount() {
        return gaAmount;
    }

    public void setGaAmount(String gaAmount) {
        this.gaAmount = gaAmount;
    }

    public String getAlimonySpousalSupportAmount() {
        return alimonySpousalSupportAmount;
    }

    public void setAlimonySpousalSupportAmount(String alimonySpousalSupportAmount) {
        this.alimonySpousalSupportAmount = alimonySpousalSupportAmount;
    }

    public String getChildSupportAmount() {
        return childSupportAmount;
    }

    public void setChildSupportAmount(String childSupportAmount) {
        this.childSupportAmount = childSupportAmount;
    }

    public String getOtherKidsAmount() {
        return otherKidsAmount;
    }

    public void setOtherKidsAmount(String otherKidsAmount) {
        this.otherKidsAmount = otherKidsAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSourcesNonCashBenefits() {
        return sourcesNonCashBenefits;
    }

    public void setSourcesNonCashBenefits(String sourcesNonCashBenefits) {
        this.sourcesNonCashBenefits = sourcesNonCashBenefits;
    }

    public String getOtherTanfBenefits() {
        return otherTanfBenefits;
    }

    public void setOtherTanfBenefits(String otherTanfBenefits) {
        this.otherTanfBenefits = otherTanfBenefits;
    }

    public String getOtherExIHSSParatransitPG() {
        return otherExIHSSParatransitPG;
    }

    public void setOtherExIHSSParatransitPG(String otherExIHSSParatransitPG) {
        this.otherExIHSSParatransitPG = otherExIHSSParatransitPG;
    }

    public String getCoveredSourceHealthInsurance() {
        return coveredSourceHealthInsurance;
    }

    public void setCoveredSourceHealthInsurance(String coveredSourceHealthInsurance) {
        this.coveredSourceHealthInsurance = coveredSourceHealthInsurance;
    }

    public String getOtherSourceHealthInsurance() {
        return otherSourceHealthInsurance;
    }

    public void setOtherSourceHealthInsurance(String otherSourceHealthInsurance) {
        this.otherSourceHealthInsurance = otherSourceHealthInsurance;
    }

    public String getUnemployedSeeking() {
        return unemployedSeeking;
    }

    public void setUnemployedSeeking(String unemployedSeeking) {
        this.unemployedSeeking = unemployedSeeking;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public List<String> getBarriersToEnrollingChildInSchoolOther() {
        return barriersToEnrollingChildInSchoolOther;
    }

    public void setBarriersToEnrollingChildInSchoolOther(final List<String> barriersToEnrollingChildInSchoolOther) {
        this.barriersToEnrollingChildInSchoolOther = barriersToEnrollingChildInSchoolOther;
    }
}
