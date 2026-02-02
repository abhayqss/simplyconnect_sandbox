package com.scnsoft.eldermark.dto.referral;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReferralDeclineDto {

    @NotNull
    private Long referralDeclineReasonId;
    @Size(max = 5_000)
    private String comment;

    public Long getReferralDeclineReasonId() {
        return referralDeclineReasonId;
    }

    public void setReferralDeclineReasonId(Long referralDeclineReasonId) {
        this.referralDeclineReasonId = referralDeclineReasonId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
