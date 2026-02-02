package com.scnsoft.eldermark.service.twilio.notification;

import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;

import java.util.Set;

public interface ConversationNotificationService {

    void sendNewMessageNotifications(Set<String> identities, String conversationSid, String messageSid, String mediaJson, String attributesJson);

    void sendServiceMessageNotifications(ServiceMessage serviceMessage,
                                         String messageSid,
                                         Long employeeId,
                                         String devicePushNotificationTokenToExclude);
}
