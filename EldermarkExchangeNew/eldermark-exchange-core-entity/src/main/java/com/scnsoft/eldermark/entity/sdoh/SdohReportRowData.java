package com.scnsoft.eldermark.entity.sdoh;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SdoHRowType;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "SdohReportRowData")
public class SdohReportRowData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sdoh_report_log_id", nullable = false)
    private SdohReportLog sdohReportLog;

    @Column(name = "sdoh_report_log_id", insertable = false, updatable = false, nullable = false)
    private Long sdohReportLogId;

    @Column(name = "service_plan_id")
    private Long servicePlanId;

    @Column(name = "need_id")
    private Long needId;

    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "resident_insurance_plan")
    private String clientInsurancePlan;

    @Column(name = "submitter_name")
    private String submitterName;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "member_last_name")
    private String memberLastName;

    @Column(name = "member_first_name")
    private String memberFirstName;

    @Column(name = "member_middle_name")
    private String memberMiddleName;

    @Column(name = "member_date_of_birth")
    private LocalDate memberDateOfBirth;

    @Column(name = "member_gender")
    private String memberGender;

    @Column(name = "member_address")
    private String memberAddress;

    @Column(name = "member_city")
    private String memberCity;

    @Column(name = "member_state")
    private String memberState;

    @Column(name = "member_zip_code")
    private String memberZipCode;

    @Column(name = "member_hicn")
    private String memberHicn;

    @Column(name = "member_card_id")
    private String memberCardId;

    @Column(name = "service_date")
    private Instant serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_referral_fulfillment")
    private SdoHRowType identificationReferralFulfillment;

    @Column(name = "icd_or_mbr_attribution_code")
    private String icdOrMbrAttributionCode;

    @Column(name = "referral_fulfillment_program_name")
    private String referralFulfillmentProgramName;

    @Column(name = "referral_fulfillment_program_address")
    private String referralFulfillmentProgramAddress;

    @Column(name = "referral_fulfillment_program_phone")
    private String referralFulfillmentProgramPhone;

    @Column(name = "ref_ful_program_type")
    private String refFulProgramType;

    @Column(name = "ref_ful_program_subtype")
    private String refFulProgramSubtype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SdohReportLog getSdohReportLog() {
        return sdohReportLog;
    }

    public void setSdohReportLog(SdohReportLog sdohReportLog) {
        this.sdohReportLog = sdohReportLog;
    }

    public Long getSdohReportLogId() {
        return sdohReportLogId;
    }

    public void setSdohReportLogId(Long sdohReportLogId) {
        this.sdohReportLogId = sdohReportLogId;
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
