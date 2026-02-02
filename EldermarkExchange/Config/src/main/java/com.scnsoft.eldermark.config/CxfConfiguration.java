package com.scnsoft.eldermark.config;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfiguration {

    /**
     * This bean is required for java 7 only. Java uses TLSv1.0 by default, but we need TLSv1.2 for direct secure messaging.
     * So here we explicitly set protocol version as "TLSv1.2" for apache cxf HTTPConduit.
     *
     * If java version is updated to 8+ simply remove this bean, because java 8+ versions use TLSv1.2+ by default.
     *
     * @return configurer for HTTPConduit
     */
    @Bean
    public HTTPConduitConfigurer httpConduitConfigurer() {
        return new HTTPConduitConfigurer() {
            @Override
            public void configure(String name, String address, HTTPConduit c) {
                TLSClientParameters params = c.getTlsClientParameters();
                if (params == null) {
                    params = new TLSClientParameters();
                    c.setTlsClientParameters(params);
                }
                params.setSecureSocketProtocol("TLSv1.2");
            }
        };
    }
}
