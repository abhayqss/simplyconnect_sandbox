package com.scnsoft.eldermark.entity.client.report;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;

public class HieConsentPolicyDetailsItem {
    private Long organizationId;
    private Long communityId;
    private String communityName;
    private Long clientId;
    private Boolean isClientActive;
    private String clientFullName;
    private HieConsentPolicyType hieConsentPolicyType;
    private String optInOutObtainedFrom;
    private HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy;
    private HieConsentPolicySource hieConsentPolicySource;
    private Instant hieConsentPolicyUpdateDateTime;
    private Instant clientUpdateDatetime;

    public HieConsentPolicyDetailsItem(
            Long organizationId,
            Long communityId,
            String communityName,
            Long clientId,
            Boolean isClientActive,
            String clientFullName,
            HieConsentPolicyType hieConsentPolicyType,
            String optInOutObtainedFrom,
            HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy,
            HieConsentPolicySource hieConsentPolicySource,
            Instant hieConsentPolicyUpdateDateTime,
            Instant clientUpdateDatetime
    ) {
        this.organizationId = organizationId;
        this.communityId = communityId;
        this.communityName = communityName;
        this.clientId = clientId;
        this.isClientActive = isClientActive;
        this.clientFullName = clientFullName;
        this.hieConsentPolicyType = hieConsentPolicyType;
        this.optInOutObtainedFrom = optInOutObtainedFrom;
        this.hieConsentPolicyObtainedBy = hieConsentPolicyObtainedBy;
        this.hieConsentPolicySource = hieConsentPolicySource;
        this.hieConsentPolicyUpdateDateTime = hieConsentPolicyUpdateDateTime;
        this.clientUpdateDatetime = clientUpdateDatetime;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getIsClientActive() {
        return isClientActive;
    }

    public void setIsClientActive(Boolean isClientActive) {
        this.isClientActive = isClientActive;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getOptInOutObtainedFrom() {
        return optInOutObtainedFrom;
    }

    public void setOptInOutObtainedFrom(String optInOutObtainedFrom) {
        this.optInOutObtainedFrom = optInOutObtainedFrom;
    }

    public HieConsentPolicyObtainedBy getHieConsentPolicyObtainedBy() {
        return hieConsentPolicyObtainedBy;
    }

    public void setHieConsentPolicyObtainedBy(HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy) {
        this.hieConsentPolicyObtainedBy = hieConsentPolicyObtainedBy;
    }

    public HieConsentPolicySource getHieConsentPolicySource() {
        return hieConsentPolicySource;
    }

    public void setHieConsentPolicySource(HieConsentPolicySource hieConsentPolicySource) {
        this.hieConsentPolicySource = hieConsentPolicySource;
    }

    public Instant getHieConsentPolicyUpdateDateTime() {
        return hieConsentPolicyUpdateDateTime;
    }

    public void setHieConsentPolicyUpdateDateTime(Instant hieConsentPolicyUpdateDateTime) {
        this.hieConsentPolicyUpdateDateTime = hieConsentPolicyUpdateDateTime;
    }

    public HieConsentPolicyType getHieConsentPolicyType() {
        return hieConsentPolicyType;
    }

    public void setHieConsentPolicyType(HieConsentPolicyType hieConsentPolicyType) {
        this.hieConsentPolicyType = hieConsentPolicyType;
    }

    public Instant getClientUpdateDatetime() {
        return clientUpdateDatetime;
    }

    public void setClientUpdateDatetime(Instant clientUpdateDatetime) {
        this.clientUpdateDatetime = clientUpdateDatetime;
    }
}
