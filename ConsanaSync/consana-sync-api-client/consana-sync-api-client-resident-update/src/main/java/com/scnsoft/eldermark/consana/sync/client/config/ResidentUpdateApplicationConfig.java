package com.scnsoft.eldermark.consana.sync.client.config;

import com.scnsoft.eldermark.consana.sync.client.config.shutdown.ShutdownConfig;
import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import com.scnsoft.eldermark.consana.sync.client.shutdown.impl.JmsAppShutdownHook;
import com.scnsoft.eldermark.consana.sync.common.config.db.DbConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({ShutdownConfig.class, UpdateJmsConverterConfig.class, DbConfig.class})
@EnableTransactionManagement
public class ResidentUpdateApplicationConfig {

    @Bean
    public AppShutdownHook appShutdownHook(ConfigurableApplicationContext ctx, JmsListenerEndpointRegistry jmsListenerEndpointRegistry) {
        return new JmsAppShutdownHook(ctx, jmsListenerEndpointRegistry);
    }
}
