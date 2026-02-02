package com.scnsoft.eldermark.services.palatiumcare.alert;


import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.entity.palatiumcare.PCEvent;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlertNotificationService {

    private PushNotificationService pushNotificationService;

    private UserDao userDao;

    @Autowired
    public void setPushNotificationService(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public void send(Long userId, String title, String text, String dataName, String dataValue) {
        User user = userDao.getOne(userId);
        final PushNotificationVO pushNotification = createPushNotification(user, title, text, dataName, dataValue);
        pushNotificationService.send(pushNotification);
    }

    private String buildNotificationText(Alert alert) {
        PCEvent event = alert != null ? alert.getEvent() : null;
        NotifyResident resident = event != null ? event.getResident() : null;
        Location location = resident != null ? resident.getLocation() : null;
        return location != null ? (location.getBuilding() + " , " + location.getRoom()) : "";
    }


    private String buildNotificationTitle(Alert alert) {
        PCEvent event = alert != null ? alert.getEvent() : null;
        NotifyResident notifyResident = event != null ? event.getResident() : null;
        return "New notify alert from " + (notifyResident != null ? notifyResident.getResident().getFirstName()
                + ' ' + notifyResident.getResident().getLastName() : "");
    }


    public void sendAlertNotification(Long userId, Alert alert) {
        if(alert != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            final String title = buildNotificationTitle(alert);
            final String text = buildNotificationText(alert);
            final String dataName = "data";
            String dataValue = null;
            try {
                dataValue = objectMapper.writeValueAsString(alert);
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(userId, title, text, dataName, dataValue);
        }
    }

    private PushNotificationVO createPushNotification(User user, String title, String text, String dataName, String dataValue) {

        final Collection<String> tokens = pushNotificationService.getTokens(user.getId(), PushNotificationRegistration.ServiceProvider.FCM);
        final Map<String, Object> payload = new HashMap<>();
        payload.put("id", PushNotificationType.NOTIFY_ALERT.getNotificationId());
        payload.put("userId", user.getId());
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
