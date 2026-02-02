package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.*;

import java.util.List;

public class AdtInsuranceDto {
    private String setId;
    private CECodedElementDto insurancePlanId;
    private List<CXExtendedCompositeIdDto> insuranceCompanyIds;
    private List<XONExtendedCompositeNameAndIdForOrganizationsDto> insuranceCompanyNames;
    private List<AddressDto> insuranceCompanyAddresses;
    private List<XTNPhoneNumberDto> insuranceCoPhoneNumbers;
    private String groupNumber;
    private List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNames;
    private Long planEffectiveDate;
    private Long planExpirationDate;
    private String planType;
    private List<XPNDto> namesOfInsured;
    private CECodedElementDto insuredsRelationshipToPatient;
    private Long insuredsDateOfBirth;
    private List<AddressDto> insuredsAddresses;
    private String preAdmitCert;
    private String typeOfAgreementCode;
    private String policyNumber;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public CECodedElementDto getInsurancePlanId() {
        return insurancePlanId;
    }

    public void setInsurancePlanId(CECodedElementDto insurancePlanId) {
        this.insurancePlanId = insurancePlanId;
    }

    public List<CXExtendedCompositeIdDto> getInsuranceCompanyIds() {
        return insuranceCompanyIds;
    }

    public void setInsuranceCompanyIds(List<CXExtendedCompositeIdDto> insuranceCompanyIds) {
        this.insuranceCompanyIds = insuranceCompanyIds;
    }

    public List<XONExtendedCompositeNameAndIdForOrganizationsDto> getInsuranceCompanyNames() {
        return insuranceCompanyNames;
    }

    public void setInsuranceCompanyNames(List<XONExtendedCompositeNameAndIdForOrganizationsDto> insuranceCompanyNames) {
        this.insuranceCompanyNames = insuranceCompanyNames;
    }

    public List<AddressDto> getInsuranceCompanyAddresses() {
        return insuranceCompanyAddresses;
    }

    public void setInsuranceCompanyAddresses(List<AddressDto> insuranceCompanyAddresses) {
        this.insuranceCompanyAddresses = insuranceCompanyAddresses;
    }

    public List<XTNPhoneNumberDto> getInsuranceCoPhoneNumbers() {
        return insuranceCoPhoneNumbers;
    }

    public void setInsuranceCoPhoneNumbers(List<XTNPhoneNumberDto> insuranceCoPhoneNumbers) {
        this.insuranceCoPhoneNumbers = insuranceCoPhoneNumbers;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public List<XONExtendedCompositeNameAndIdForOrganizationsDto> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNames) {
        this.groupNames = groupNames;
    }

    public Long getPlanEffectiveDate() {
        return planEffectiveDate;
    }

    public void setPlanEffectiveDate(Long planEffectiveDate) {
        this.planEffectiveDate = planEffectiveDate;
    }

    public Long getPlanExpirationDate() {
        return planExpirationDate;
    }

    public void setPlanExpirationDate(Long planExpirationDate) {
        this.planExpirationDate = planExpirationDate;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public List<XPNDto> getNamesOfInsured() {
        return namesOfInsured;
    }

    public void setNamesOfInsured(List<XPNDto> namesOfInsured) {
        this.namesOfInsured = namesOfInsured;
    }

    public CECodedElementDto getInsuredsRelationshipToPatient() {
        return insuredsRelationshipToPatient;
    }

    public void setInsuredsRelationshipToPatient(CECodedElementDto insuredsRelationshipToPatient) {
        this.insuredsRelationshipToPatient = insuredsRelationshipToPatient;
    }

    public Long getInsuredsDateOfBirth() {
        return insuredsDateOfBirth;
    }

    public void setInsuredsDateOfBirth(Long insuredsDateOfBirth) {
        this.insuredsDateOfBirth = insuredsDateOfBirth;
    }

    public List<AddressDto> getInsuredsAddresses() {
        return insuredsAddresses;
    }

    public void setInsuredsAddresses(List<AddressDto> insuredsAddresses) {
        this.insuredsAddresses = insuredsAddresses;
    }

    public String getPreAdmitCert() {
        return preAdmitCert;
    }

    public void setPreAdmitCert(String preAdmitCert) {
        this.preAdmitCert = preAdmitCert;
    }

    public String getTypeOfAgreementCode() {
        return typeOfAgreementCode;
    }

    public void setTypeOfAgreementCode(String typeOfAgreementCode) {
        this.typeOfAgreementCode = typeOfAgreementCode;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
