package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.filter.webhook.PccWebhookSubscriptionListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscription;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscriptionResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPublicGetWebhookSubscriptionList;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
public class PointClickCareWebhookServiceImpl implements PointClickCareWebhookService {

    @Value("${pcc.webhook.auth.user}")
    private String webhookUser;

    @Value("${pcc.webhook.auth.password}")
    private String webhookUserPassword;

    @Value("${pcc.application-name}")
    private String pccScApplicationName;

    @Value("${pcc.webhook.url}")
    private String pccWebhookUrl;

    @Autowired
    private PointClickCareApiGateway pointClickCareApiGateway;

    @Override
    public PccPublicGetWebhookSubscriptionList getSubscriptions(PccWebhookSubscriptionStatus status, int page, int pageSize) {
        var filter = new PccWebhookSubscriptionListFilter(pccScApplicationName);
        filter.setStatus(status);
        return pointClickCareApiGateway.listOfWebhookSubscriptions(filter, page, pageSize);
    }

    @Override
    public PccPostWebhookSubscriptionResponse subscribe(List<String> groups) {
        PccPostWebhookSubscription body = new PccPostWebhookSubscription();
        body.setApplicationName(pccScApplicationName);
        body.setEnableRoomReservationCancellation(true);
        body.setEndUrl(pccWebhookUrl);
        body.setEventGroupList(groups);
        body.setIncludeDischarged(false);
        body.setIncludeOutpatient(true);
        body.setPassword(webhookUserPassword);
        body.setUsername(webhookUser);

        return pointClickCareApiGateway.subscribeWebhook(body);
    }
}
