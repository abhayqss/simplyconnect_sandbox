package com.scnsoft.eldermark.consana.sync.client.shutdown.impl;

import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

public class JmsAppShutdownHook implements AppShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(JmsAppShutdownHook.class);

    private final ConfigurableApplicationContext ctx;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    public JmsAppShutdownHook(ConfigurableApplicationContext ctx, JmsListenerEndpointRegistry jmsListenerEndpointRegistry) {
        this.ctx = ctx;
        this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
    }

    @Override
    public void run() {
        logger.info("Stopping jms listeners...");
        jmsListenerEndpointRegistry.stop();
        logger.info("Done");

        logger.info("Registering shutdown hook for Spring application Context...");
        ctx.registerShutdownHook();
        logger.info("Done");
    }
}
