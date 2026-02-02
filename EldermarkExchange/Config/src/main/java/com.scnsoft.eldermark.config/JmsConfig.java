package com.scnsoft.eldermark.config;

import com.scnsoft.eldermark.services.consana.model.EventCreatedQueueDto;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateQueueDto;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:jms.properties")
@Import(JmsConfig.EnableJmsConfig.class)
public class JmsConfig {

    public static class EnableJmsCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            final Boolean enableJms = context.getEnvironment().getProperty("jms.enabled", Boolean.class);
            return Boolean.TRUE.equals(enableJms);
        }
    }

    @Conditional(EnableJmsCondition.class)
    @EnableJms
    public static class EnableJmsConfig { }

    @Bean
    public ConnectionFactory connectionFactory(@Value("${jms.artemis.user}") String user,
                                               @Value("${jms.artemis.password}") String password,
                                               @Value("${jms.artemis.url}") String brokerUrl) {
        return new ActiveMQConnectionFactory(user, password, brokerUrl);
    }

    @Bean(name = "jacksonJmsMessageConverter")
    public MessageConverter jacksonJmsMessageConverter(@Value("${jms.converter.jackson.propertyName.typeId}") String typeIdProperty) {
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName(typeIdProperty);
        converter.setTypeIdMappings(buildTypeIdMappings());
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonJmsMessageConverter) {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                 @Qualifier("jacksonJmsMessageConverter") MessageConverter messageConverter) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setSessionTransacted(true);

        return factory;
    }


    private Map<String, Class<?>> buildTypeIdMappings() {
        final Map<String, Class<?>> mapping = new HashMap<>();
        mapping.put(ResidentUpdateQueueDto.class.getSimpleName(), ResidentUpdateQueueDto.class);
        mapping.put(EventCreatedQueueDto.class.getSimpleName(), EventCreatedQueueDto.class);
        return mapping;
    }
}
