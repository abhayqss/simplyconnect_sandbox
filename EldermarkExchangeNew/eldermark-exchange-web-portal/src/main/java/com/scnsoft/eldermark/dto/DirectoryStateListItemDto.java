package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public class DirectoryStateListItemDto<T> {
    private T id;
    private String label;
    private HieConsentPolicyType hieConsentPolicyName;
    private String hieConsentPolicyTitle;

    public DirectoryStateListItemDto(T id, String label, HieConsentPolicyType hieConsentPolicyName, String hieConsentPolicyTitle) {
        this.id = id;
        this.label = label;
        this.hieConsentPolicyName = hieConsentPolicyName;
        this.hieConsentPolicyTitle = hieConsentPolicyTitle;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
}
