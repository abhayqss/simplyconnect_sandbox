package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class CoveragePlanDescription extends LegacyIdAwareEntity {
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
