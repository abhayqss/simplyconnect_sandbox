package com.scnsoft.eldermark.config;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGenerator;
import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGeneratorImpl;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.service.pointclickcare.PccIntegrationOrPatientMatchEnabledCondition;
import com.scnsoft.eldermark.service.pushnotification.sender.FcmPushNotificationSender;
import com.scnsoft.eldermark.service.pushnotification.sender.LegacyFcmPushNotificationSender;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.sftp.SftpSessionManagerFactory;
import com.scnsoft.eldermark.util.CertificateLoadUtil;
import com.scnsoft.eldermark.util.ClasspathURIResolverFactory;
import com.scnsoft.eldermark.util.MutualTLSUtils;
import com.scnsoft.eldermark.util.PrivateKeyLoadUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
@PropertySources({
        @PropertySource("classpath:config/services.properties"),
        @PropertySource("classpath:config/services-${spring.profiles.active}.properties"),

        @PropertySource("classpath:config/xds/xds.properties"),
        @PropertySource("classpath:config/xds/xds-${spring.profiles.active}.properties"),

        @PropertySource("classpath:config/ccd/ccd-document.properties")
})
public class CoreServicesConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Bean
    public RestTemplateBuilder jsonRestTemplateBuilder(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        var builder = new RestTemplateBuilder()
                .messageConverters(mappingJackson2HttpMessageConverter);
        return builder;
    }

    @SuppressFBWarnings({"XXE_DTD_TRANSFORM_FACTORY", "XXE_XSLT_TRANSFORM_FACTORY"})
    @Bean
    public Templates lantanaCdaTransformationTemplate(@Value("classpath:${cda.transformation.lantana.xsl}") Resource lantanaXsl,
                                                      @Value("${cda.transformation.lantana.base}") String baseDir) throws IOException, TransformerConfigurationException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();

        transformerFactory.setURIResolver(ClasspathURIResolverFactory.inClassPathDirectory(baseDir));
        return transformerFactory.newTemplates(new StreamSource(lantanaXsl.getInputStream()));
    }

    @Bean
    public SessionManager apolloSftpSessionManager(
            @Value("${apollo.sftp.host}") String hostname,
            @Value("${apollo.sftp.port}") Integer port,
            @Value("${apollo.sftp.user}") String username,
            @Value("${apollo.sftp.password}") String password,
            @Value("classpath:sftp/known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }

    @Bean
    public SessionManager philipsSftpSessionManager(
            @Value("${philips.sftp.host}") String hostname,
            @Value("${philips.sftp.port}") Integer port,
            @Value("${philips.sftp.user}") String username,
            @Value("${philips.sftp.password}") String password,
            @Value("classpath:sftp/known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }

    @Bean
    public SessionManager healthPartnersSftpSessionManager(
            @Value("${healthPartners.sftp.host}") String hostname,
            @Value("${healthPartners.sftp.port}") Integer port,
            @Value("${healthPartners.sftp.user}") String username,
            @Value("${healthPartners.sftp.password}") String password,
            @Value("classpath:sftp/known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }

    @Bean
    public HapiContext apolloHapiContext() {
        return new DefaultHapiContext();
    }

    @Bean
    @ConditionalOnMissingBean(LoggedUserService.class)
    public LoggedUserService notImplementedLoggedUserService() {
        return new LoggedUserService() {

            @Override
            public Optional<UserPrincipal> getCurrentUser() {
                return Optional.empty();
            }

            @Override
            public Employee getCurrentEmployee() {
                return null;
            }

            @Override
            public Long getCurrentEmployeeId() {
                return null;
            }

            @Override
            public List<Employee> getCurrentAndLinkedEmployees() {
                return Collections.emptyList();
            }

            @Override
            public void addRecordSearchFoundClientIds(Collection<Long> clientIds) {

            }
        };
    }

    //using new Http V1 Api instead
//    @Bean
    public LegacyFcmPushNotificationSender phrLegacyFcmPushNotificationSender(ObjectMapper objectMapper, @Value("${phr.fcm.server.key}") String fcmServerKey) {
        return new LegacyFcmPushNotificationSender(objectMapper, fcmServerKey, PushNotificationRegistration.Application.PHR);
    }


    @Bean
    public FirebaseApp phrFirebaseApp(@Value("classpath:push-notification/phr-firebase-key.json") Resource jsonKeyFile) throws IOException {
        removeFirebaseAppIfPresent(PushNotificationRegistration.Application.PHR.name());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(jsonKeyFile.getInputStream()))
                .build();

        return FirebaseApp.initializeApp(options, PushNotificationRegistration.Application.PHR.name());
    }

    @Bean
    public FirebaseApp scmFirebaseApp(@Value("classpath:push-notification/scm-firebase-key.json") Resource jsonKeyFile) throws IOException {
        removeFirebaseAppIfPresent(PushNotificationRegistration.Application.SCM.name());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(jsonKeyFile.getInputStream()))
                .build();

        return FirebaseApp.initializeApp(options, PushNotificationRegistration.Application.SCM.name());
    }

    private void removeFirebaseAppIfPresent(String appName) {
        //due to the fact that apps are stored in static variable within FirebaseApp,
        //context before/after integration tests marked as DirtiesContext can't be recreated
        //because app already exists and FirebaseApp.initializeApp fails.
        //therefore we have to clean apps if exists
        if (FirebaseApp.getApps().stream().map(FirebaseApp::getName).anyMatch(appName::equals)) {
            var app = FirebaseApp.getInstance(appName);
            app.delete();
        }
    }

    @Bean
    public FcmPushNotificationSender phrFcmPushNotificationSender(@Qualifier("phrFirebaseApp") FirebaseApp app) {
        return new FcmPushNotificationSender(app, PushNotificationRegistration.Application.PHR);
    }

    @Bean
    public FcmPushNotificationSender scmFcmPushNotificationSender(@Qualifier("scmFirebaseApp") FirebaseApp app) {
        return new FcmPushNotificationSender(app, PushNotificationRegistration.Application.SCM);
    }

    @Bean
    @ConditionalOnProperty(value = "apns.enableProd", havingValue = "true")
    public ApnsClient prodApnsClient(@Value("classpath:${apns.key.file}") Resource apnsKeyFile,
                                     @Value("${apns.teamId}") String teamId,
                                     @Value("${apns.keyId}") String keyId
    ) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                .setSigningKey(ApnsSigningKey.loadFromInputStream(apnsKeyFile.getInputStream(), teamId, keyId))
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "apns.enableSandbox", havingValue = "true")
    public ApnsClient sandboxApnsClient(@Value("classpath:${apns.key.file}") Resource apnsKeyFile,
                                        @Value("${apns.teamId}") String teamId,
                                        @Value("${apns.keyId}") String keyId
    ) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setSigningKey(ApnsSigningKey.loadFromInputStream(apnsKeyFile.getInputStream(), teamId, keyId))
                .build();
    }

    @Bean
    public DocutrackApiSslSocketFactoryGenerator docutrackApiSslSocketFactoryGenerator() {
        return new DocutrackApiSslSocketFactoryGeneratorImpl();
    }

    @Bean(name = "rxnormRetryTemplate")
    public RetryTemplate rxnormRetryTemplate(@Value("${rxnorm.init.attempt.interval}") long initInterval,
                                             @Value("${rxnorm.max.attempt.interval}") long maxInterval,
                                             @Value("${rxnorm.attempt.multiplier}") double multiplier,
                                             @Value("${rxnorm.max.attempts}") int maxAttempts) {
        RetryTemplate retryTemplate = new RetryTemplate();

        var exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(initInterval);
        exponentialBackOffPolicy.setMaxInterval(maxInterval);
        exponentialBackOffPolicy.setMultiplier(multiplier);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        var retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.registerListener(new LoggingRetryListener());

        return retryTemplate;
    }

    @Bean(name = "ndcApiRestTemplate")
    public RestTemplate ndcApiRestTemplate(@Qualifier("jsonRestTemplateBuilder") RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    @Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
    public SSLContext pccSslContext(
            @Value("${pcc.mutualTLS.enabled}") boolean mutualTlsEnabled,
            @Value("${app.server.certFile}") String certFile,
            @Value("${app.server.certChainFile}") String certChainFile,
            @Value("${app.server.keyFile}") String keyFile
    ) throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (mutualTlsEnabled) {
            var clientKey = PrivateKeyLoadUtil.loadKeyFromFile(keyFile);
            if (clientKey == null) {
                throw new RuntimeException("Missing client key");
            }

            var domainCert = CertificateLoadUtil.loadCertificatesFromFile(certFile);
            if (CollectionUtils.isEmpty(domainCert)) {
                throw new RuntimeException("Missing domain certificate");
            }

            var chain = CertificateLoadUtil.loadCertificatesFromFile(certChainFile);
            if (CollectionUtils.isEmpty(chain)) {
                throw new RuntimeException("Missing intermediate certificates chain");
            }

            return MutualTLSUtils.clientAuthSslContext(
                    "TLSV1.2",
                    clientKey,
                    Stream.concat(domainCert.stream(), chain.stream()).toArray(Certificate[]::new)
            );
        } else {
            var sslContext = SSLContext.getInstance("TLSV1.2");
            sslContext.init(null, null, null);
            return sslContext;
        }
    }

    @Bean
    @Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
    public RestTemplateBuilder pccRestTemplateBuilder(
            @Qualifier("jsonRestTemplateBuilder") RestTemplateBuilder jsonRestTemplateBuilder,
            @Qualifier("pccSslContext") SSLContext pccSslContext) {
        return jsonRestTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(
                        HttpClients.custom().setSSLContext(pccSslContext).build()
                ));
    }
}
