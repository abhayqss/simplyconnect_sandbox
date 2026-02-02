package com.scnsoft.eldermark.dto.pointclickcare.model.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccPostWebhookSubscriptionResponse {
    private Long webhookSubscriptionId;

    public Long getWebhookSubscriptionId() {
        return webhookSubscriptionId;
    }

    public void setWebhookSubscriptionId(Long webhookSubscriptionId) {
        this.webhookSubscriptionId = webhookSubscriptionId;
    }
}
