package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.referral.Referral;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Referral")
public class AuditLogReferralRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "referral_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Referral referral;

    @Column(name = "referral_id", nullable = false)
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

    @Override
    public List<Long> getRelatedIds() {
        return List.of(referralId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.REFERRAL;
    }
}
