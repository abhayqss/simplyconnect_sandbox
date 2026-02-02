package com.scnsoft.eldermark.beans.reports.model.optinout;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;
import java.util.List;

public class OptInOutReportCommunityRow {
    private String communityName;
    private List<DefaultCommunityHieConsentPolicy> defaultCommunityPolicies;
    private List<OptInOutReportClientRow> clientRows;

    public static class DefaultCommunityHieConsentPolicy {
        private HieConsentPolicyType hieConsentPolicy;
        private Instant lastModifiedDate;

        public DefaultCommunityHieConsentPolicy(HieConsentPolicyType hieConsentPolicy) {
            this.hieConsentPolicy = hieConsentPolicy;
        }

        public DefaultCommunityHieConsentPolicy(HieConsentPolicyType hieConsentPolicy, Instant lastModifiedDate) {
            this.hieConsentPolicy = hieConsentPolicy;
            this.lastModifiedDate = lastModifiedDate;
        }

        public HieConsentPolicyType getHieConsentPolicy() {
            return hieConsentPolicy;
        }

        public void setHieConsentPolicy(HieConsentPolicyType hieConsentPolicy) {
            this.hieConsentPolicy = hieConsentPolicy;
        }

        public Instant getLastModifiedDate() {
            return lastModifiedDate;
        }

        public void setLastModifiedDate(Instant lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<OptInOutReportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(List<OptInOutReportClientRow> clientRows) {
        this.clientRows = clientRows;
    }

    public List<DefaultCommunityHieConsentPolicy> getDefaultCommunityPolicies() {
        return defaultCommunityPolicies;
    }

    public void setDefaultCommunityPolicies(List<DefaultCommunityHieConsentPolicy> defaultCommunityPolicies) {
        this.defaultCommunityPolicies = defaultCommunityPolicies;
    }
}
