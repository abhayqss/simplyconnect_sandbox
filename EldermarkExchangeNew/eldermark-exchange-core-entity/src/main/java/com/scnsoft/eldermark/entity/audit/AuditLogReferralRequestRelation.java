package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.referral.ReferralRequest;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_ReferralRequest")
public class AuditLogReferralRequestRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "referral_request_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ReferralRequest referralRequest;

    @Column(name = "referral_request_id", nullable = false)
    private Long referralRequestId;

    public ReferralRequest getReferralRequest() {
        return referralRequest;
    }

    public void setReferralRequest(ReferralRequest referralRequest) {
        this.referralRequest = referralRequest;
    }

    public Long getReferralRequestId() {
        return referralRequestId;
    }

    public void setReferralRequestId(Long referralRequestId) {
        this.referralRequestId = referralRequestId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(referralRequestId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.REFERRAL_REQUEST;
    }
}
