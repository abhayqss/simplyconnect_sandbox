package com.scnsoft.eldermark.service.twilio.notification;

import java.util.concurrent.CompletableFuture;

public interface ConversationNotificationSender {

    CompletableFuture<Boolean> send(ConversationNotificationVO notificationVO);

    boolean sendAndWait(ConversationNotificationVO notificationVO);
}
