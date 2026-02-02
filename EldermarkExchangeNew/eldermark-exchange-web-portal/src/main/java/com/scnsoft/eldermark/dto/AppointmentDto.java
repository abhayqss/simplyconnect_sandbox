package com.scnsoft.eldermark.dto;

import javax.validation.constraints.NotNull;

/**
 * This DTO is intended to represent submitted marketplace appointment Basic Things
 */
public class AppointmentDto {
    
    private Boolean isEmergencyVisit;
    private String name;
    private String email;
    private Long date;
    private Long requestDate;   
    private String phone;
    private String comment;
    public Boolean getIsEmergencyVisit() {
        return isEmergencyVisit;
    }
    public void setIsEmergencyVisit(Boolean isEmergencyVisit) {
        this.isEmergencyVisit = isEmergencyVisit;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Long getDate() {
        return date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public Long getRequestDate() {
        return requestDate;
    }
    public void setRequestDate(Long requestDate) {
        this.requestDate = requestDate;
    }
    @NotNull
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    
}
