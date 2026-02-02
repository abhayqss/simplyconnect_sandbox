package com.scnsoft.eldermark.jms.consumer;

import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdAboutToHitNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdReachedNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdResetNotificationQueueDto;

public interface PointClickCareNotificationSender {

    void sendDailyThresholdAboutToHit(PccDailyThresholdAboutToHitNotificationQueueDto dto);

    void sendDailyThresholdReached(PccDailyThresholdReachedNotificationQueueDto dto);

    void reset(PccDailyThresholdResetNotificationQueueDto dto);

}
