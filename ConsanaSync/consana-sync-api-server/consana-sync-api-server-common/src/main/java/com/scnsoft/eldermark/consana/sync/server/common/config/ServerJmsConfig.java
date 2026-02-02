package com.scnsoft.eldermark.consana.sync.server.common.config;

import com.scnsoft.eldermark.consana.sync.common.config.jms.JmsConfig;
import com.scnsoft.eldermark.consana.sync.server.common.model.dto.DocumentUploadQueueDto;
import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Import(JmsConfig.class)
public class ServerJmsConfig {

    @Bean
    Map<String, Class<?>> jacksonConverterTypeIdMappings() {
        return Map.of(ReceiveConsanaPatientQueueDto.class.getSimpleName(), ReceiveConsanaPatientQueueDto.class,
                DocumentUploadQueueDto.class.getSimpleName(), DocumentUploadQueueDto.class);
    }

}
