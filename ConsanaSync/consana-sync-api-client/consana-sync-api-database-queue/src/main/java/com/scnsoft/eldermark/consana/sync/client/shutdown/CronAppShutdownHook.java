package com.scnsoft.eldermark.consana.sync.client.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class CronAppShutdownHook implements AppShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(CronAppShutdownHook.class);
    private final ConfigurableApplicationContext ctx;
    private final ThreadPoolTaskScheduler scheduler;

    public CronAppShutdownHook(ConfigurableApplicationContext ctx, ThreadPoolTaskScheduler scheduler) {
        this.ctx = ctx;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        logger.info("Waiting for cron stop...");
        scheduler.shutdown();
        logger.info("Cron stopped.");

        logger.info("Registering shutdown hook for Spring application Context...");
        ctx.registerShutdownHook();
        logger.info("Spring shutdown hook registered");
    }
}
