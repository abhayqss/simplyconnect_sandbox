package com.scnsoft.eldermark.dto.pointclickcare.model.webhook;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scnsoft.eldermark.dto.pointclickcare.model.PccApplicationType;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccPublicGetWebhookSubscription {
    private PccWebhookSubscriptionAction action;
    private String applicationName;
    private PccApplicationType applicationType;
    private Instant createdDate;
    private List<PccOrgWebhookSubscriptionStatus> currentSubscription;
    private boolean enableRoomReservationCancellation;
    private String endUrl;
    private List<String> eventGroupList;
    private boolean includeDischarged;
    private boolean includeOutpatient;
    private Instant revisionDate;
    private PccWebhookSubscriptionStatus status;
    private String username;
    private String vendorExternalId;
    private Long webhookSubscriptionId;

    public PccWebhookSubscriptionAction getAction() {
        return action;
    }

    public void setAction(PccWebhookSubscriptionAction action) {
        this.action = action;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public PccApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(PccApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public List<PccOrgWebhookSubscriptionStatus> getCurrentSubscription() {
        return currentSubscription;
    }

    public void setCurrentSubscription(List<PccOrgWebhookSubscriptionStatus> currentSubscription) {
        this.currentSubscription = currentSubscription;
    }

    public boolean isEnableRoomReservationCancellation() {
        return enableRoomReservationCancellation;
    }

    public void setEnableRoomReservationCancellation(boolean enableRoomReservationCancellation) {
        this.enableRoomReservationCancellation = enableRoomReservationCancellation;
    }

    public String getEndUrl() {
        return endUrl;
    }

    public void setEndUrl(String endUrl) {
        this.endUrl = endUrl;
    }

    public List<String> getEventGroupList() {
        return eventGroupList;
    }

    public void setEventGroupList(List<String> eventGroupList) {
        this.eventGroupList = eventGroupList;
    }

    public boolean isIncludeDischarged() {
        return includeDischarged;
    }

    public void setIncludeDischarged(boolean includeDischarged) {
        this.includeDischarged = includeDischarged;
    }

    public boolean isIncludeOutpatient() {
        return includeOutpatient;
    }

    public void setIncludeOutpatient(boolean includeOutpatient) {
        this.includeOutpatient = includeOutpatient;
    }

    public Instant getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Instant revisionDate) {
        this.revisionDate = revisionDate;
    }

    public PccWebhookSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PccWebhookSubscriptionStatus status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVendorExternalId() {
        return vendorExternalId;
    }

    public void setVendorExternalId(String vendorExternalId) {
        this.vendorExternalId = vendorExternalId;
    }

    public Long getWebhookSubscriptionId() {
        return webhookSubscriptionId;
    }

    public void setWebhookSubscriptionId(Long webhookSubscriptionId) {
        this.webhookSubscriptionId = webhookSubscriptionId;
    }
}
