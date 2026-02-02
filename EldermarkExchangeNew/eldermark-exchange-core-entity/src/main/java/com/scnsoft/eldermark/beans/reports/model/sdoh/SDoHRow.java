package com.scnsoft.eldermark.beans.reports.model.sdoh;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class SDoHRow {

    private SDohRowDescriptor rowDescriptor;

    private Long servicePlanId;
    private Long needId;
    private Long goalId;

    private String clientInsurancePlan;

    private String submitterName;
    private String sourceSystem;
    private String memberLastName;
    private String memberFirstName;
    private String memberMiddleName;
    private LocalDate memberDateOfBirth;
    private String memberGender;
    private String memberAddress;
    private String memberCity;
    private String memberState;
    private String memberZipCode;
    private String memberHicn;
    private String memberCardId;
    private Instant serviceDate;
    private SdoHRowType identificationReferralFulfillment;
    private String icdOrMbrAttributionCode;
    private String referralFulfillmentProgramName;
    private String referralFulfillmentProgramAddress;
    private String referralFulfillmentProgramPhone;
    private String refFulProgramType;
    private String refFulProgramSubtype;

    public SDoHRow(SDohRowDescriptor cardinalityDescriptor) {
        this.rowDescriptor = Objects.requireNonNull(cardinalityDescriptor);
    }

    public SDohRowDescriptor getRowDescriptor() {
        return rowDescriptor;
    }

    public void setRowDescriptor(SDohRowDescriptor rowDescriptor) {
        this.rowDescriptor = rowDescriptor;
    }

    public Long getServicePlanId() {
        return servicePlanId;
    }

    public void setServicePlanId(Long servicePlanId) {
        this.servicePlanId = servicePlanId;
    }

    public Long getNeedId() {
        return needId;
    }

    public void setNeedId(Long needId) {
        this.needId = needId;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getClientInsurancePlan() {
        return clientInsurancePlan;
    }

    public void setClientInsurancePlan(String clientInsurancePlan) {
        this.clientInsurancePlan = clientInsurancePlan;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberMiddleName() {
        return memberMiddleName;
    }

    public void setMemberMiddleName(String memberMiddleName) {
        this.memberMiddleName = memberMiddleName;
    }

    public LocalDate getMemberDateOfBirth() {
        return memberDateOfBirth;
    }

    public void setMemberDateOfBirth(LocalDate memberDateOfBirth) {
        this.memberDateOfBirth = memberDateOfBirth;
    }

    public String getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(String memberGender) {
        this.memberGender = memberGender;
    }

    public String getMemberAddress() {
        return memberAddress;
    }

    public void setMemberAddress(String memberAddress) {
        this.memberAddress = memberAddress;
    }

    public String getMemberCity() {
        return memberCity;
    }

    public void setMemberCity(String memberCity) {
        this.memberCity = memberCity;
    }

    public String getMemberState() {
        return memberState;
    }

    public void setMemberState(String memberState) {
        this.memberState = memberState;
    }

    public String getMemberZipCode() {
        return memberZipCode;
    }

    public void setMemberZipCode(String memberZipCode) {
        this.memberZipCode = memberZipCode;
    }

    public String getMemberHicn() {
        return memberHicn;
    }

    public void setMemberHicn(String memberHicn) {
        this.memberHicn = memberHicn;
    }

    public String getMemberCardId() {
        return memberCardId;
    }

    public void setMemberCardId(String memberCardId) {
        this.memberCardId = memberCardId;
    }

    public Instant getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Instant serviceDate) {
        this.serviceDate = serviceDate;
    }

    public SdoHRowType getIdentificationReferralFulfillment() {
        return identificationReferralFulfillment;
    }

    public void setIdentificationReferralFulfillment(SdoHRowType identificationReferralFulfillment) {
        this.identificationReferralFulfillment = identificationReferralFulfillment;
    }

    public String getIcdOrMbrAttributionCode() {
        return icdOrMbrAttributionCode;
    }

    public void setIcdOrMbrAttributionCode(String icdOrMbrAttributionCode) {
        this.icdOrMbrAttributionCode = icdOrMbrAttributionCode;
    }

    public String getReferralFulfillmentProgramName() {
        return referralFulfillmentProgramName;
    }

    public void setReferralFulfillmentProgramName(String referralFulfillmentProgramName) {
        this.referralFulfillmentProgramName = referralFulfillmentProgramName;
    }

    public String getReferralFulfillmentProgramAddress() {
        return referralFulfillmentProgramAddress;
    }

    public void setReferralFulfillmentProgramAddress(String referralFulfillmentProgramAddress) {
        this.referralFulfillmentProgramAddress = referralFulfillmentProgramAddress;
    }

    public String getReferralFulfillmentProgramPhone() {
        return referralFulfillmentProgramPhone;
    }

    public void setReferralFulfillmentProgramPhone(String referralFulfillmentProgramPhone) {
        this.referralFulfillmentProgramPhone = referralFulfillmentProgramPhone;
    }

    public String getRefFulProgramType() {
        return refFulProgramType;
    }

    public void setRefFulProgramType(String refFulProgramType) {
        this.refFulProgramType = refFulProgramType;
    }

    public String getRefFulProgramSubtype() {
        return refFulProgramSubtype;
    }

    public void setRefFulProgramSubtype(String refFulProgramSubtype) {
        this.refFulProgramSubtype = refFulProgramSubtype;
    }
}
