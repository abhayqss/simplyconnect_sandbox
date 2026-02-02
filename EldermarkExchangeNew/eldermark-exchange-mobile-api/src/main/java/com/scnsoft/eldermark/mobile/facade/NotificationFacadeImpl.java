package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.dao.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.mobile.dto.notification.PushNotificationTokenRegistrationDto;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationFactory;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationType;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationFacadeImpl implements NotificationFacade {
    private static final Logger logger = LoggerFactory.getLogger(NotificationFacadeImpl.class);

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private PushNotificationRegistrationDao pushNotificationRegistrationDao;

    @Override
    public boolean register(PushNotificationTokenRegistrationDto dto) {
        var currentEmployee = loggedUserService.getCurrentEmployee();

        var pushNotificationRegistration = pushNotificationRegistrationDao.findByDeviceTokenAndServiceProviderAndAppName(
                dto.getDeviceToken(), dto.getServiceProvider(), PushNotificationRegistration.Application.SCM);

        if (pushNotificationRegistration != null) {
            if (currentEmployee.getId().equals(pushNotificationRegistration.getEmployeeId())) {
                return true;
            }
        } else {
            pushNotificationRegistration = new PushNotificationRegistration();
            pushNotificationRegistration.setDeviceToken(dto.getDeviceToken());
            pushNotificationRegistration.setServiceProvider(dto.getServiceProvider());
            pushNotificationRegistration.setAppName(PushNotificationRegistration.Application.SCM);
        }

        pushNotificationRegistration.setEmployee(currentEmployee);

        try {
            pushNotificationRegistrationDao.save(pushNotificationRegistration);
        } catch (DataIntegrityViolationException ignored) {
            logger.warn("Exception during registering push notification token", ignored);
            return false;
        }

        return true;
    }

    @Override
    public boolean sendDebug() {
        var pushNotification = PushNotificationFactory.builder(PushNotificationType.DEBUG)
                .receiver(PushNotificationRegistration.Application.SCM, loggedUserService.getCurrentEmployeeId())
                .build();

        pushNotification.setTitle("Debug title");
        pushNotification.setBody("Debug body");

        return pushNotificationService.sendAndWait(pushNotification).getDeliveredCount() > 0;
    }
}
