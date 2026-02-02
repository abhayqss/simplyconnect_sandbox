package com.scnsoft.eldermark.service.inbound.philips;

import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventFileProcessingSummary;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractSftpInboundFileGateway;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import com.scnsoft.eldermark.service.inbound.NoSftpAcknowledgeStrategy;
import com.scnsoft.eldermark.service.inbound.SftpInboundFileGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;

@Service("philipsSftpInboundFileGateway")
@ConditionalOnProperty(
        value = "philips.integration.enabled",
        havingValue = "true"
)
public class PhilipsSftpInboundFileGateway extends AbstractSftpInboundFileGateway<PhilipsEventFileProcessingSummary> {

    @Autowired
    public PhilipsSftpInboundFileGateway(
            @Qualifier("philipsSftpSessionManager") SessionManager sessionManager,
            InboundProcessingReportService inboundProcessingReportService,
            @Value("${philips.sftp.enabled}") Boolean isSftpEnabled,
            @Value("${philips.sftp.workingDirectory}") String sftpWorkingDirectory,
            @Value("${philips.sftp.localStorage.base}") String localStorageBaseDirPath,
            @Value("${philips.sftp.statusFolder.ok}") String okDirName,
            @Value("${philips.sftp.statusFolder.warn}") String warnDirName,
            @Value("${philips.sftp.statusFolder.error}") String errorDirName,
            @Value("${philips.sftp.reportfile.postfix}") String reportFilePostfix,
            DocumentEncryptionService documentEncryptionService
    ) {
        super(sessionManager,
                inboundProcessingReportService,
                new SftpInboundFileGatewayConfig(
                        isSftpEnabled,
                        sftpWorkingDirectory,
                        localStorageBaseDirPath,
                        okDirName,
                        warnDirName,
                        errorDirName,
                        reportFilePostfix,
                        entry -> !entry.getAttrs().isDir()
                                && entry.getFilename().startsWith("MedAdept_CareSage_Incidents-"),
                        new NoSftpAcknowledgeStrategy(),
                        Comparator.comparing(File::getName),
                        false),
                documentEncryptionService
        );
    }
}
