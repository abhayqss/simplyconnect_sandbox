package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareWebhookDto;

public interface PointClickCareWebhookFacade {

    void acceptWebhook(PointClickCareWebhookDto dto);
}
