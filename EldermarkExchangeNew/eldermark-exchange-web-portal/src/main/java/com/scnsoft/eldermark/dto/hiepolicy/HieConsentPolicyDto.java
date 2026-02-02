package com.scnsoft.eldermark.dto.hiepolicy;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import javax.validation.constraints.NotNull;

public class HieConsentPolicyDto {

    private Long clientId;

    @NotNull
    private HieConsentPolicyType value;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public HieConsentPolicyType getValue() {
        return value;
    }

    public void setValue(HieConsentPolicyType value) {
        this.value = value;
    }
}
