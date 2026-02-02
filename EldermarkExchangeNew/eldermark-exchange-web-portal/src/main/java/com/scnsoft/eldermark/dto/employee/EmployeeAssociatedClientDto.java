package com.scnsoft.eldermark.dto.employee;

import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public class EmployeeAssociatedClientDto {

    private Long id;
    private String fullName;
    private String communityName;
    private HieConsentPolicyType hieConsentPolicyName;
    private String hieConsentPolicyTitle;
    private PrimaryContactDto primaryContact;
    private boolean shouldConfirmHieConsentPolicy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }

    public String getHieConsentPolicyTitle() {
        return hieConsentPolicyTitle;
    }

    public void setHieConsentPolicyTitle(String hieConsentPolicyTitle) {
        this.hieConsentPolicyTitle = hieConsentPolicyTitle;
    }

    public PrimaryContactDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(PrimaryContactDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public boolean isShouldConfirmHieConsentPolicy() {
        return shouldConfirmHieConsentPolicy;
    }

    public void setShouldConfirmHieConsentPolicy(boolean shouldConfirmHieConsentPolicy) {
        this.shouldConfirmHieConsentPolicy = shouldConfirmHieConsentPolicy;
    }
}
