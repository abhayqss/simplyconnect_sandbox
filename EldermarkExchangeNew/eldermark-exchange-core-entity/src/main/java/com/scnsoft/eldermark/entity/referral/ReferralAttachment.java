package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.BaseAttachment;

import javax.persistence.*;

@Entity
@Table(name = "ReferralAttachment")
public class ReferralAttachment extends BaseAttachment {

    @ManyToOne(optional = false)
    @JoinColumn(name = "referral_id", referencedColumnName = "id", nullable = false)
    private Referral referral;

    @Column(name = "referral_id", insertable = false, updatable = false, nullable = false)
    private Long referralId;

    public Referral getReferral() {
        return referral;
    }

    public void setReferral(Referral referral) {
        this.referral = referral;
    }

    public Long getReferralId() {
        return referralId;
    }

    public void setReferralId(Long referralId) {
        this.referralId = referralId;
    }
}
