package com.scnsoft.eldermark.consana.sync.common.config.auth.oauth2;

import com.scnsoft.eldermark.consana.sync.common.config.ConsanaOauth2Context;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
@ComponentScan(basePackages = {"com.scnsoft.eldermark.consana.sync.common.consana.auth.oauth2"})
@EnableConfigurationProperties(ConsanaOauth2Context.class)
@PropertySource("classpath:config/auth/oauth2/oauth2-common.properties")
public class ConsanaOauth2Config {

    @Bean
    public RetryTemplate retryTemplate(@Value("${consana.init.attempt.interval}") long initInterval, @Value("${consana.max.attempt.interval}") long maxInterval, @Value("${consana.attempt.multiplier}") double multiplier) {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(initInterval);
        exponentialBackOffPolicy.setMaxInterval(maxInterval);
        exponentialBackOffPolicy.setMultiplier(multiplier);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        //todo this causes catching all types of exceptions
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.registerListener(new LoggingRetryListener());

        return retryTemplate;
    }

    @Bean("consanaHttpClient")
    public HttpClient consanaHttpClient(@Value("${consana.truststore.path}") String truststorePath,
                                        @Value("${consana.truststore.password}") String truststorePassword,
                                        @Value("${consana.host}") String consanaHost,
                                        @Value("${consana.http.protocol}") String protocol
    ) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException, IOException {
        try (var trustStore = Thread.currentThread().getContextClassLoader().getResourceAsStream(truststorePath)) {
            var tm = new DefaultAndCustormTrustManager(trustStore, truststorePassword);

            var sslContext = SSLContext.getInstance(protocol);
            sslContext.init(null, new TrustManager[]{tm}, null);

            return HttpClients
                    .custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier((hostname, session) -> consanaHost.equals(hostname))
                    .build();
        }
    }
}

