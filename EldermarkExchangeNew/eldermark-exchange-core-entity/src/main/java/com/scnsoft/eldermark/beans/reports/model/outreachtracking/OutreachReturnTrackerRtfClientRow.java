package com.scnsoft.eldermark.beans.reports.model.outreachtracking;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.client.report.CareTeamMemberOutreachReportItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OutreachReturnTrackerRtfClientRow {
    private String primaryPayerIdentifier;
    private String mcpName;
    private String memberClientIndexNumber;
    private String memberLastName;
    private String memberFirstName;
    private String memberNewAddressIndicator;
    private String memberHomelessnessIndicator;
    private String memberResidentialAddress;
    private String memberResidentialCity;
    private String memberResidentialState;
    private String memberResidentialZip;
    private String memberNewPhoneNumberIndicator;
    private String memberPhoneNumber;
    private String memberPofIndicator;
    private String memberPof;
    private String memberEcmStatus;
    private String memberAcuityLevel;
    private Instant ecmEnrollmentStartDate;
    private Instant ecmEnrollmentEndDate;
    private Instant latestCarePlanRevisionDate;
    private Instant mostRecentCareConferenceDate;
    private Instant assessmentStartedDate;
    private Instant mostRecentCompletedAssessmentDate;
    private Instant mostRecentEncounterWithMemberDate;
    private List<CareTeamMemberOutreachReportItem> careTeamMemberOutreachReportItems = new ArrayList<>();
    private List<Instant> discontinuationDates;
    private String discontinuationReasonCode;
    private List<ClientDeactivationReason> discontinuationReasons;
    private Long inPerson;
    private Long telephonicVideo;
    private Instant memberInformationReturnTransmissionFileProductionDate;
    private String memberInformationFileReportingPeriod;
    private String ecmProviderNationalProviderIdentifier;
    private String ecmProviderName;
    private String ecmProviderTaxIdNumber;
    private List<String> ecmProviderPhoneNumbers;

    public String getPrimaryPayerIdentifier() {
        return primaryPayerIdentifier;
    }

    public void setPrimaryPayerIdentifier(String primaryPayerIdentifier) {
        this.primaryPayerIdentifier = primaryPayerIdentifier;
    }

    public String getMcpName() {
        return mcpName;
    }

    public void setMcpName(String mcpName) {
        this.mcpName = mcpName;
    }

    public String getMemberClientIndexNumber() {
        return memberClientIndexNumber;
    }

    public void setMemberClientIndexNumber(String memberClientIndexNumber) {
        this.memberClientIndexNumber = memberClientIndexNumber;
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

    public String getMemberNewAddressIndicator() {
        return memberNewAddressIndicator;
    }

    public void setMemberNewAddressIndicator(String memberNewAddressIndicator) {
        this.memberNewAddressIndicator = memberNewAddressIndicator;
    }

    public String getMemberHomelessnessIndicator() {
        return memberHomelessnessIndicator;
    }

    public void setMemberHomelessnessIndicator(String memberHomelessnessIndicator) {
        this.memberHomelessnessIndicator = memberHomelessnessIndicator;
    }

    public String getMemberResidentialAddress() {
        return memberResidentialAddress;
    }

    public void setMemberResidentialAddress(String memberResidentialAddress) {
        this.memberResidentialAddress = memberResidentialAddress;
    }

    public String getMemberResidentialCity() {
        return memberResidentialCity;
    }

    public void setMemberResidentialCity(String memberResidentialCity) {
        this.memberResidentialCity = memberResidentialCity;
    }

    public String getMemberResidentialState() {
        return memberResidentialState;
    }

    public void setMemberResidentialState(String memberResidentialState) {
        this.memberResidentialState = memberResidentialState;
    }

    public String getMemberResidentialZip() {
        return memberResidentialZip;
    }

    public void setMemberResidentialZip(String memberResidentialZip) {
        this.memberResidentialZip = memberResidentialZip;
    }

    public String getMemberNewPhoneNumberIndicator() {
        return memberNewPhoneNumberIndicator;
    }

    public void setMemberNewPhoneNumberIndicator(String memberNewPhoneNumberIndicator) {
        this.memberNewPhoneNumberIndicator = memberNewPhoneNumberIndicator;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public String getMemberPofIndicator() {
        return memberPofIndicator;
    }

    public void setMemberPofIndicator(String memberPofIndicator) {
        this.memberPofIndicator = memberPofIndicator;
    }

    public String getMemberPof() {
        return memberPof;
    }

    public void setMemberPof(String memberPof) {
        this.memberPof = memberPof;
    }

    public String getMemberEcmStatus() {
        return memberEcmStatus;
    }

    public void setMemberEcmStatus(String memberEcmStatus) {
        this.memberEcmStatus = memberEcmStatus;
    }

    public String getMemberAcuityLevel() {
        return memberAcuityLevel;
    }

    public void setMemberAcuityLevel(String memberAcuityLevel) {
        this.memberAcuityLevel = memberAcuityLevel;
    }

    public Instant getEcmEnrollmentStartDate() {
        return ecmEnrollmentStartDate;
    }

    public void setEcmEnrollmentStartDate(Instant ecmEnrollmentStartDate) {
        this.ecmEnrollmentStartDate = ecmEnrollmentStartDate;
    }

    public Instant getEcmEnrollmentEndDate() {
        return ecmEnrollmentEndDate;
    }

    public void setEcmEnrollmentEndDate(Instant ecmEnrollmentEndDate) {
        this.ecmEnrollmentEndDate = ecmEnrollmentEndDate;
    }

    public Instant getLatestCarePlanRevisionDate() {
        return latestCarePlanRevisionDate;
    }

    public void setLatestCarePlanRevisionDate(Instant latestCarePlanRevisionDate) {
        this.latestCarePlanRevisionDate = latestCarePlanRevisionDate;
    }

    public Instant getMostRecentCareConferenceDate() {
        return mostRecentCareConferenceDate;
    }

    public void setMostRecentCareConferenceDate(Instant mostRecentCareConferenceDate) {
        this.mostRecentCareConferenceDate = mostRecentCareConferenceDate;
    }

    public Instant getAssessmentStartedDate() {
        return assessmentStartedDate;
    }

    public void setAssessmentStartedDate(Instant assessmentStartedDate) {
        this.assessmentStartedDate = assessmentStartedDate;
    }

    public Instant getMostRecentCompletedAssessmentDate() {
        return mostRecentCompletedAssessmentDate;
    }

    public void setMostRecentCompletedAssessmentDate(Instant mostRecentCompletedAssessmentDate) {
        this.mostRecentCompletedAssessmentDate = mostRecentCompletedAssessmentDate;
    }

    public Instant getMostRecentEncounterWithMemberDate() {
        return mostRecentEncounterWithMemberDate;
    }

    public void setMostRecentEncounterWithMemberDate(Instant mostRecentEncounterWithMemberDate) {
        this.mostRecentEncounterWithMemberDate = mostRecentEncounterWithMemberDate;
    }

    public List<CareTeamMemberOutreachReportItem> getCareTeamMemberOutreachReportItems() {
        return careTeamMemberOutreachReportItems;
    }

    public void setCareTeamMemberOutreachReportItems(List<CareTeamMemberOutreachReportItem> careTeamMemberOutreachReportItems) {
        this.careTeamMemberOutreachReportItems = careTeamMemberOutreachReportItems;
    }

    public List<Instant> getDiscontinuationDates() {
        return discontinuationDates;
    }

    public void setDiscontinuationDates(List<Instant> discontinuationDates) {
        this.discontinuationDates = discontinuationDates;
    }

    public String getDiscontinuationReasonCode() {
        return discontinuationReasonCode;
    }

    public void setDiscontinuationReasonCode(String discontinuationReasonCode) {
        this.discontinuationReasonCode = discontinuationReasonCode;
    }

    public List<ClientDeactivationReason> getDiscontinuationReasons() {
        return discontinuationReasons;
    }

    public void setDiscontinuationReasons(List<ClientDeactivationReason> discontinuationReasons) {
        this.discontinuationReasons = discontinuationReasons;
    }

    public Long getInPerson() {
        return inPerson;
    }

    public void setInPerson(Long inPerson) {
        this.inPerson = inPerson;
    }

    public Long getTelephonicVideo() {
        return telephonicVideo;
    }

    public void setTelephonicVideo(Long telephonicVideo) {
        this.telephonicVideo = telephonicVideo;
    }

    public Instant getMemberInformationReturnTransmissionFileProductionDate() {
        return memberInformationReturnTransmissionFileProductionDate;
    }

    public void setMemberInformationReturnTransmissionFileProductionDate(Instant memberInformationReturnTransmissionFileProductionDate) {
        this.memberInformationReturnTransmissionFileProductionDate = memberInformationReturnTransmissionFileProductionDate;
    }

    public String getMemberInformationFileReportingPeriod() {
        return memberInformationFileReportingPeriod;
    }

    public void setMemberInformationFileReportingPeriod(String memberInformationFileReportingPeriod) {
        this.memberInformationFileReportingPeriod = memberInformationFileReportingPeriod;
    }

    public String getEcmProviderNationalProviderIdentifier() {
        return ecmProviderNationalProviderIdentifier;
    }

    public void setEcmProviderNationalProviderIdentifier(String ecmProviderNationalProviderIdentifier) {
        this.ecmProviderNationalProviderIdentifier = ecmProviderNationalProviderIdentifier;
    }

    public String getEcmProviderTaxIdNumber() {
        return ecmProviderTaxIdNumber;
    }

    public void setEcmProviderTaxIdNumber(String ecmProviderTaxIdNumber) {
        this.ecmProviderTaxIdNumber = ecmProviderTaxIdNumber;
    }

    public List<String> getEcmProviderPhoneNumbers() {
        return ecmProviderPhoneNumbers;
    }

    public void setEcmProviderPhoneNumbers(List<String> ecmProviderPhoneNumbers) {
        this.ecmProviderPhoneNumbers = ecmProviderPhoneNumbers;
    }

    public String getEcmProviderName() {
        return ecmProviderName;
    }

    public void setEcmProviderName(String ecmProviderName) {
        this.ecmProviderName = ecmProviderName;
    }
}
