package com.scnsoft.eldermark.dto.notification;

public class AppointmentEmailNotificationDto {

    private String receiverEmail;
    private String receiverFullName;

    private String communityName;
    private String location;

    private String appointmentDateTime;
    private String appointmentDate;
    private String appointmentTime;

    private String appointmentUrl;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentUrl() {
        return appointmentUrl;
    }

    public void setAppointmentUrl(String appointmentUrl) {
        this.appointmentUrl = appointmentUrl;
    }
}
