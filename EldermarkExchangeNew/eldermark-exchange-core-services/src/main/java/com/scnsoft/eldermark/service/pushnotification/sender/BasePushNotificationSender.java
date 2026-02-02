package com.scnsoft.eldermark.service.pushnotification.sender;

import com.scnsoft.eldermark.beans.projection.DeviceTokenAndAppNameAware;
import com.scnsoft.eldermark.beans.projection.DeviceTokenAware;
import com.scnsoft.eldermark.dao.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.dao.specification.PushNotificationRegistrationSpecificationGenerator;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BasePushNotificationSender implements PushNotificationSender {

    @Autowired
    protected PushNotificationRegistrationDao pushNotificationRegistrationDao;

    @Autowired
    protected PushNotificationRegistrationSpecificationGenerator pushNotificationRegistrationSpecifications;

    protected List<String> getTokens(PushNotificationVO pushNotificationVO, PushNotificationRegistration.ServiceProvider serviceProvider) {
        return getTokens(
                pushNotificationVO,
                serviceProvider,
                receiver -> true,
                DeviceTokenAware.class
        )
                .map(DeviceTokenAware::getDeviceToken)
                .collect(Collectors.toList());
    }

    protected List<String> getTokens(PushNotificationVO pushNotificationVO, PushNotificationRegistration.ServiceProvider serviceProvider,
                                     PushNotificationRegistration.Application appName) {
        return getTokens(
                pushNotificationVO,
                serviceProvider,
                receiver -> receiver.getAppName() == appName,
                DeviceTokenAware.class
        )
                .map(DeviceTokenAware::getDeviceToken)
                .collect(Collectors.toList());
    }

    private <P> Stream<P> getTokens(PushNotificationVO pushNotificationVO, PushNotificationRegistration.ServiceProvider serviceProvider,
                                    Predicate<PushNotificationVO.Receiver> appFilter,
                                    Class<P> projection) {
        return pushNotificationVO.getReceivers().stream()
                .filter(appFilter)
                .flatMap(appReceiverSettings -> pushNotificationRegistrationDao
                        .findAll(
                                pushNotificationRegistrationSpecifications.byUserIdAndServiceProviderAndAppNameExcludingToken(
                                        appReceiverSettings.getUserId(),
                                        serviceProvider,
                                        appReceiverSettings.getAppName(),
                                        pushNotificationVO.getDevicePushNotificationTokenToExclude()),
                                projection)
                        .stream());
    }

    protected Map<PushNotificationRegistration.Application, List<String>> getAppTokensMap(
            PushNotificationVO pushNotificationVO,
            PushNotificationRegistration.ServiceProvider serviceProvider) {
        return getTokens(
                pushNotificationVO,
                serviceProvider,
                receiver -> true,
                DeviceTokenAndAppNameAware.class
        )
                .collect(Collectors.groupingBy(
                        DeviceTokenAndAppNameAware::getAppName,
                        HashMap::new,
                        Collectors.mapping(
                                DeviceTokenAndAppNameAware::getDeviceToken,
                                Collectors.toList()
                        )
                ));
    }

    protected void evictTokens(List<String> tokens, PushNotificationRegistration.ServiceProvider serviceProvider) {
        pushNotificationRegistrationDao.deleteByRegIdAndServiceProvider(tokens, serviceProvider);
    }
}
