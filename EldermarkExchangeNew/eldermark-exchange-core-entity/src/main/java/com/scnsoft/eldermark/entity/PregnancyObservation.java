package com.scnsoft.eldermark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;

@Entity
public class PregnancyObservation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "effective_time_low")
    private Date effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Date estimatedDateOfDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_history_id", nullable = false)
    private SocialHistory socialHistory;

    public Date getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Date effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    public Date getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public void setEstimatedDateOfDelivery(Date estimatedDateOfDelivery) {
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
    }

    public SocialHistory getSocialHistory() {
        return socialHistory;
    }

    public void setSocialHistory(SocialHistory socialHistory) {
        this.socialHistory = socialHistory;
    }
}
