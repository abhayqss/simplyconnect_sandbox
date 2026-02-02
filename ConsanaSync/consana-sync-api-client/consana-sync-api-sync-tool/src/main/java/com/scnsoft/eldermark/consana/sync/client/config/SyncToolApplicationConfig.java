package com.scnsoft.eldermark.consana.sync.client.config;

import com.scnsoft.eldermark.consana.sync.common.config.db.DbConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@Import({DbConfig.class})
@EnableTransactionManagement
@EnableScheduling
public class SyncToolApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("CST", ZoneId.SHORT_IDS));
    }
}
