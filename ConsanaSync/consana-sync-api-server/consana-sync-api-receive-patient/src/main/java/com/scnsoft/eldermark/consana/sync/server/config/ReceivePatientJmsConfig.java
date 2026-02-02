package com.scnsoft.eldermark.consana.sync.server.config;

import com.scnsoft.eldermark.consana.sync.server.common.config.ServerJmsConfig;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
@Import(ServerJmsConfig.class)
public class ReceivePatientJmsConfig {

    @Bean
    public DefaultJmsListenerContainerFactory patientReceiveJmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

}
