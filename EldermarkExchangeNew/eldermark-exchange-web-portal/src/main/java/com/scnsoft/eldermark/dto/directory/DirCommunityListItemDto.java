package com.scnsoft.eldermark.dto.directory;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;

public class DirCommunityListItemDto extends IdentifiedNamedEntityDto {

    private HieConsentPolicyType hieConsentPolicyName;
    private String hieConsentPolicyTitle;
    private boolean canAddContact;
    private boolean canViewOrHasAccessibleClient;
    private boolean canAddClient;
    private boolean canAddGroupNote;
    private boolean canViewInboundReferrals;
    private boolean canViewOutboundReferrals;

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

    public boolean isCanAddContact() {
        return canAddContact;
    }

    private boolean getCanAddContact() {
        return canAddContact;
    }

    public void setCanAddContact(boolean canAddContact) {
        this.canAddContact = canAddContact;
    }

    public boolean getCanViewOrHasAccessibleClient() {
        return canViewOrHasAccessibleClient;
    }

    public void setCanViewOrHasAccessibleClient(boolean canViewOrHasAccessibleClient) {
        this.canViewOrHasAccessibleClient = canViewOrHasAccessibleClient;
    }

    public boolean isCanViewOrHasAccessibleClient() {
        return canViewOrHasAccessibleClient;
    }

    public boolean getCanAddClient() {
        return canAddClient;
    }

    public void setCanAddClient(boolean canAddClient) {
        this.canAddClient = canAddClient;
    }

    public boolean getCanAddGroupNote() {
        return canAddGroupNote;
    }

    public void setCanAddGroupNote(boolean canAddGroupNote) {
        this.canAddGroupNote = canAddGroupNote;
    }

    public boolean getCanViewInboundReferrals() {
        return canViewInboundReferrals;
    }

    public void setCanViewInboundReferrals(boolean canViewInboundReferrals) {
        this.canViewInboundReferrals = canViewInboundReferrals;
    }

    public boolean getCanViewOutboundReferrals() {
        return canViewOutboundReferrals;
    }

    public void setCanViewOutboundReferrals(boolean canViewOutboundReferrals) {
        this.canViewOutboundReferrals = canViewOutboundReferrals;
    }
}
