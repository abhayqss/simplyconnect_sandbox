package com.scnsoft.eldermark.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
@ConditionalOnBean(JmsConfig.EnabledJmsConfig.class)
@ConditionalOnProperty(value = "jms.web.receive.enabled")
@PropertySource("classpath:jms-web.properties")
public class WebJmsReceiveConfig {

    @Bean
    public DefaultJmsListenerContainerFactory documentUploadJmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory pccNotificationDailyLimitAboutToHitJmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory pccNotificationDailyLimitReachedJmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory pccNotificationDailyLimitResetJmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}