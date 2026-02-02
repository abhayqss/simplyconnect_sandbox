package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.util.Normalizer;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    @Qualifier("smsTwilioRestClient")
    private TwilioRestClient twilioRestClient;

    @Value("${sms.from.number}")
    private String smsFromNumber;

    @Override
    @Async
    public Future<Boolean> sendSmsNotification(String to, String body) {
        return new AsyncResult<>(sendSmsNotificationAndWait(to, body));
    }

    @Override
    public boolean sendSmsNotificationAndWait(String to, String body) {
        try {
            logger.info("Trying send SMS");

            Message message = Message.creator(
                    new PhoneNumber(toE164Format(to)),
                    new PhoneNumber(smsFromNumber),
                    body)
                    .create(twilioRestClient);

            logger.info("SMS to {} was send, sid is {}", to, message.getSid());
        } catch (Exception e) {
            logger.error("Error while sending SMS ", e);
            return false;
        }
        return true;
    }

    private String toE164Format(String to) {
        return "+" + Normalizer.normalizePhone(to);
    }

}
