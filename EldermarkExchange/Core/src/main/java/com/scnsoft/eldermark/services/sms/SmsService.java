package com.scnsoft.eldermark.services.sms;

import java.util.concurrent.Future;

/**
 * Created by pzhurba on 29-Sep-15.
 */
public interface SmsService {
    Future<Boolean> sendSmsNotification(String to, String body);
}
