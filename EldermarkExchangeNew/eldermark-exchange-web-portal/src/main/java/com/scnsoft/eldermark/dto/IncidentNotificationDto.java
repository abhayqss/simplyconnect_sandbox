package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentNotificationDto {

    private Long id;

    @NotNull
    private Long date;

    @NotEmpty
    @Size(max = 256)
    private String byWhom;

    @Size(max = 512)
    private String fullName;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    @Size(max = 256)
    private String response;

    private Long responseDate;

    @Size(max = 256)
    private String comment;

    private Boolean isNotified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getByWhom() {
        return byWhom;
    }

    public void setByWhom(String byWhom) {
        this.byWhom = byWhom;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Long getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Long responseDate) {
        this.responseDate = responseDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }
}
