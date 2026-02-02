package com.scnsoft.eldermark.dto.appointment;

import java.time.Instant;

public class ClientAppointmentExportClientRow {
    private Instant dateFrom;
    private Instant dateTo;
    private Instant appointmentDate;
    private Instant startTime;
    private Instant endTime;
    private String appointmentStatus;
    private String clientName;
    private String creator;
    private String serviceProviders;
    private String appointmentTitle;
    private String location;
    private String appointmentType;
    private String serviceCategory;
    private String referralSource;
    private String reasonForVisit;
    private String directionsInstructions;
    private String notes;
    private String clientReminders;
    private String notificationMethods;
    private String cellPhone;
    private String email;
    private String clientStatus;

    public Instant getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Instant appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(String serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle = appointmentTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getDirectionsInstructions() {
        return directionsInstructions;
    }

    public void setDirectionsInstructions(String directionsInstructions) {
        this.directionsInstructions = directionsInstructions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getClientReminders() {
        return clientReminders;
    }

    public void setClientReminders(String clientReminders) {
        this.clientReminders = clientReminders;
    }

    public String getNotificationMethods() {
        return notificationMethods;
    }

    public void setNotificationMethods(String notificationMethods) {
        this.notificationMethods = notificationMethods;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Instant dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Instant getDateTo() {
        return dateTo;
    }

    public void setDateTo(Instant dateTo) {
        this.dateTo = dateTo;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }
}
