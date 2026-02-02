package com.scnsoft.eldermark.dto.assessment.hmis.intake;

import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;

import java.util.List;

public class HmisAdultChildIntakeReasessmentDefaultsDto extends AssessmentDefaultsDto {
    private ProgramData programData;
    private FamilyMember familyMember;
    private FamilyMember familyMember1;
    private FamilyMember familyMember2;
    private FamilyMember familyMember3;
    private FamilyMember familyMember4;
    private FamilyMember familyMember5;

    public void setProgramData(ProgramData programData) {
        this.programData = programData;
    }

    public void setFamilyMember(final FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public void setFamilyMember1(final FamilyMember familyMember1) {
        this.familyMember1 = familyMember1;
    }

    public void setFamilyMember2(final FamilyMember familyMember2) {
        this.familyMember2 = familyMember2;
    }

    public void setFamilyMember3(final FamilyMember familyMember3) {
        this.familyMember3 = familyMember3;
    }

    public void setFamilyMember4(final FamilyMember familyMember4) {
        this.familyMember4 = familyMember4;
    }

    public void setFamilyMember5(final FamilyMember familyMember5) {
        this.familyMember5 = familyMember5;
    }

    public ProgramData getProgramData() {
        return programData;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public FamilyMember getFamilyMember1() {
        return familyMember1;
    }

    public FamilyMember getFamilyMember2() {
        return familyMember2;
    }

    public FamilyMember getFamilyMember3() {
        return familyMember3;
    }

    public FamilyMember getFamilyMember4() {
        return familyMember4;
    }

    public FamilyMember getFamilyMember5() {
        return familyMember5;
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

    public static class FamilyMember {
        private Demographics demographics;
        private Disability disability;
        private Benefits benefits;
        private Insurance insurance;
        private Education education;
        public static class Demographics {
            private String ssnLastFourDigits;
            private String birthDate;
            private String firstName;
            private String lastName;
            private String middleName;
            private String sexualOrientation;
            private String zipCode;
            private String isPermanentHousing;
            private String moveInDate;
            private String phone;
            private String streetAddress;
            private String email;
            private String city;
            private String state;

            public void setSsnLastFourDigits(final String ssnLastFourDigits) {
                this.ssnLastFourDigits = ssnLastFourDigits;
            }

            public void setBirthDate(final String birthDate) {
                this.birthDate = birthDate;
            }

            public void setFirstName(final String firstName) {
                this.firstName = firstName;
            }

            public void setLastName(final String lastName) {
                this.lastName = lastName;
            }

            public void setMiddleName(final String middleName) {
                this.middleName = middleName;
            }

            public void setSexualOrientation(final String sexualOrientation) {
                this.sexualOrientation = sexualOrientation;
            }

            public void setZipCode(final String zipCode) {
                this.zipCode = zipCode;
            }

            public void setIsPermanentHousing(final String isPermanentHousing) {
                this.isPermanentHousing = isPermanentHousing;
            }

            public void setMoveInDate(final String moveInDate) {
                this.moveInDate = moveInDate;
            }

            public String getSsnLastFourDigits() {
                return ssnLastFourDigits;
            }

            public String getBirthDate() {
                return birthDate;
            }

            public String getFirstName() {
                return firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public String getMiddleName() {
                return middleName;
            }

            public String getSexualOrientation() {
                return sexualOrientation;
            }

            public String getZipCode() {
                return zipCode;
            }

            public String getIsPermanentHousing() {
                return isPermanentHousing;
            }

            public String getMoveInDate() {
                return moveInDate;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(final String phone) {
                this.phone = phone;
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
        }

        public static class Disability {
            private String hasPhysicalDisability;
            private String isReceivingServicesForPhysicalDisability;
            private String longTermImpairs;
            private String developmentalDisability;
            private String receivingServicesForDevDisability;
            private String chronicHealthCondition;
            private String receivingServicesForCHC;
            private String longTermImpairs2;
            private String hivAids;
            private String lastDateOfDV;
            private String receivingServicesForHIVAIDS;
            private String mentalHealth;
            private String receivingServicesForMH;
            private String longTermImpairsAbility;
            private String substanceAbuse;
            private String receivingServicesForSA;
            private String longTermImpairsAbility2;
            private String disablingCondition;
            private String domesticViolence;
            private String areYouCurrentlyFleeing;
            private String isPregnant;
            private String dueDate;

            public void setHasPhysicalDisability(final String hasPhysicalDisability) {
                this.hasPhysicalDisability = hasPhysicalDisability;
            }

            public void setIsReceivingServicesForPhysicalDisability(final String isReceivingServicesForPhysicalDisability) {
                this.isReceivingServicesForPhysicalDisability = isReceivingServicesForPhysicalDisability;
            }

            public void setLongTermImpairs(final String longTermImpairs) {
                this.longTermImpairs = longTermImpairs;
            }

            public void setDevelopmentalDisability(final String developmentalDisability) {
                this.developmentalDisability = developmentalDisability;
            }

            public void setReceivingServicesForDevDisability(final String receivingServicesForDevDisability) {
                this.receivingServicesForDevDisability = receivingServicesForDevDisability;
            }

            public void setChronicHealthCondition(final String chronicHealthCondition) {
                this.chronicHealthCondition = chronicHealthCondition;
            }

            public void setReceivingServicesForCHC(final String receivingServicesForCHC) {
                this.receivingServicesForCHC = receivingServicesForCHC;
            }

            public void setLongTermImpairs2(final String longTermImpairs2) {
                this.longTermImpairs2 = longTermImpairs2;
            }

            public void setHivAids(final String hivAids) {
                this.hivAids = hivAids;
            }

            public void setReceivingServicesForHIVAIDS(final String receivingServicesForHIVAIDS) {
                this.receivingServicesForHIVAIDS = receivingServicesForHIVAIDS;
            }

            public void setMentalHealth(final String mentalHealth) {
                this.mentalHealth = mentalHealth;
            }

            public void setReceivingServicesForMH(final String receivingServicesForMH) {
                this.receivingServicesForMH = receivingServicesForMH;
            }

            public void setLongTermImpairsAbility(final String longTermImpairsAbility) {
                this.longTermImpairsAbility = longTermImpairsAbility;
            }

            public void setSubstanceAbuse(final String substanceAbuse) {
                this.substanceAbuse = substanceAbuse;
            }

            public void setReceivingServicesForSA(final String receivingServicesForSA) {
                this.receivingServicesForSA = receivingServicesForSA;
            }

            public void setLongTermImpairsAbility2(final String longTermImpairsAbility2) {
                this.longTermImpairsAbility2 = longTermImpairsAbility2;
            }

            public void setDisablingCondition(final String disablingCondition) {
                this.disablingCondition = disablingCondition;
            }

            public void setDomesticViolence(final String domesticViolence) {
                this.domesticViolence = domesticViolence;
            }

            public void setAreYouCurrentlyFleeing(final String areYouCurrentlyFleeing) {
                this.areYouCurrentlyFleeing = areYouCurrentlyFleeing;
            }

            public void setIsPregnant(final String isPregnant) {
                this.isPregnant = isPregnant;
            }

            public void setDueDate(final String dueDate) {
                this.dueDate = dueDate;
            }

            public String getHasPhysicalDisability() {
                return hasPhysicalDisability;
            }

            public String getIsReceivingServicesForPhysicalDisability() {
                return isReceivingServicesForPhysicalDisability;
            }

            public String getLongTermImpairs() {
                return longTermImpairs;
            }

            public String getDevelopmentalDisability() {
                return developmentalDisability;
            }

            public String getReceivingServicesForDevDisability() {
                return receivingServicesForDevDisability;
            }

            public String getChronicHealthCondition() {
                return chronicHealthCondition;
            }

            public String getReceivingServicesForCHC() {
                return receivingServicesForCHC;
            }

            public String getLongTermImpairs2() {
                return longTermImpairs2;
            }

            public String getHivAids() {
                return hivAids;
            }

            public String getReceivingServicesForHIVAIDS() {
                return receivingServicesForHIVAIDS;
            }

            public String getMentalHealth() {
                return mentalHealth;
            }

            public String getReceivingServicesForMH() {
                return receivingServicesForMH;
            }

            public String getLongTermImpairsAbility() {
                return longTermImpairsAbility;
            }

            public String getSubstanceAbuse() {
                return substanceAbuse;
            }

            public String getReceivingServicesForSA() {
                return receivingServicesForSA;
            }

            public String getLongTermImpairsAbility2() {
                return longTermImpairsAbility2;
            }

            public String getDisablingCondition() {
                return disablingCondition;
            }

            public String getDomesticViolence() {
                return domesticViolence;
            }

            public String getAreYouCurrentlyFleeing() {
                return areYouCurrentlyFleeing;
            }

            public String getIsPregnant() {
                return isPregnant;
            }

            public String getDueDate() {
                return dueDate;
            }

            public String getLastDateOfDV() {
                return lastDateOfDV;
            }

            public void setLastDateOfDV(final String lastDateOfDV) {
                this.lastDateOfDV = lastDateOfDV;
            }

        }

        public static class Benefits {
            private String cashIncome;
            private List<String> sourceOfCashIncome;
            private String employmentIncomeAmount;
            private String unemploymentInsAmount;
            private String workersCompAmount;
            private String privateDisabilityInsAmount;
            private String vaDisabilityAmount;
            private String ssdiAmount;
            private String ssiAmount;
            private String ssaAmount;
            private String vaPensionAmount;
            private String pensionFromFormerFobAmount;
            private String tanfCalWorksAmount;
            private String gaAmount;
            private String alimonySpousalSupportAmount;
            private String childSupportAmount;
            private String otherKidsAmount;
            private String totalAmount;
            private String nonCashBenefits;
            private String sourcesOfNonCashBenefits;
            private String otherTANFBenefits;
            private String otherExIHSSParatransitPG;

            public void setCashIncome(final String cashIncome) {
                this.cashIncome = cashIncome;
            }

            public void setSourceOfCashIncome(final List<String> sourceOfCashIncome) {
                this.sourceOfCashIncome = sourceOfCashIncome;
            }

            public void setEmploymentIncomeAmount(final String employmentIncomeAmount) {
                this.employmentIncomeAmount = employmentIncomeAmount;
            }

            public void setUnemploymentInsAmount(final String unemploymentInsAmount) {
                this.unemploymentInsAmount = unemploymentInsAmount;
            }

            public void setWorkersCompAmount(final String workersCompAmount) {
                this.workersCompAmount = workersCompAmount;
            }

            public void setPrivateDisabilityInsAmount(final String privateDisabilityInsAmount) {
                this.privateDisabilityInsAmount = privateDisabilityInsAmount;
            }

            public void setVaDisabilityAmount(final String vaDisabilityAmount) {
                this.vaDisabilityAmount = vaDisabilityAmount;
            }

            public void setSsdiAmount(final String ssdiAmount) {
                this.ssdiAmount = ssdiAmount;
            }

            public void setSsiAmount(final String ssiAmount) {
                this.ssiAmount = ssiAmount;
            }

            public void setSsaAmount(final String ssaAmount) {
                this.ssaAmount = ssaAmount;
            }

            public void setVaPensionAmount(final String vaPensionAmount) {
                this.vaPensionAmount = vaPensionAmount;
            }

            public void setPensionFromFormerFobAmount(final String pensionFromFormerFobAmount) {
                this.pensionFromFormerFobAmount = pensionFromFormerFobAmount;
            }

            public void setTanfCalWorksAmount(final String tanfCalWorksAmount) {
                this.tanfCalWorksAmount = tanfCalWorksAmount;
            }

            public void setGaAmount(final String gaAmount) {
                this.gaAmount = gaAmount;
            }

            public void setAlimonySpousalSupportAmount(final String alimonySpousalSupportAmount) {
                this.alimonySpousalSupportAmount = alimonySpousalSupportAmount;
            }

            public void setChildSupportAmount(final String childSupportAmount) {
                this.childSupportAmount = childSupportAmount;
            }

            public void setOtherKidsAmount(final String otherKidsAmount) {
                this.otherKidsAmount = otherKidsAmount;
            }

            public void setTotalAmount(final String totalAmount) {
                this.totalAmount = totalAmount;
            }

            public void setNonCashBenefits(final String nonCashBenefits) {
                this.nonCashBenefits = nonCashBenefits;
            }

            public void setSourcesOfNonCashBenefits(final String sourcesOfNonCashBenefits) {
                this.sourcesOfNonCashBenefits = sourcesOfNonCashBenefits;
            }

            public void setOtherTANFBenefits(final String otherTANFBenefits) {
                this.otherTANFBenefits = otherTANFBenefits;
            }

            public void setOtherExIHSSParatransitPG(final String otherExIHSSParatransitPG) {
                this.otherExIHSSParatransitPG = otherExIHSSParatransitPG;
            }

            public String getCashIncome() {
                return cashIncome;
            }

            public List<String> getSourceOfCashIncome() {
                return sourceOfCashIncome;
            }

            public String getEmploymentIncomeAmount() {
                return employmentIncomeAmount;
            }

            public String getUnemploymentInsAmount() {
                return unemploymentInsAmount;
            }

            public String getWorkersCompAmount() {
                return workersCompAmount;
            }

            public String getPrivateDisabilityInsAmount() {
                return privateDisabilityInsAmount;
            }

            public String getVaDisabilityAmount() {
                return vaDisabilityAmount;
            }

            public String getSsdiAmount() {
                return ssdiAmount;
            }

            public String getSsiAmount() {
                return ssiAmount;
            }

            public String getSsaAmount() {
                return ssaAmount;
            }

            public String getVaPensionAmount() {
                return vaPensionAmount;
            }

            public String getPensionFromFormerFobAmount() {
                return pensionFromFormerFobAmount;
            }

            public String getTanfCalWorksAmount() {
                return tanfCalWorksAmount;
            }

            public String getGaAmount() {
                return gaAmount;
            }

            public String getAlimonySpousalSupportAmount() {
                return alimonySpousalSupportAmount;
            }

            public String getChildSupportAmount() {
                return childSupportAmount;
            }

            public String getOtherKidsAmount() {
                return otherKidsAmount;
            }

            public String getTotalAmount() {
                return totalAmount;
            }

            public String getNonCashBenefits() {
                return nonCashBenefits;
            }

            public String getSourcesOfNonCashBenefits() {
                return sourcesOfNonCashBenefits;
            }

            public String getOtherTANFBenefits() {
                return otherTANFBenefits;
            }

            public String getOtherExIHSSParatransitPG() {
                return otherExIHSSParatransitPG;
            }
        }

        public static class Insurance {
            private String healthInsurance;
            private String ifCoveredSourceOfHealthInsurance;
            private String otherSourceOfHealthInsurance;
            private String employed;
            private String employmentTenure;
            private String hoursWorkedLastWeek;
            private String ifUnemployedSeeking;

            public void setHealthInsurance(final String healthInsurance) {
                this.healthInsurance = healthInsurance;
            }

            public void setIfCoveredSourceOfHealthInsurance(final String ifCoveredSourceOfHealthInsurance) {
                this.ifCoveredSourceOfHealthInsurance = ifCoveredSourceOfHealthInsurance;
            }

            public void setOtherSourceOfHealthInsurance(final String otherSourceOfHealthInsurance) {
                this.otherSourceOfHealthInsurance = otherSourceOfHealthInsurance;
            }

            public void setEmployed(final String employed) {
                this.employed = employed;
            }

            public void setEmploymentTenure(final String employmentTenure) {
                this.employmentTenure = employmentTenure;
            }

            public void setHoursWorkedLastWeek(final String hoursWorkedLastWeek) {
                this.hoursWorkedLastWeek = hoursWorkedLastWeek;
            }

            public void setIfUnemployedSeeking(final String ifUnemployedSeeking) {
                this.ifUnemployedSeeking = ifUnemployedSeeking;
            }

            public String getHealthInsurance() {
                return healthInsurance;
            }

            public String getIfCoveredSourceOfHealthInsurance() {
                return ifCoveredSourceOfHealthInsurance;
            }

            public String getOtherSourceOfHealthInsurance() {
                return otherSourceOfHealthInsurance;
            }

            public String getEmployed() {
                return employed;
            }

            public String getEmploymentTenure() {
                return employmentTenure;
            }

            public String getHoursWorkedLastWeek() {
                return hoursWorkedLastWeek;
            }

            public String getIfUnemployedSeeking() {
                return ifUnemployedSeeking;
            }
        }

        public static class Education {
            private String schoolCurrentlyEnrolled;
            private String vocationalCurrentlyEnrolled;
            private String ifNotEnrolledLastDateOfEnrollment;
            private String highestLevelOfSchoolCompletedORCurrentGradeEnrolled;
            private List<String> barriersToEnrollingChildInSchool;
            private List<String> barriersToEnrollingChildInSchoolOther;
            private String highestDegreeEarned;
            private String schoolName;
            private String hudHomelessLiaison;
            private String typeOfSchool;

            public void setSchoolCurrentlyEnrolled(final String schoolCurrentlyEnrolled) {
                this.schoolCurrentlyEnrolled = schoolCurrentlyEnrolled;
            }

            public void setVocationalCurrentlyEnrolled(final String vocationalCurrentlyEnrolled) {
                this.vocationalCurrentlyEnrolled = vocationalCurrentlyEnrolled;
            }

            public void setIfNotEnrolledLastDateOfEnrollment(final String ifNotEnrolledLastDateOfEnrollment) {
                this.ifNotEnrolledLastDateOfEnrollment = ifNotEnrolledLastDateOfEnrollment;
            }

            public void setHighestLevelOfSchoolCompletedORCurrentGradeEnrolled(final String highestLevelOfSchoolCompletedORCurrentGradeEnrolled) {
                this.highestLevelOfSchoolCompletedORCurrentGradeEnrolled = highestLevelOfSchoolCompletedORCurrentGradeEnrolled;
            }

            public void setBarriersToEnrollingChildInSchool(final List<String> barriersToEnrollingChildInSchool) {
                this.barriersToEnrollingChildInSchool = barriersToEnrollingChildInSchool;
            }

            public void setHighestDegreeEarned(final String highestDegreeEarned) {
                this.highestDegreeEarned = highestDegreeEarned;
            }

            public void setSchoolName(final String schoolName) {
                this.schoolName = schoolName;
            }

            public void setHudHomelessLiaison(final String hudHomelessLiaison) {
                this.hudHomelessLiaison = hudHomelessLiaison;
            }

            public void setTypeOfSchool(final String typeOfSchool) {
                this.typeOfSchool = typeOfSchool;
            }

            public String getSchoolCurrentlyEnrolled() {
                return schoolCurrentlyEnrolled;
            }

            public String getVocationalCurrentlyEnrolled() {
                return vocationalCurrentlyEnrolled;
            }

            public String getIfNotEnrolledLastDateOfEnrollment() {
                return ifNotEnrolledLastDateOfEnrollment;
            }

            public String getHighestLevelOfSchoolCompletedORCurrentGradeEnrolled() {
                return highestLevelOfSchoolCompletedORCurrentGradeEnrolled;
            }

            public List<String> getBarriersToEnrollingChildInSchool() {
                return barriersToEnrollingChildInSchool;
            }

            public String getHighestDegreeEarned() {
                return highestDegreeEarned;
            }

            public String getSchoolName() {
                return schoolName;
            }

            public String getHudHomelessLiaison() {
                return hudHomelessLiaison;
            }

            public String getTypeOfSchool() {
                return typeOfSchool;
            }

            public List<String> getBarriersToEnrollingChildInSchoolOther() {
                return barriersToEnrollingChildInSchoolOther;
            }

            public void setBarriersToEnrollingChildInSchoolOther(final List<String> barriersToEnrollingChildInSchoolOther) {
                this.barriersToEnrollingChildInSchoolOther = barriersToEnrollingChildInSchoolOther;
            }
        }

        public void setDemographics(final Demographics demographics) {
            this.demographics = demographics;
        }

        public void setDisability(final Disability disability) {
            this.disability = disability;
        }

        public void setBenefits(final Benefits benefits) {
            this.benefits = benefits;
        }

        public void setInsurance(final Insurance insurance) {
            this.insurance = insurance;
        }

        public void setEducation(final Education education) {
            this.education = education;
        }

        public Demographics getDemographics() {
            return demographics;
        }

        public Disability getDisability() {
            return disability;
        }

        public Benefits getBenefits() {
            return benefits;
        }

        public Insurance getInsurance() {
            return insurance;
        }

        public Education getEducation() {
            return education;
        }
    }
}
