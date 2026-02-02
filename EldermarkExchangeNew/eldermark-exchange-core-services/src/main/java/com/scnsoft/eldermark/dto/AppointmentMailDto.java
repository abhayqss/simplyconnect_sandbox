package com.scnsoft.eldermark.dto;

import java.util.List;

public class AppointmentMailDto {

    private String toEmail;

    private String organizationName;

    private String communityName;

    private String name;

    private String phone;

    private String email;

    private List<String> services;

    private Long appointmentDate;

    private String isEmergencyVisit;

    private String comment;

    private int daysToContact;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public Long getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Long appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getIsEmergencyVisit() {
        return isEmergencyVisit;
    }

    public void setIsEmergencyVisit(String isEmergencyVisit) {
        this.isEmergencyVisit = isEmergencyVisit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDaysToContact() {
        return daysToContact;
    }

    public void setDaysToContact(int daysToContact) {
        this.daysToContact = daysToContact;
    }
}
