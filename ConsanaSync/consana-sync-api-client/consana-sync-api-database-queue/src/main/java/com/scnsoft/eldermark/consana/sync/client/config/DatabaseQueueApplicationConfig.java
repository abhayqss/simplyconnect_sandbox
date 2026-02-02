package com.scnsoft.eldermark.consana.sync.client.config;

import com.scnsoft.eldermark.consana.sync.client.config.shutdown.ShutdownConfig;
import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import com.scnsoft.eldermark.consana.sync.client.shutdown.CronAppShutdownHook;
import com.scnsoft.eldermark.consana.sync.common.config.db.DbConfig;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@Import({ShutdownConfig.class, DatabaseQueueJmsConfig.class, DbConfig.class})
@EnableScheduling
public class DatabaseQueueApplicationConfig {

    @Bean
    AppShutdownHook appShutdownHook(ConfigurableApplicationContext ctx, ThreadPoolTaskScheduler scheduler) {
        return new CronAppShutdownHook(ctx, scheduler);
    }

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer() {
        return taskScheduler -> {
            taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
            taskScheduler.setAwaitTerminationSeconds(60);
        };
    }
}
