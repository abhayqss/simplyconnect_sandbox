package com.scnsoft.eldermark.api.shared.dto.adt;

import com.scnsoft.eldermark.api.shared.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.XTNPhoneNumberDto;

import java.util.Date;
import java.util.List;

public class IN1InsuranceSegmentDto implements SegmentDto {
    private String setId;
    private CECodedElementDto insurancePlanId;
    private CXExtendedCompositeIdDto insuranceCompanyId;
    private XONExtendedCompositeNameAndIdForOrganizationsDto insuranceCompanyName;
    private List<XTNPhoneNumberDto> insuranceCoPhoneNumbers;
    private String groupNumber;
    private List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNames;
    private Date planEffectiveDate;
    private Date planExpirationDate;
    private String planType;
    private List<String> namesOfInsured;
    private CECodedElementDto insuredsRelationshipToPatient;

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

    public CXExtendedCompositeIdDto getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(CXExtendedCompositeIdDto insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public XONExtendedCompositeNameAndIdForOrganizationsDto getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(XONExtendedCompositeNameAndIdForOrganizationsDto insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
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

    public Date getPlanEffectiveDate() {
        return planEffectiveDate;
    }

    public void setPlanEffectiveDate(Date planEffectiveDate) {
        this.planEffectiveDate = planEffectiveDate;
    }

    public Date getPlanExpirationDate() {
        return planExpirationDate;
    }

    public void setPlanExpirationDate(Date planExpirationDate) {
        this.planExpirationDate = planExpirationDate;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public List<String> getNamesOfInsured() {
        return namesOfInsured;
    }

    public void setNamesOfInsured(List<String> namesOfInsured) {
        this.namesOfInsured = namesOfInsured;
    }

    public CECodedElementDto getInsuredsRelationshipToPatient() {
        return insuredsRelationshipToPatient;
    }

    public void setInsuredsRelationshipToPatient(CECodedElementDto insuredsRelationshipToPatient) {
        this.insuredsRelationshipToPatient = insuredsRelationshipToPatient;
    }
}
