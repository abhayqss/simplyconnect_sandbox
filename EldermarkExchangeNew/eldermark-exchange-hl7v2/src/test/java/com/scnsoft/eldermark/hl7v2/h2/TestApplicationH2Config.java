package com.scnsoft.eldermark.hl7v2.h2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import com.scnsoft.eldermark.hl7v2.HapiSslSocketFactory;
import com.scnsoft.eldermark.service.WebServiceClientFactory;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;

@Configuration
@ComponentScan(basePackages = {"com.scnsoft.eldermark"})
@PropertySource({"classpath:application-h2.properties"})
public class TestApplicationH2Config {

    //mock for faster context startup
    @MockBean
    WebServiceClientFactory webServiceClientFactory;

    @MockBean(name = "yardiHttpClient")
    private HttpClient yardiHttpClient;


    @SpyBean
    private JdbcTemplate jdbcTemplate;

    //don't run open key in h2 database
    @PostConstruct
    public void initJdbcTemplate() {
        Mockito
                .lenient()
                .doNothing()
                .when(jdbcTemplate)
                .execute("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1");

    }


    @Bean
    public HapiContext h2hl7v2HapiContext(HapiSslSocketFactory hapiSslSocketFactory) {
        HapiContext context = new DefaultHapiContext();
//        context.setModelClassFactory(new CanonicalModelClassFactory("2.5.1"));
        context.setSocketFactory(hapiSslSocketFactory);

        return context;
    }

}
