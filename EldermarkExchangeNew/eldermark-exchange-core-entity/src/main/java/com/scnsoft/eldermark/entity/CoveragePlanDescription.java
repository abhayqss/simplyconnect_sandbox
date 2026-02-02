package com.scnsoft.eldermark.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.ccd.PolicyActivity;

@Entity
public class CoveragePlanDescription extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Lob
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_activity_id", nullable = false)
    private PolicyActivity policyActivity;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PolicyActivity getPolicyActivity() {
        return policyActivity;
    }

    public void setPolicyActivity(PolicyActivity policyActivity) {
        this.policyActivity = policyActivity;
    }
}
