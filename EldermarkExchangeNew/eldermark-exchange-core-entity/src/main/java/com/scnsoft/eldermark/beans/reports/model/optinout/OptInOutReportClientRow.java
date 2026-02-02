package com.scnsoft.eldermark.beans.reports.model.optinout;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class OptInOutReportClientRow {
    private Long clientId;
    private String clientStatus;
    private String fullClientName;
    private LinkedList<HieConsentPolicy> policies = new LinkedList<>();

    public static class HieConsentPolicy {
        private HieConsentPolicyType status;
        private Instant statusUpdateTime;
        private String obtainedFrom;
        private HieConsentPolicyObtainedBy obtainedBy;
        private Instant obtainedDate;
        private HieConsentPolicySource source;

        public HieConsentPolicyType getStatus() {
            return status;
        }

        public void setStatus(HieConsentPolicyType status) {
            this.status = status;
        }

        public Instant getStatusUpdateTime() {
            return statusUpdateTime;
        }

        public void setStatusUpdateTime(Instant statusUpdateTime) {
            this.statusUpdateTime = statusUpdateTime;
        }

        public String getObtainedFrom() {
            return obtainedFrom;
        }

        public void setObtainedFrom(String obtainedFrom) {
            this.obtainedFrom = obtainedFrom;
        }

        public HieConsentPolicyObtainedBy getObtainedBy() {
            return obtainedBy;
        }

        public void setObtainedBy(HieConsentPolicyObtainedBy obtainedBy) {
            this.obtainedBy = obtainedBy;
        }

        public Instant getObtainedDate() {
            return obtainedDate;
        }

        public void setObtainedDate(Instant obtainedDate) {
            this.obtainedDate = obtainedDate;
        }

        public HieConsentPolicySource getSource() {
            return source;
        }

        public void setSource(HieConsentPolicySource source) {
            this.source = source;
        }
    }

    public LinkedList<HieConsentPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(LinkedList<HieConsentPolicy> policies) {
        this.policies = policies;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public String getFullClientName() {
        return fullClientName;
    }

    public void setFullClientName(String fullClientName) {
        this.fullClientName = fullClientName;
    }
}
