package com.scnsoft.eldermark.hl7v2.config;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import com.scnsoft.eldermark.hl7v2.HapiMessagesReceiverApplication;
import com.scnsoft.eldermark.hl7v2.HapiSslSocketFactory;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HL7v2Config {

    @Bean
    public HapiContext hl7v2HapiContext(HapiSslSocketFactory hapiSslSocketFactory) {
        HapiContext context = HapiUtils.basicHapiContext();
        context.setSocketFactory(hapiSslSocketFactory);

        return context;
    }

    @Bean
    public HL7Service hl7TcpServer(HapiContext hl7v2HapiContext,
                                   HapiMessagesReceiverApplication receiverApplication,
                                   @Value("${hl7server.port}") int port) {
        HL7Service server = hl7v2HapiContext.newServer(port, true);
        server.registerApplication("*", "*", receiverApplication);

        return server;
    }

    @Bean
    public HapiSslSocketFactory hapiSslSocketFactory(
            @Value("${hl7server.keystore.path}") String keyStorePath,
            @Value("${hl7server.keystore.password}") String keyStorePassword,
            @Value("${hl7server.keystore.type}") String keyStoreType,
            @Value("${hl7server.truststore.path}") String trustStorePath,
            @Value("${hl7server.truststore.password}") String trustStorePassword,
            @Value("${hl7server.truststore.type}") String trustStoreType
    ) {
        return new HapiSslSocketFactory(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath),
                keyStorePassword,
                keyStoreType,

                Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStorePath),
                trustStorePassword,
                trustStoreType
        );
    }

}
