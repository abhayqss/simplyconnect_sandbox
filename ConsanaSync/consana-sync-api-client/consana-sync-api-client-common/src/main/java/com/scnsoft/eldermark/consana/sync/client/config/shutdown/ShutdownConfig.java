package com.scnsoft.eldermark.consana.sync.client.config.shutdown;

import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import com.scnsoft.eldermark.consana.sync.client.shutdown.impl.DefaultAppShutdownHook;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownConfig {

    @Bean
    @ConditionalOnMissingBean(AppShutdownHook.class)
    public AppShutdownHook appShutdownHook(ConfigurableApplicationContext ctx) {
        return new DefaultAppShutdownHook(ctx);
    }

    @Bean
    public ApplicationListener<ApplicationStartedEvent> applicationStartedListener(AppShutdownHook appShutdownHook) {
        return applicationStartedEvent -> Runtime.getRuntime().addShutdownHook(new Thread(appShutdownHook));
    }
}
