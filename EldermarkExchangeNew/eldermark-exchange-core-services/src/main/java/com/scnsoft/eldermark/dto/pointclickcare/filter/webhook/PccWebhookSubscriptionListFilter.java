package com.scnsoft.eldermark.dto.pointclickcare.filter.webhook;

import com.scnsoft.eldermark.dto.pointclickcare.filter.PccGetParamsFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionStatus;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

public class PccWebhookSubscriptionListFilter extends PccGetParamsFilter {

    private final String applicationName;
    private PccWebhookSubscriptionStatus status;
    private LocalDate subscriptionDate;


    public PccWebhookSubscriptionListFilter(String applicationName) {
        this.applicationName = applicationName;
    }

    public PccWebhookSubscriptionListFilter(PccWebhookSubscriptionStatus status, String applicationName, LocalDate subscriptionDate) {
        this.status = status;
        this.applicationName = applicationName;
        this.subscriptionDate = subscriptionDate;
    }

    public PccWebhookSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PccWebhookSubscriptionStatus status) {
        this.status = status;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public LocalDate getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(LocalDate subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    @Override
    protected void fillParams(MultiValueMap<String, String> map) {
        addNonNull(map, "status", status);
        addNonNull(map, "applicationName", applicationName);
        addNonNull(map, "subscriptionDate", subscriptionDate);
    }
}
