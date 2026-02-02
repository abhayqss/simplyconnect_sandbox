package com.scnsoft.eldermark.beans.reports.model.outreachtracking;


import org.springframework.data.util.Pair;

import java.time.Instant;
import java.util.List;

public class OutreachReturnTrackerOtfClientRow {
    private String primaryPayerIdentifier;
    private String mcpName;
    private String ecmProviderName;
    private String ecmProviderNationalProviderIdentifier;
    private String ecmProviderTaxIdNumber;
    private String clientName;
    private String memberClientIndexNumber;
    private String providerType;
    private List<Pair<Instant, Instant>> dateOfOutreachAttempts;
    private List<String> outreachAttemptMethods;
    private String outreachAttemptSuccessful;
    private List<Long> timeSpentPerformingOutreach;

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

    public String getEcmProviderName() {
        return ecmProviderName;
    }

    public void setEcmProviderName(String ecmProviderName) {
        this.ecmProviderName = ecmProviderName;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getMemberClientIndexNumber() {
        return memberClientIndexNumber;
    }

    public void setMemberClientIndexNumber(String memberClientIndexNumber) {
        this.memberClientIndexNumber = memberClientIndexNumber;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public List<Pair<Instant, Instant>> getDateOfOutreachAttempts() {
        return dateOfOutreachAttempts;
    }

    public void setDateOfOutreachAttempts(List<Pair<Instant, Instant>> dateOfOutreachAttempts) {
        this.dateOfOutreachAttempts = dateOfOutreachAttempts;
    }

    public List<String> getOutreachAttemptMethods() {
        return outreachAttemptMethods;
    }

    public void setOutreachAttemptMethods(List<String> outreachAttemptMethods) {
        this.outreachAttemptMethods = outreachAttemptMethods;
    }

    public String getOutreachAttemptSuccessful() {
        return outreachAttemptSuccessful;
    }

    public void setOutreachAttemptSuccessful(String outreachAttemptSuccessful) {
        this.outreachAttemptSuccessful = outreachAttemptSuccessful;
    }

    public List<Long> getTimeSpentPerformingOutreach() {
        return timeSpentPerformingOutreach;
    }

    public void setTimeSpentPerformingOutreach(List<Long> timeSpentPerformingOutreach) {
        this.timeSpentPerformingOutreach = timeSpentPerformingOutreach;
    }
}
