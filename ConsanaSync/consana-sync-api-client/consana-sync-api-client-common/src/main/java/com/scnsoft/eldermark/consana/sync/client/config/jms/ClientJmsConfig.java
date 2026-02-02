package com.scnsoft.eldermark.consana.sync.client.config.jms;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.EventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.common.config.jms.JmsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(JmsConfig.class)
public class ClientJmsConfig {

    @Bean
    Map<String, Class<?>> jacksonConverterTypeIdMappings() {
        var mapping = new HashMap<String, Class<?>>();
        mapping.put(ResidentUpdateQueueDto.class.getSimpleName(), ResidentUpdateQueueDto.class);
        mapping.put(ConsanaPatientUpdateQueueDto.class.getSimpleName(), ConsanaPatientUpdateQueueDto.class);
        mapping.put(EventCreatedQueueDto.class.getSimpleName(), EventCreatedQueueDto.class);
        mapping.put(ConsanaEventCreatedQueueDto.class.getSimpleName(), ConsanaEventCreatedQueueDto.class);
        return mapping;
    }
}
