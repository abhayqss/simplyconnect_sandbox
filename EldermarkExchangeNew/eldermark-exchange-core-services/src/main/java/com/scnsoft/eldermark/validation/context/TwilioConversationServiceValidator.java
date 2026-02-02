package com.scnsoft.eldermark.validation.context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

@Order(Integer.MIN_VALUE)
public class TwilioConversationServiceValidator implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(TwilioConversationServiceValidator.class);

    private TwilioConversationServiceSidRepository twilioConversationServiceSidRepository;

    public TwilioConversationServiceValidator(TwilioConversationServiceSidRepository twilioConversationServiceSidRepository) {
        this.twilioConversationServiceSidRepository = twilioConversationServiceSidRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        var environment = applicationReadyEvent.getApplicationContext().getEnvironment();
        var isChatEnabled = environment.getProperty("twilio.chat.enabled", Boolean.class);
        if (!isChatEnabled) {
            return;
        }
        var serviceInProperties = environment.getProperty("twilio.chat.service.sid");
        var accountInProperties = environment.getProperty("twilio.account.sid");
        if (StringUtils.isEmpty(serviceInProperties)) {
            throw new ApplicationContextException("Property twilio.chat.service.sid is not set");
        }

        twilioConversationServiceSidRepository.findFirstByServiceSidAndAccountSid(serviceInProperties, accountInProperties)
                .orElseThrow(() -> new ApplicationContextException("Twilio conversation sid [" + serviceInProperties + "], " +
                                "account sid [" + accountInProperties + "] " +
                                "is not among allowed services list in DB table TwilioConversationServiceSid. " +
                                "Are you a developer who runs the application locally against test or prod database and forgot " +
                                "to update conversation service sid in accordance to the database?")
                        //if you are a developer who uses another local chat service just add the row to your DB
                );

        logger.info("Twilio conversation service sid is valid.");
    }
}
