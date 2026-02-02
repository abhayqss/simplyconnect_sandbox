package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentNotificationsDto {

    @Valid
    private IncidentPersonalNotificationDto family;

    @Valid
    private IncidentPersonalNotificationDto friend;

    @Valid
    private IncidentRespondedNotificationDto physician;

    @Valid
    private IncidentNotificationDto adultProtectiveServices;

    @Valid
    private IncidentPersonalNotificationDto careManager;

    @Valid
    private IncidentNotificationDto ohioHealthDepartment;

    @Valid
    private IncidentNotificationDto emergency;

    @Valid
    private IncidentNotificationDto police;

    @Valid
    private IncidentCommentedNotificationDto other;

    public IncidentNotificationDto getFamily() {
        return family;
    }

    public void setFamily(IncidentPersonalNotificationDto family) {
        this.family = family;
    }

    public IncidentNotificationDto getFriend() {
        return friend;
    }

    public void setFriend(IncidentPersonalNotificationDto friend) {
        this.friend = friend;
    }

    public IncidentNotificationDto getPhysician() {
        return physician;
    }

    public void setPhysician(IncidentRespondedNotificationDto physician) {
        this.physician = physician;
    }

    public IncidentNotificationDto getAdultProtectiveServices() {
        return adultProtectiveServices;
    }

    public void setAdultProtectiveServices(IncidentNotificationDto adultProtectiveServices) {
        this.adultProtectiveServices = adultProtectiveServices;
    }

    public IncidentNotificationDto getCareManager() {
        return careManager;
    }

    public void setCareManager(IncidentPersonalNotificationDto careManager) {
        this.careManager = careManager;
    }

    public IncidentNotificationDto getOhioHealthDepartment() {
        return ohioHealthDepartment;
    }

    public void setOhioHealthDepartment(IncidentNotificationDto ohioHealthDepartment) {
        this.ohioHealthDepartment = ohioHealthDepartment;
    }

    public IncidentNotificationDto getEmergency() {
        return emergency;
    }

    public void setEmergency(IncidentNotificationDto emergency) {
        this.emergency = emergency;
    }

    public IncidentNotificationDto getPolice() {
        return police;
    }

    public void setPolice(IncidentNotificationDto police) {
        this.police = police;
    }

    public IncidentNotificationDto getOther() {
        return other;
    }

    public void setOther(IncidentCommentedNotificationDto other) {
        this.other = other;
    }
}
