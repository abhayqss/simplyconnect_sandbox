package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileProcessingSummary;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractSftpInboundFileGateway;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import com.scnsoft.eldermark.service.inbound.SftpAcknowledgeStrategy;
import com.scnsoft.eldermark.service.inbound.SftpInboundFileGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@ConditionalOnProperty(
        value = "healthPartners.integration.enabled",
        havingValue = "true"
)
public class HpSftpInboundFileGateway extends AbstractSftpInboundFileGateway<HpFileProcessingSummary<?>> {

    @Autowired
    public HpSftpInboundFileGateway(@Qualifier("healthPartnersSftpSessionManager") SessionManager sessionManager,
                                    InboundProcessingReportService inboundProcessingReportService,
                                    HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator,
                                    HpFileNameSupport hpFileNameSupport,
                                    @Value("${healthPartners.integration.sftp.enabled}") boolean isSftpEnabled,
                                    @Value("${healthPartners.sftp.workingDirectory}") String sftpWorkingDirectory,
                                    @Value("${healthPartners.sftp.localStorage.base}") String localStorageBaseDirPath,
                                    @Value("${healthPartners.sftp.statusFolder.ok}") String okDirName,
                                    @Value("${healthPartners.sftp.statusFolder.warn}") String warnDirName,
                                    @Value("${healthPartners.sftp.statusFolder.error}") String errorDirName,
                                    @Value("${healthPartners.sftp.reportfile.postfix}") String reportFilePostfix,
                                    @Value("${healthPartners.sftp.acknowledgeDirectory}") String acknowledgeDirectory,
                                    @Value("${healthPartners.sftp.canCreateFolders}") boolean canCreateSFTPFolders,
                                    DocumentEncryptionService documentEncryptionService
    ) {
        super(
                sessionManager,
                inboundProcessingReportService,
                new SftpInboundFileGatewayConfig(
                        isSftpEnabled,
                        sftpWorkingDirectory,
                        localStorageBaseDirPath,
                        okDirName,
                        warnDirName,
                        errorDirName,
                        reportFilePostfix,
                        e -> !e.getAttrs().isDir() &&
                                hpFileNameSupport.isHealthPartnersSftpInputFile(e.getFilename()),
                        new HpSftpAcknowledgeStrategy(
                                hpFileNameSupport,
                                hpAcknowledgeReportNameGenerator,
                                acknowledgeDirectory
                        ),
                        new HpFileSortComparator(),
                        canCreateSFTPFolders),
                documentEncryptionService
        );
    }

    private static class HpSftpAcknowledgeStrategy implements SftpAcknowledgeStrategy {
        private final HpFileNameSupport fileNameSupport;
        private final HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator;
        private final String acknowledgeDirectory;

        private HpSftpAcknowledgeStrategy(HpFileNameSupport fileNameSupport, HpAcknowledgeReportNameGenerator hpAcknowledgeReportNameGenerator, String acknowledgeDirectory) {
            this.fileNameSupport = fileNameSupport;
            this.hpAcknowledgeReportNameGenerator = hpAcknowledgeReportNameGenerator;
            this.acknowledgeDirectory = acknowledgeDirectory;
        }

        @Override
        public boolean shouldAcknowledge(File file) {
            return fileNameSupport.describe(file.getName())
                    .map(HpFileDescription::getSource)
                    .filter(HpFileSource.SFTP::equals)
                    .isPresent();
        }

        @Override
        public String getAcknowledgeDirectory(ProcessingSummary.ProcessingStatus processingStatus, String statusFolder) {
            return acknowledgeDirectory;
        }

        @Override
        public boolean shouldMoveOriginalFile() {
            return false;
        }

        @Override
        public String generateReportFileName(File file, ProcessingSummary.ProcessingStatus status) {
            var type = fileNameSupport.typeFromFileName(file.getName()).orElseThrow(
                    () -> new HpFileProcessingException("Unknown file type") //should never happen here
            );
            return hpAcknowledgeReportNameGenerator.generate(type, status);
        }
    }
}
