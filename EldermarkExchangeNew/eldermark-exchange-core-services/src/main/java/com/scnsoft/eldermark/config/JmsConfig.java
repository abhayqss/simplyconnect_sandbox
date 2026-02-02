package com.scnsoft.eldermark.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdAboutToHitNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdReachedNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdResetNotificationQueueDto;
import com.scnsoft.eldermark.jms.dto.DocumentUploadQueueDto;
import com.scnsoft.eldermark.jms.dto.EventCreatedQueueDto;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateQueueDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
@PropertySources({
        @PropertySource("classpath:config/jms/jms.properties"),
        @PropertySource("classpath:config/jms/jms-${spring.profiles.active}.properties")
})
public class JmsConfig {

    @Configuration
    @ConditionalOnProperty(value = "jms.enabled")
    @EnableJms
    //as nested in order to read jms.enabled from jms.properties files
    public static class EnabledJmsConfig {
        //common beans needed for both sending and receiving jms messages
        //make sure to set jms.enabled=true along with specific 'enable' properties if needed

        @Bean
        public MessageConverter jacksonJmsMessageConverter() {
            var objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(objectMapper);
            converter.setTargetType(MessageType.TEXT);
            converter.setTypeIdPropertyName("_type");
            converter.setTypeIdMappings(jacksonConverterTypeIdMappings());
            return converter;
        }

        @Bean
        Map<String, Class<?>> jacksonConverterTypeIdMappings() {
            return Map.of(
                    DocumentUploadQueueDto.class.getSimpleName(), DocumentUploadQueueDto.class,
                    ResidentUpdateQueueDto.class.getSimpleName(), ResidentUpdateQueueDto.class,
                    EventCreatedQueueDto.class.getSimpleName(), EventCreatedQueueDto.class,
                    PccDailyThresholdAboutToHitNotificationQueueDto.class.getSimpleName(), PccDailyThresholdAboutToHitNotificationQueueDto.class,
                    PccDailyThresholdReachedNotificationQueueDto.class.getSimpleName(), PccDailyThresholdReachedNotificationQueueDto.class,
                    PccDailyThresholdResetNotificationQueueDto.class.getSimpleName(), PccDailyThresholdResetNotificationQueueDto.class
                    );
        }
    }
}