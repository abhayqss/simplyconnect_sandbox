package com.scnsoft.eldermark.hl7v2.config.integrations.prognocis;

import ca.uhn.hl7v2.HapiContext;
import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.hl7v2.facade.HL7v2MessageFacade;
import com.scnsoft.eldermark.hl7v2.poll.sftp.HL7SftpAcknowledgeStrategy;
import com.scnsoft.eldermark.hl7v2.poll.sftp.InboundHL7FilesProcessingServiceMessages;
import com.scnsoft.eldermark.hl7v2.poll.sftp.SftpInboundHL7FileGateway;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import com.scnsoft.eldermark.service.inbound.SftpInboundFileGatewayConfig;
import com.scnsoft.eldermark.service.sftp.SftpSessionManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Comparator;

@Configuration
@ConditionalOnProperty(value = "prognocis.enabled", havingValue = "true")
public class PrognocisConfig {

    @Bean
    public SessionManager prognocisHL7SessionManager(@Value("${prognocis.sftp.host}") String hostname,
                                                     @Value("${prognocis.sftp.port}") Integer port,
                                                     @Value("${prognocis.sftp.user}") String username,
                                                     @Value("${prognocis.sftp.password}") String password,
                                                     @Value("classpath:sftp/hl7_known_hosts") Resource knownHosts) throws IOException, JSchException {
        return SftpSessionManagerFactory.newSessionManager(
                hostname,
                port,
                username,
                password,
                knownHosts.getInputStream());
    }

    @Bean
    public SftpInboundHL7FileGateway prognocisSftpInboundHL7FileGateway(
            SessionManager prognocisHL7SessionManager,
            InboundProcessingReportService inboundProcessingReportService,
            @Value("${prognocis.integration.sftp.enabled}") boolean isSftpEnabled,
            @Value("${prognocis.sftp.workingDirectory}") String sftpWorkingDirectory,
            @Value("${prognocis.sftp.localStorage.base}") String localStorageBaseDirPath,
            @Value("${prognocis.sftp.statusFolder.ok}") String okDirName,
            @Value("${prognocis.sftp.statusFolder.warn}") String warnDirName,
            @Value("${prognocis.sftp.statusFolder.error}") String errorDirName,
            @Value("${prognocis.sftp.reportfile.postfix}") String reportFilePostfix,
            @Value("${prognocis.sftp.acknowledgeDirectory}") String acknowledgeDirectory,
            @Value("${prognocis.sftp.canCreateFolders}") boolean canCreateSFTPFolders,
            DocumentEncryptionService documentEncryptionService

    ) {
        return new SftpInboundHL7FileGateway(
                prognocisHL7SessionManager,
                inboundProcessingReportService,
                new SftpInboundFileGatewayConfig(
                        isSftpEnabled,
                        sftpWorkingDirectory,
                        localStorageBaseDirPath,
                        okDirName,
                        warnDirName,
                        errorDirName,
                        reportFilePostfix,
                        file -> file.getFilename().toLowerCase().endsWith(".hl7"),
                        new HL7SftpAcknowledgeStrategy(
                                true,
                                acknowledgeDirectory
                        ),
                        Comparator.naturalOrder(),
                        canCreateSFTPFolders
                ),
                documentEncryptionService
        );
    }

    @Bean
    public InboundHL7FilesProcessingServiceMessages prognocisInboundHL7FilesProcessingServiceMessages(
            @Qualifier("prognocisSftpInboundHL7FileGateway") SftpInboundHL7FileGateway fileGateway,
            HL7v2MessageFacade hl7v2MessageFacade,
            @Qualifier("hl7v2HapiContext") HapiContext hapiContext,
            DocumentEncryptionService documentEncryptionService
    ) {
        return new InboundHL7FilesProcessingServiceMessages(
                fileGateway,
                HL7v2IntegrationPartner.PROGNOCIS,
                hl7v2MessageFacade,
                hapiContext.getPipeParser(),
                documentEncryptionService
        );
    }
}
