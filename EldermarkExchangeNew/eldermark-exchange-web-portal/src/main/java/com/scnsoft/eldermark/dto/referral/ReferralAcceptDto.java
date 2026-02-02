package com.scnsoft.eldermark.dto.referral;

import javax.validation.constraints.Size;

public class ReferralAcceptDto {
    private Long serviceStartDate;
    private Long serviceEndDate;
    @Size(max = 5_000)
    private String comment;

    public Long getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(Long serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    public Long getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(Long serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
