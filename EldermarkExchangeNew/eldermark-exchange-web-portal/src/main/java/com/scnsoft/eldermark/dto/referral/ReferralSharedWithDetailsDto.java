package com.scnsoft.eldermark.dto.referral;

public class ReferralSharedWithDetailsDto extends ReferralSharedWithListItemDto {

    private String declineReason;
    private String comment;
    private Long preAdmitDate;

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPreAdmitDate() {
        return preAdmitDate;
    }

    public void setPreAdmitDate(Long preAdmitDate) {
        this.preAdmitDate = preAdmitDate;
    }
}
