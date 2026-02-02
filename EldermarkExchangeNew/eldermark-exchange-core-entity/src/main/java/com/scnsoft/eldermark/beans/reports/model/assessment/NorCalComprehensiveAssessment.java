package com.scnsoft.eldermark.beans.reports.model.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scnsoft.eldermark.beans.reports.model.EmergencyContact;

import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.EMERGENCY_CONTACT_PREFIX_1;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.EMERGENCY_CONTACT_PREFIX_2;
import static com.scnsoft.eldermark.beans.reports.constants.ReportConstants.EMERGENCY_CONTACT_PREFIX_3;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NorCalComprehensiveAssessment implements EmergencyContactsAware {

    @JsonProperty("Quality of DOB")
    String qualityOfDOB;

    @JsonProperty("Alias")
    String alias;

    @JsonProperty("Suffix")
    String suffix;

    @JsonProperty("Quality of Name")
    String qualityOfName;

    @JsonProperty("Gender Assigned at Birth")
    String genderAssignedAtBirth;

    @JsonProperty("Sexual Orientation")
    String sexualOrientation;

    @JsonProperty("Quality of Zip Code")
    String qualityOfZipCode;

    @JsonProperty("Primary Language")
    String primaryLanguage;

    @JsonProperty("U.S. Citizen?")
    String isUSCitizen;

    @JsonProperty("Are you a veteran?")
    String isVeteran;

    @JsonProperty("Theatre of Operations: World War II")
    String worldWarIIOperations;

    @JsonProperty("Theatre of Operations: Korean War")
    String koreanWarOperations;

    @JsonProperty("Theatre of Operations: Vietnam War")
    String vietnamWarOperations;

    @JsonProperty("Theatre of Operations: Persian Gulf War (Operation Desert Storm)")
    String persianGulfWarOperations;

    @JsonProperty("Theatre of Operations: Afghanistan (Operation Enduring Freedom)")
    String afghanistanWarOperations;

    @JsonProperty("Theatre of Operations: Iraq (Operation Iraqi Freedom)")
    String iraqFreedomWarOperations;

    @JsonProperty("Theatre of Operations: Iraq (Operation New Dawn)")
    String iraqNewDawnWarOperations;

    @JsonProperty("Theatre of Operations: Other Peace-keeping Operations or Military Interventions (such as Lebanon, Panama, Somalia, Bosnia, Kosovo)")
    String otherWarOperations;

    @JsonProperty("Branch of the Military")
    String militaryBranch;

    @JsonProperty("Discharge Status")
    String dischargeStatus;

    @JsonProperty("Discharge Status Verified (DD-214)")
    String dischargeStatusVerified;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_1)
    private EmergencyContact emergencyContact1;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_2)
    private EmergencyContact emergencyContact2;

    @JsonUnwrapped(prefix = EMERGENCY_CONTACT_PREFIX_3)
    private EmergencyContact emergencyContact3;

    public String getQualityOfDOB() {
        return qualityOfDOB;
    }

    public void setQualityOfDOB(String qualityOfDOB) {
        this.qualityOfDOB = qualityOfDOB;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getQualityOfName() {
        return qualityOfName;
    }

    public void setQualityOfName(String qualityOfName) {
        this.qualityOfName = qualityOfName;
    }

    public String getGenderAssignedAtBirth() {
        return genderAssignedAtBirth;
    }

    public void setGenderAssignedAtBirth(String genderAssignedAtBirth) {
        this.genderAssignedAtBirth = genderAssignedAtBirth;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public String getQualityOfZipCode() {
        return qualityOfZipCode;
    }

    public void setQualityOfZipCode(String qualityOfZipCode) {
        this.qualityOfZipCode = qualityOfZipCode;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getIsUSCitizen() {
        return isUSCitizen;
    }

    public void setIsUSCitizen(String isUSCitizen) {
        this.isUSCitizen = isUSCitizen;
    }

    public String getIsVeteran() {
        return isVeteran;
    }

    public void setIsVeteran(String isVeteran) {
        this.isVeteran = isVeteran;
    }

    public String getWorldWarIIOperations() {
        return worldWarIIOperations;
    }

    public void setWorldWarIIOperations(String worldWarIIOperations) {
        this.worldWarIIOperations = worldWarIIOperations;
    }

    public String getKoreanWarOperations() {
        return koreanWarOperations;
    }

    public void setKoreanWarOperations(String koreanWarOperations) {
        this.koreanWarOperations = koreanWarOperations;
    }

    public String getVietnamWarOperations() {
        return vietnamWarOperations;
    }

    public void setVietnamWarOperations(String vietnamWarOperations) {
        this.vietnamWarOperations = vietnamWarOperations;
    }

    public String getPersianGulfWarOperations() {
        return persianGulfWarOperations;
    }

    public void setPersianGulfWarOperations(String persianGulfWarOperations) {
        this.persianGulfWarOperations = persianGulfWarOperations;
    }

    public String getAfghanistanWarOperations() {
        return afghanistanWarOperations;
    }

    public void setAfghanistanWarOperations(String afghanistanWarOperations) {
        this.afghanistanWarOperations = afghanistanWarOperations;
    }

    public String getIraqFreedomWarOperations() {
        return iraqFreedomWarOperations;
    }

    public void setIraqFreedomWarOperations(String iraqFreedomWarOperations) {
        this.iraqFreedomWarOperations = iraqFreedomWarOperations;
    }

    public String getIraqNewDawnWarOperations() {
        return iraqNewDawnWarOperations;
    }

    public void setIraqNewDawnWarOperations(String iraqNewDawnWarOperations) {
        this.iraqNewDawnWarOperations = iraqNewDawnWarOperations;
    }

    public String getOtherWarOperations() {
        return otherWarOperations;
    }

    public void setOtherWarOperations(String otherWarOperations) {
        this.otherWarOperations = otherWarOperations;
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
}
