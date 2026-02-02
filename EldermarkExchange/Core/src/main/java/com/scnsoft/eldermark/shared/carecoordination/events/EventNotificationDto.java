package com.scnsoft.eldermark.shared.carecoordination.events;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by pzhurba on 13-Oct-15.
 */
public class EventNotificationDto {
    @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm a")
    private Date dateTime;
    private String notificationType;
    private String notificationText;
    private String contactName;
    private Long contactId;
    private String careTeamRole;
    private String description;
    private String responsibility;
    private String details;
//    private String destination;
    private String sentToText;
    private String organization;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(String careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

//    public String getDestination() {
//        return destination;
//    }
//
//    public void setDestination(String destination) {
//        this.destination = destination;
//    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getSentToText() {
        return sentToText;
    }

    public void setSentToText(String sentToText) {
        this.sentToText = sentToText;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
