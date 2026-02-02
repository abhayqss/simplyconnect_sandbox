package com.scnsoft.eldermark.consana.sync.client.config;

import com.scnsoft.eldermark.consana.sync.client.config.jms.ClientJmsConfig;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
@Import(ClientJmsConfig.class)
public class DispatchJmsConfig {

    @Bean
    public DefaultJmsListenerContainerFactory patientDispatchJmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                         DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory eventDispatchJmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                       DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
