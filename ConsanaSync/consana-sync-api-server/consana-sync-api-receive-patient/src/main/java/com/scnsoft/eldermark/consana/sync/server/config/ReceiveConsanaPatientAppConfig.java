package com.scnsoft.eldermark.consana.sync.server.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.ErrorHandlerAdapter;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import com.scnsoft.eldermark.consana.sync.common.config.auth.oauth2.ConsanaOauth2Config;
import com.scnsoft.eldermark.consana.sync.common.config.db.DbConfig;
import com.scnsoft.eldermark.consana.sync.server.common.config.ServerJmsConfig;
import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@Import({ServerJmsConfig.class, DbConfig.class, ConsanaOauth2Config.class})
@EnableTransactionManagement
public class ReceiveConsanaPatientAppConfig {

    @Bean
    public FhirContext fhirContextForDstu2Hl7Org(HttpClient consanaHttpClient) {
        var context = FhirContext.forDstu2Hl7Org();
        context.getRestfulClientFactory().setHttpClient(consanaHttpClient);
        context.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        context.setParserErrorHandler(new ErrorHandlerAdapter());
        return context;
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("CST", ZoneId.SHORT_IDS));
    }

}
