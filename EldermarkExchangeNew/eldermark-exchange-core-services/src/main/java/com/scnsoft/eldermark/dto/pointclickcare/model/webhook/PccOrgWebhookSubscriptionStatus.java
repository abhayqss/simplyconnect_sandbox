package com.scnsoft.eldermark.dto.pointclickcare.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccOrgWebhookSubscriptionStatus {
    private PccWebhookSubscriptionAction action;
    private String orgUuid;
    private PccOrgWebhookSubscriptionStatusState status;

    public PccWebhookSubscriptionAction getAction() {
        return action;
    }

    public void setAction(PccWebhookSubscriptionAction action) {
        this.action = action;
    }

    public String getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(String orgUuid) {
        this.orgUuid = orgUuid;
    }

    public PccOrgWebhookSubscriptionStatusState getStatus() {
        return status;
    }

    public void setStatus(PccOrgWebhookSubscriptionStatusState status) {
        this.status = status;
    }
}
