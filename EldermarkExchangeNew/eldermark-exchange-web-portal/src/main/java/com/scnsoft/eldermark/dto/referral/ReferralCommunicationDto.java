package com.scnsoft.eldermark.dto.referral;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

public class ReferralCommunicationDto {

    private Long id;

    @NotEmpty(groups = SendValidation.class)
    @Size(max = 256, groups = SendValidation.class)
    private String subject;

    private String statusName;
    private String statusTitle;

    @Valid
    @NotNull(groups = SendValidation.class)
    private ReferralCommunicationItemDto request;

    @Valid
    @NotNull(groups = RespondValidation.class)
    private ReferralCommunicationItemDto response;

    @JsonIgnore
    private Long referralRequestId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getReferralRequestId() {
        return referralRequestId;
    }

    public void setReferralRequestId(Long referralRequestId) {
        this.referralRequestId = referralRequestId;
    }

    public ReferralCommunicationItemDto getRequest() {
        return request;
    }

    public void setRequest(ReferralCommunicationItemDto request) {
        this.request = request;
    }

    public ReferralCommunicationItemDto getResponse() {
        return response;
    }

    public void setResponse(ReferralCommunicationItemDto response) {
        this.response = response;
    }

    public interface SendValidation extends Default {
    }

    public interface RespondValidation extends Default {
    }
}

