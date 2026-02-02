package com.scnsoft.eldermark.hl7v2.config.integrations.yardi;

import ca.uhn.hl7v2.validation.PrimitiveTypeRule;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll.YardiHttpPollBodyGenerator;
import com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll.YardiHttpPollResponseAnalyzer;
import com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll.YardiPollFileDataToMessageStringConverter;
import com.scnsoft.eldermark.hl7v2.facade.HL7v2MessageFacade;
import com.scnsoft.eldermark.hl7v2.hapi.CorrectingValidationBuilder;
import com.scnsoft.eldermark.hl7v2.hapi.DashRemovingInDatePrimitiveTypeCorrector;
import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollBodyGenerator;
import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollResponseAnalyzer;
import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollingHL7Gateway;
import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollingInboundHL7MessageService;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Clock;

@Configuration
@ConditionalOnProperty(value = "yardi.enabled", havingValue = "true")
public class YardiConfig {

    @Bean
    public SSLContext yardiSSLContext(
            @Value("classpath:cert/yardi/yardi-poll-trust.jks") Resource trustStoreResource,
            @Value("${yardi.http.poll.truststore.password}") String trustStorePassword
    ) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {
        var trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(trustStoreResource.getInputStream(), trustStorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        var sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    @Bean
    public HttpClient yardiHttpClient(@Qualifier("yardiSSLContext") SSLContext yardiSslContext) {
        return HttpClient
                .newBuilder()
                .sslContext(yardiSslContext)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Bean
    public YardiHttpPollBodyGenerator yardiHttpPollBodyGenerator(
            @Value("${yardi.http.poll.password}") String password,
            @Value("${yardi.http.poll.body.sendingApplication}") String sendingApplication,
            @Value("${yardi.http.poll.body.sendingFacility}") String sendingFacility,
            @Value("${yardi.http.poll.body.receivingApplication}") String receivingApplication,
            @Value("${yardi.http.poll.body.receivingFacility}") String receivingFacility
    ) {
        return new YardiHttpPollBodyGenerator(password,
                sendingApplication,
                sendingFacility,
                receivingApplication,
                receivingFacility,
                Clock.systemDefaultZone());
    }

    @Bean
    public YardiHttpPollResponseAnalyzer yardiHttpPollResponseAnalyzer() {
        return new YardiHttpPollResponseAnalyzer();
    }

    @Bean
    public HttpPollingHL7Gateway yardiHttpPollingHL7Gateway(
            @Value("${yardi.http.poll.enabled}") boolean pollingEnabled,
            @Qualifier("yardiHttpClient") HttpClient yardiHttpClient,
            @Value("${yardi.http.poll.endpoint.fetchMessage}") String fetchMessageEndpoint,
            @Qualifier("yardiHttpPollBodyGenerator") HttpPollBodyGenerator yardiHttpPollBodyGenerator,
            @Qualifier("yardiHttpPollResponseAnalyzer") HttpPollResponseAnalyzer yardiHttpPollResponseAnalyzer,
            InboundProcessingReportService inboundProcessingReportService,
            @Value("${yardi.localStorage.base}") String localStorageBaseDirPath,
            @Value("${yardi.statusFolder.ok}") String okDirName,
            @Value("${yardi.statusFolder.warn}") String warnDirName,
            @Value("${yardi.statusFolder.error}") String errorDirName,
            @Value("${yardi.reportfile.postfix}") String reportExtension,
            @Value("${yardi.http.poll.maxPollAtOnce}") int maxPollAtOnce,
            DocumentEncryptionService documentEncryptionService
    ) {
        return new HttpPollingHL7Gateway(
                pollingEnabled,
                HL7v2IntegrationPartner.YARDI,
                yardiHttpClient,
                fetchMessageEndpoint,
                yardiHttpPollBodyGenerator,
                yardiHttpPollResponseAnalyzer,
                inboundProcessingReportService,
                localStorageBaseDirPath,
                okDirName,
                warnDirName,
                errorDirName,
                reportExtension,
                maxPollAtOnce,
                documentEncryptionService
        );
    }

    @Bean
    public YardiPollFileDataToMessageStringConverter yardiPollFileDataToMessageStringConverter() {
        return new YardiPollFileDataToMessageStringConverter();
    }

    @Bean
    public HttpPollingInboundHL7MessageService yardiHttpPollingInboundHL7MessageService(
            @Qualifier("yardiHttpPollingHL7Gateway") HttpPollingHL7Gateway pollingHL7Gateway,
            HL7v2MessageFacade hl7v2MessageFacade,
            @Qualifier("yardiPollFileDataToMessageStringConverter") YardiPollFileDataToMessageStringConverter yardiPollDataToMessageStringConverter,
            DocumentEncryptionService documentEncryptionService
    ) {
        var context = HapiUtils.basicHapiContext();
        context.setValidationRuleBuilder(new CorrectingValidationBuilder() {
            @Override
            protected void addCorrections() {
                forAllVersions()
                        .primitive("DT", "DTM")
                        .test((PrimitiveTypeRule) prepareRule(new DashRemovingInDatePrimitiveTypeCorrector()));
            }
        });

        return new HttpPollingInboundHL7MessageService(
                pollingHL7Gateway,
                hl7v2MessageFacade,
                yardiPollDataToMessageStringConverter,
                HL7v2IntegrationPartner.YARDI,
                context.getPipeParser(),
                documentEncryptionService
        );
    }
}
