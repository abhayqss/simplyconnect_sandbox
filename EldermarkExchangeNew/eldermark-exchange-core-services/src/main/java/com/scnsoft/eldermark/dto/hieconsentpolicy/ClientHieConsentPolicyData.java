package com.scnsoft.eldermark.dto.hieconsentpolicy;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;

public class ClientHieConsentPolicyData {
    private HieConsentPolicyObtainedBy obtainedBy;
    private String obtainedFrom;
    private HieConsentPolicySource source;
    private Instant updateDateTime;
    private HieConsentPolicyType type;
    private Employee author;

    public HieConsentPolicyObtainedBy getObtainedBy() {
        return obtainedBy;
    }

    public void setObtainedBy(HieConsentPolicyObtainedBy obtainedBy) {
        this.obtainedBy = obtainedBy;
    }

    public String getObtainedFrom() {
        return obtainedFrom;
    }

    public void setObtainedFrom(String obtainedFrom) {
        this.obtainedFrom = obtainedFrom;
    }

    public HieConsentPolicySource getSource() {
        return source;
    }

    public void setSource(HieConsentPolicySource source) {
        this.source = source;
    }

    public Instant getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Instant updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public HieConsentPolicyType getType() {
        return type;
    }

    public void setType(HieConsentPolicyType type) {
        this.type = type;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }
}
