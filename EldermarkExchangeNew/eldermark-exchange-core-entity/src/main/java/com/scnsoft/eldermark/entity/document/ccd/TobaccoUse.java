package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class TobaccoUse extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "effective_time_low")
    private Date effectiveTimeLow;

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

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public SocialHistory getSocialHistory() {
        return socialHistory;
    }

    public void setSocialHistory(SocialHistory socialHistory) {
        this.socialHistory = socialHistory;
    }
}
