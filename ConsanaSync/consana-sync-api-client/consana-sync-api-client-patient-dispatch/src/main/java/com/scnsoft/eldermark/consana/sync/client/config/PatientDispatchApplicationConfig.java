package com.scnsoft.eldermark.consana.sync.client.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.ErrorHandlerAdapter;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.scnsoft.eldermark.consana.sync.client.config.shutdown.ShutdownConfig;
import com.scnsoft.eldermark.consana.sync.client.shutdown.AppShutdownHook;
import com.scnsoft.eldermark.consana.sync.client.shutdown.impl.JmsAppShutdownHook;
import com.scnsoft.eldermark.consana.sync.common.config.auth.oauth2.ConsanaOauth2Config;
import com.scnsoft.eldermark.consana.sync.common.config.db.DbConfig;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
@Import({ShutdownConfig.class, DispatchJmsConfig.class, ConsanaOauth2Config.class, DbConfig.class})
public class PatientDispatchApplicationConfig {

    @Bean
    public AppShutdownHook appShutdownHook(ConfigurableApplicationContext ctx, JmsListenerEndpointRegistry jmsListenerEndpointRegistry) {
        return new JmsAppShutdownHook(ctx, jmsListenerEndpointRegistry);
    }

    @Bean
    Cache<String, Instant> processedPatientsCache(@Value("${dispatchCache.size}") int cacheSize, @Value("${dispatchCache.concurrency}") int concurrency) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .concurrencyLevel(concurrency)
                .build();
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("CST", ZoneId.SHORT_IDS));
    }

    @Bean
    public FhirContext fhirContextForDstu2Hl7Org(HttpClient consanaHttpClient) {
        var context = FhirContext.forDstu2Hl7Org();
        context.getRestfulClientFactory().setHttpClient(consanaHttpClient);
        context.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        context.setParserErrorHandler(new ErrorHandlerAdapter());
        return context;
    }

}
