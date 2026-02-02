package com.scnsoft.eldermark.service;

import java.util.concurrent.Future;

public interface SmsService {
    Future<Boolean> sendSmsNotification(String to, String body);

    boolean sendSmsNotificationAndWait(String to, String body);
}
