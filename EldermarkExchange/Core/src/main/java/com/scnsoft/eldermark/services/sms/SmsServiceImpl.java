package com.scnsoft.eldermark.services.sms;

import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by pzhurba on 29-Sep-15.
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    TwilioRestClient twilioRestClient;

    @Value("${sms.from.number}")
    String smsFromNumber;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    @Async
    public Future<Boolean> sendSmsNotification(String to, String body) {
        try {
            logger.info("Trying send SMS");
            MessageFactory messageFactory = twilioRestClient.getAccount().getMessageFactory();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("To", toE164Format(to)));
            params.add(new BasicNameValuePair("From", smsFromNumber));
            params.add(new BasicNameValuePair("Body", body));
            messageFactory.create(params);
            logger.info("SMS to " + to + " was send");
        } catch (Exception e) {
            logger.error("Error while sending SMS ", e);
            return new AsyncResult<Boolean>(false);
        }
        return new AsyncResult<Boolean>(true);

    }

    private String toE164Format(String to) {
        return "+" + Normalizer.normalizePhone(to);
    }

}
