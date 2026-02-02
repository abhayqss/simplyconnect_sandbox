package com.scnsoft.eldermark.config;

import com.scnsoft.eldermark.service.twilio.media.CustomTwilioHttpClient;
import com.scnsoft.eldermark.validation.context.TwilioConversationServiceSidRepository;
import com.scnsoft.eldermark.validation.context.TwilioConversationServiceValidator;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
@PropertySources({
        @PropertySource("classpath:twilio/twilio.properties"),
        @PropertySource(value = "classpath:twilio/twilio-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
public class TwilioConfiguration {

    //we use different twilio accounts for chats and sms on test, so we need 2 TwilioRestClient beans
    @Bean
    public TwilioRestClient smsTwilioRestClient(@Value("${twilio.sms.sid}") String sid, @Value("${twilio.sms.token}") String token) {
        return new TwilioRestClient.Builder(sid, token).build();
    }

    @Bean
    @Primary
    public TwilioRestClient twilioRestClient(@Value("${twilio.account.sid}") String accountSid,
                                             @Value("${twilio.auth.token}") String authToken) throws IllegalAccessException, NoSuchAlgorithmException, KeyManagementException {
        return new TwilioRestClient.Builder(accountSid, authToken)
                .httpClient(new CustomTwilioHttpClient())
                .build();
    }

    @Bean
    public TwilioConversationServiceValidator twilioConversationServiceValidator(
            TwilioConversationServiceSidRepository twilioConversationServiceSidRepository) {
        return new TwilioConversationServiceValidator(twilioConversationServiceSidRepository);
    }

}
