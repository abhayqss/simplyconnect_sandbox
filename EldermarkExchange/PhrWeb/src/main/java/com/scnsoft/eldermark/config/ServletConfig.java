package com.scnsoft.eldermark.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration of additional connectors in Tomcat.
 *
 * @author phomal
 * Created on 7/11/2017.
 */
@Configuration
@Profile({"local", "test"})
public class ServletConfig {

    @Value("${server.port.http}")
    private Integer httpPort;

    // not used at the moment
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

        if (httpPort != null) {
            Connector httpConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            httpConnector.setPort(httpPort);
            tomcat.addAdditionalTomcatConnectors(httpConnector);
        }

        /*
        Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setPort(9090);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        tomcat.addAdditionalTomcatConnectors(ajpConnector);
        */

        return tomcat;
    }

}
