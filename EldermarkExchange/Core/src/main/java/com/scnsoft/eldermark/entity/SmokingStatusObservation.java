package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SmokingStatusObservation extends BasicEntity {
    @Column(name = "effective_time_low")
    private Date effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Date effectiveTimeHigh;

    @ManyToOne
    @JoinColumn(name="value_code_id")
    private CcdCode value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_history_id", nullable = false)
    private SocialHistory socialHistory;

    public Date getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(Date effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    public SocialHistory getSocialHistory() {
        return socialHistory;
    }

    public void setSocialHistory(SocialHistory socialHistory) {
        this.socialHistory = socialHistory;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public Date getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Date effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }
}
