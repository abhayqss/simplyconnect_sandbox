package com.scnsoft.eldermark.dto.pointclickcare.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccPostWebhookSubscription {

    private String applicationName;
    private boolean enableRoomReservationCancellation;
    private String endUrl;
    private List<String> eventGroupList;
    private boolean includeDischarged;
    private boolean includeOutpatient;
    private String password;
    private String username;
    private String vendorExternalId;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
