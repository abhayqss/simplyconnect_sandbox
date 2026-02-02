package com.scnsoft.eldermark.consana.sync.common.config.jms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.Collections;
import java.util.Map;

@Configuration
@ConditionalOnClass(MessageConverter.class)
public class JmsConverterTypesMappingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "jacksonConverterTypeIdMappings")
    Map<String, Class<?>> jacksonConverterTypeIdMappings() {
        return Collections.emptyMap();
    }

}
