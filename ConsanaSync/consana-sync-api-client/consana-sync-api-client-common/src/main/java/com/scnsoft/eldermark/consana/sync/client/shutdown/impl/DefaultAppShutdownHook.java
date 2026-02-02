package com.scnsoft.eldermark.consana.sync.client.shutdown.impl;

import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class DefaultAppShutdownHook implements AppShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(JmsAppShutdownHook.class);

    private final ConfigurableApplicationContext ctx;

    public DefaultAppShutdownHook(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        logger.info("Registering shutdown hook for Spring application Context...");
        ctx.registerShutdownHook();
        logger.info("Done");
    }
}
