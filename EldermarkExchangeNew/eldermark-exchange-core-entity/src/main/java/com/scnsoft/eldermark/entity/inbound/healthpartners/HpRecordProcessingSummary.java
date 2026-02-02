package com.scnsoft.eldermark.entity.inbound.healthpartners;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;

import java.util.Set;

public abstract class HpRecordProcessingSummary extends ProcessingSummary {

    private String memberIdentifier;

    @JsonIgnore
    private Long clientId;

    @JsonIgnore
    private Set<ResidentUpdateType> updateTypes;

    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Set<ResidentUpdateType> getUpdateTypes() {
        return updateTypes;
    }

    public void setUpdateTypes(Set<ResidentUpdateType> updateTypes) {
        this.updateTypes = updateTypes;
    }
}
