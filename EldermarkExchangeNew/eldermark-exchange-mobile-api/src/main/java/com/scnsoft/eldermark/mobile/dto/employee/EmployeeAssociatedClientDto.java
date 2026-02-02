package com.scnsoft.eldermark.mobile.dto.employee;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.mobile.dto.client.ClientPrimaryContactDto;

public class EmployeeAssociatedClientDto {

    private Long id;
    private HieConsentPolicyType hieConsentPolicy;
    private ClientPrimaryContactDto primaryContact;
    private boolean shouldConfirmHieConsentPolicy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HieConsentPolicyType getHieConsentPolicy() {
        return hieConsentPolicy;
    }

    public void setHieConsentPolicy(HieConsentPolicyType hieConsentPolicy) {
        this.hieConsentPolicy = hieConsentPolicy;
    }

    public ClientPrimaryContactDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ClientPrimaryContactDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public boolean getShouldConfirmHieConsentPolicy() {
        return shouldConfirmHieConsentPolicy;
    }

    public void setShouldConfirmHieConsentPolicy(boolean shouldConfirmHieConsentPolicy) {
        this.shouldConfirmHieConsentPolicy = shouldConfirmHieConsentPolicy;
    }
}
