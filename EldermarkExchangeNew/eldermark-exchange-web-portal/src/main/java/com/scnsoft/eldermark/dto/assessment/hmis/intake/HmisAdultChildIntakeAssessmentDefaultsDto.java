package com.scnsoft.eldermark.dto.assessment.hmis.intake;

import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;

public class HmisAdultChildIntakeAssessmentDefaultsDto extends AssessmentDefaultsDto {
    private ProgramData programData;
    private IndividualMember individualMember;

    public ProgramData getProgramData() {
        return programData;
    }

    public void setProgramData(ProgramData programData) {
        this.programData = programData;
    }

    public IndividualMember getIndividualMember() {
        return individualMember;
    }

    public void setIndividualMember(IndividualMember individualMember) {
        this.individualMember = individualMember;
    }

    public static class ProgramData {
        String caseManager;

        public String getCaseManager() {
            return caseManager;
        }

        public void setCaseManager(String caseManager) {
            this.caseManager = caseManager;
        }
    }

    public static class IndividualMember {
        private String ssnLastFourDigits;
        private String birthDate;
        private String qualityOfDOB;
        private String firstName;
        private String lastName;
        private String middleName;
        private String alias;
        private String suffix;
        private String qualityOfName;
        private String genderAssignedAtBirth;
        private String sexualOrientation;
        private String zipCodeOfLastPermanentAddress;
        private String qualityOfZipCode;
        private String primaryLanguage;
        private String isUSCitizen;
        private String isVeteran;
        private String worldWarIIOperations;
        private String koreanWarOperations;
        private String vietnamWarOperations;
        private String persianGulfWarOperations;
        private String afghanistanWarOperations;
        private String iraqFreedomWarOperations;
        private String iraqNewDawnWarOperations;
        private String otherWarOperations;
        private String militaryBranch;
        private String dischargeStatus;
        private String dischargeStatusVerified;

        public String getSsnLastFourDigits() {
            return ssnLastFourDigits;
        }

        public void setSsnLastFourDigits(String ssnLastFourDigits) {
            this.ssnLastFourDigits = ssnLastFourDigits;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getQualityOfDOB() {
            return qualityOfDOB;
        }

        public void setQualityOfDOB(String qualityOfDOB) {
            this.qualityOfDOB = qualityOfDOB;
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

        public String getZipCodeOfLastPermanentAddress() {
            return zipCodeOfLastPermanentAddress;
        }

        public void setZipCodeOfLastPermanentAddress(String zipCodeOfLastPermanentAddress) {
            this.zipCodeOfLastPermanentAddress = zipCodeOfLastPermanentAddress;
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
    }
}
