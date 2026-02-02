package com.scnsoft.eldermark.entity.referral;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ReferralHistory")
public class ReferralHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "referral_id")
    private Referral referral;

    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_status")
    private ReferralStatus referralStatus;

    @OneToOne
    @JoinColumn(name = "updated_by_response_id", referencedColumnName = "id")
    private ReferralRequestResponse updatedByResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Referral getReferral() {
        return referral;
    }

    public void setReferral(Referral referral) {
        this.referral = referral;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ReferralStatus getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(ReferralStatus referralStatus) {
        this.referralStatus = referralStatus;
    }

    public ReferralRequestResponse getUpdatedByResponse() {
        return updatedByResponse;
    }

    public void setUpdatedByResponse(ReferralRequestResponse updatedByResponse) {
        this.updatedByResponse = updatedByResponse;
    }
}
