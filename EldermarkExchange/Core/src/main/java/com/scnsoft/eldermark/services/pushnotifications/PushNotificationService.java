package com.scnsoft.eldermark.services.pushnotifications;

import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 *
 * @author phomal
 * Created on 6/8/2017.
 */
public interface PushNotificationService {

    Collection<String> getTokens(Long userId, PushNotificationRegistration.ServiceProvider serviceProvider);

    Future<Boolean> send(PushNotificationVO pushNotificationDto);   
}
