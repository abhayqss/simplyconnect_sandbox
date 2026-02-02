package com.scnsoft.eldermark.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.phr.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.dao.phr.chat.PhrChatUserDao;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;

/**
 * @see <a href="https://developers.google.com/cloud-messaging/registration">Google Cloud Messaging - Registering Client Apps</a>
 * @see <a href="https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/APNSOverview.html#//apple_ref/doc/uid/TP40008194-CH8-SW1">Local and Remote Notification Programming Guide - Apple Push Notifications Service</a>
 * @see <a href="https://firebase.google.com/docs/cloud-messaging/server">Firebase - About Firebase Cloud Messaging Server</a>
 * @author phomal
 * Created on 6/2/2017
 */
@Service
public class PushNotificationsService extends BasePhrService {

    private PushNotificationRegistrationDao pushNotificationRegistrationDao;

    protected PushNotificationService pushNotificationService;

    @Autowired
    public void setPushNotificationRegistrationDao(PushNotificationRegistrationDao pushNotificationRegistrationDao) {
        this.pushNotificationRegistrationDao = pushNotificationRegistrationDao;
    }

    @Autowired
    public void setPushNotificationService(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Autowired
    private PhrChatUserDao phrChatUserDao;

    public Boolean register(Long userId, String token, PushNotificationRegistration.ServiceProvider service) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        User user = userDao.getOne(userId);

        PushNotificationRegistration pushNotificationRegistration = pushNotificationRegistrationDao
                .findByDeviceTokenAndServiceProviderAndAppName(token, service, PushNotificationRegistration.PHR_APP);
        if (pushNotificationRegistration == null) {
            pushNotificationRegistration = new PushNotificationRegistration();
            pushNotificationRegistration.setDeviceToken(token);
            pushNotificationRegistration.setServiceProvider(service);
            pushNotificationRegistration.setAppName(PushNotificationRegistration.PHR_APP);
        } else if (userId.equals(pushNotificationRegistration.getUser().getId())) {
            return Boolean.TRUE;
        }
        pushNotificationRegistration.setUser(user);
        try {
            pushNotificationRegistrationDao.save(pushNotificationRegistration);
        } catch (DataIntegrityViolationException ignored) {}

        return Boolean.TRUE;
    }

    @Transactional
    public void send(Long userId, Long chatUserId, String title, String text, String dataName, String dataValue,
            String chatPayload, PushNotificationType pushNotificationType) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        User user = userDao.getOne(userId);
        String chatUserPhoneNo = user.getEmployeePhone();
        if (chatUserId != null) {
            user = userDao.getOne(chatUserId);
        }

        final PushNotificationVO pushNotification = createPushNotification(user, title, text, dataName, dataValue,
                chatPayload, pushNotificationType, chatUserPhoneNo, chatUserId);
        pushNotificationService.send(pushNotification);
    }

    private PushNotificationVO createPushNotification(User user, String title, String text, String dataName,
            String dataValue, String chatPayload, PushNotificationType pushNotificationType, String phoneNo,
            Long chatUserId) {
        final Collection<String> tokens = pushNotificationService.getTokens(user.getId(),
                PushNotificationRegistration.ServiceProvider.FCM);

        final Map<String, Object> payload = new HashMap<>();
        payload.put("id", pushNotificationType != null ? pushNotificationType.getNotificationId()
                : PushNotificationType.DEBUG.getNotificationId());
        payload.put("userId", user.getId());
        if (chatUserId != null)
            payload.put("chatUserId", phrChatUserDao.findByNotifyUserId(chatUserId).getId());
        payload.put("phoneNumber", phoneNo);
        payload.put("fullName", user.getFullName());
        payload.put("chatData", chatPayload);
        if (dataName != null && dataValue != null) {
            payload.put(dataName, dataValue);
        }
        final PushNotificationVO result = new PushNotificationVO();
        result.setTokens(new ArrayList<>(tokens));
        result.setTitle(title);
        result.setText(text);
        result.setPayload(payload);
        result.setServiceProvider(PushNotificationRegistration.ServiceProvider.FCM);
        return result;
    }
}