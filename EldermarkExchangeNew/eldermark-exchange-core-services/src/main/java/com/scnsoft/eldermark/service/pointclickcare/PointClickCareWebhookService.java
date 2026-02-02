package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscriptionResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPublicGetWebhookSubscriptionList;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionStatus;

import java.util.List;

public interface PointClickCareWebhookService {

    PccPublicGetWebhookSubscriptionList getSubscriptions(PccWebhookSubscriptionStatus status, int page, int pageSize);

    PccPostWebhookSubscriptionResponse subscribe(List<String> groups);
}
