package com.scnsoft.eldermark.service.pushnotification.sender;

import com.eatthepath.pushy.apns.ApnsClient;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for sending APNS PROD push notifications (both PushKit and UserNotifications) for all applications
 */
@Service
@Transactional
@ConditionalOnProperty(value = "apns.enableProd", havingValue = "true")
public class ProdApnsPushNotificationSender extends BaseApnsPushNotificationSender {

    @Autowired
    public ProdApnsPushNotificationSender(@Qualifier("prodApnsClient") ApnsClient apnsClient) {
        super(apnsClient);
    }

    protected PushNotificationRegistration.ServiceProvider resolveServiceProvider(PushNotificationVO pushNotificationVO) {
        return pushNotificationVO.getIosSettings().isVOIP() ?
                PushNotificationRegistration.ServiceProvider.APNS_PK :
                PushNotificationRegistration.ServiceProvider.APNS_UN;
    }
}
