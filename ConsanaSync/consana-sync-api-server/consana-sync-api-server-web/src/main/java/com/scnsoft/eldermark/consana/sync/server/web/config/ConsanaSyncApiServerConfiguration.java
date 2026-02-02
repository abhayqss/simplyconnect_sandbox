package com.scnsoft.eldermark.consana.sync.server.web.config;

import com.scnsoft.eldermark.consana.sync.server.common.config.ServerJmsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ServerJmsConfig.class, RequestLoggingFilterConfig.class})
public class ConsanaSyncApiServerConfiguration {
}
