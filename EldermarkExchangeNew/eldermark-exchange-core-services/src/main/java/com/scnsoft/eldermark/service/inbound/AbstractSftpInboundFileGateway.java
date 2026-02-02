package com.scnsoft.eldermark.service.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.sftp.SftpUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class AbstractSftpInboundFileGateway<P extends ProcessingSummary> implements InboundFileGateway<File, P> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSftpInboundFileGateway.class);

    private static final DateTimeFormatter FILE_NAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss").withZone(ZoneId.of("UTC"));

    protected final SessionManager sessionManager;
    protected final InboundProcessingReportService inboundProcessingReportService;

    protected final SftpInboundFileGatewayConfig config;

    protected final File localStorageOkDir;
    protected final File localStorageWarnDir;
    protected final File localStorageErrorDir;
    private final DocumentEncryptionService documentEncryptionService;


    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    protected AbstractSftpInboundFileGateway(SessionManager sessionManager,
                                             InboundProcessingReportService inboundProcessingReportService,
                                             SftpInboundFileGatewayConfig config,
                                             DocumentEncryptionService documentEncryptionService) {
        this.sessionManager = sessionManager;
        this.inboundProcessingReportService = inboundProcessingReportService;
        this.config = config;

        //init base dirs
        final File localStorageBaseDir = new File(config.getLocalStorageBaseDirPath());
        localStorageOkDir = initFolder(localStorageBaseDir, config.getOkDirName());
        localStorageWarnDir = initFolder(localStorageBaseDir, config.getWarnDirName());
        localStorageErrorDir = initFolder(localStorageBaseDir, config.getErrorDirName());
        this.documentEncryptionService = documentEncryptionService;
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    private File initFolder(File base, String name) {
        var result = new File(base, name);
        result.mkdirs();
        return result;
    }

    @Override
    public List<File> loadFiles() {
        try {
            moveFilesToLocalStorageFromSftp();
            return getFilesFromBaseDir();
        } catch (Exception e) {
            throw new InboundFileGatewayException(e);
        }
    }

    private void moveFilesToLocalStorageFromSftp() {
        if (Boolean.FALSE.equals(config.getSftpEnabled())) {
            return;
        }

        SftpUtils.doInSftp(sessionManager, channel -> {
            final String oldPath = channel.pwd();
            channel.cd(config.getSftpWorkingDirectory());

            final Vector fileList = channel.ls(".");
            for (Object aFileList : fileList) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) aFileList;
                if (config.getFileFilter().test(entry)) {
                    var output = new ByteArrayOutputStream();
                    channel.get(entry.getFilename(), output);
                    var encrypted = documentEncryptionService.encrypt(output.toByteArray());
                    Files.copy(new ByteArrayInputStream(encrypted), Paths.get(config.getLocalStorageBaseDirPath(), entry.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                    channel.rm(entry.getFilename());
                }
            }
            channel.cd(oldPath);
        });
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    private List<File> getFilesFromBaseDir() throws IOException {
        List<File> localFiles;
        try (Stream<Path> paths = Files.list(Paths.get(config.getLocalStorageBaseDirPath()))) {
            localFiles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .sorted(config.getFileSort())
                    .collect(Collectors.toList());
        }
        return localFiles;
    }

    @Override
    public void afterProcessingStatusOk(File remoteFile, P summary) {
        afterProcessing(remoteFile, summary, config.getOkDirName(), localStorageOkDir);
    }

    @Override
    public void afterProcessingStatusWarn(File remoteFile, P summary) {
        afterProcessing(remoteFile, summary, config.getWarnDirName(), localStorageWarnDir);
    }

    @Override
    public void afterProcessingStatusError(File remoteFile, P processingSummary) {
        var processedFileName = generateProcessedFileName(remoteFile);

        try {
            acknowledgeInSftp(remoteFile, processingSummary, processedFileName, config.getErrorDirName());
        } catch (Exception e) {
            logger.error("Error during acknowledge in {}, {}", this.getClass().getSimpleName(), remoteFile.getName(), e);
        }

        try {
            moveToFolderInLocalStorage(remoteFile, processingSummary, processedFileName, localStorageErrorDir);
        } catch (Exception e) {
            logger.error("Error during moving to local folder in {}, {}", this.getClass().getSimpleName(), remoteFile.getName(), e);
        }
    }

    protected void afterProcessing(File remoteFile, P processingSummary, String dirName, File localStorageFolder) {
        try {
            var processedFileName = generateProcessedFileName(remoteFile);
            moveToFolderInLocalStorage(remoteFile, processingSummary, processedFileName, localStorageFolder);
            acknowledgeInSftp(remoteFile, processingSummary, processedFileName, dirName);
        } catch (IOException | SftpException e) {
            throw new InboundFileGatewayException(e);
        }
    }

    protected void acknowledgeInSftp(File remoteFile, P processingSummary, String newFileName, String dirName) throws SftpException, JsonProcessingException {
        var strategy = config.getSftpAcknowledgeStrategy();
        if (strategy.shouldAcknowledge(remoteFile)) {
            if (Boolean.FALSE.equals(config.getSftpEnabled())) {
                logger.warn("Won't acknowledge in SFTP - sftp is disabled");
                return;
            }
            SftpUtils.doInSftp(sessionManager, channel -> {
                final String oldPath = channel.pwd();
                channel.cd(config.getSftpWorkingDirectory());

                var ackFolder = strategy.getAcknowledgeDirectory(processingSummary.getStatus(), dirName);
                if (config.isCanCreateSFTPFolders()) {
                    SftpUtils.createAndCdToFolders(channel, ackFolder);
                } else {
                    channel.cd(ackFolder);
                }

                if (strategy.shouldMoveOriginalFile()) {
                    try (var fis = new FileInputStream(remoteFile);
                         var encrypted = new ByteArrayInputStream(documentEncryptionService.decrypt(fis.readAllBytes()))
                    ) {
                        channel.put(encrypted, newFileName);
                    }
                }

                var reportFileName = strategy.generateReportFileName(remoteFile, processingSummary.getStatus());
                channel.put(
                        new ByteArrayInputStream(createAcknowledgeContent(processingSummary).getBytes()),
                        reportFileName
                );

                channel.cd(oldPath);
            });
        }
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "Only filename is used for path construction")
    protected void moveToFolderInLocalStorage(File remoteFile, P processingSummary, String processedFileName, File folder) throws IOException {
        Files.move(remoteFile.toPath(), new File(folder, processedFileName).toPath());
        writeLocalReportToFile(new File(folder, processedFileName + config.getReportFilePostfix()), processingSummary);
    }

    protected String generateProcessedFileName(File remoteFile) {
        return FILE_NAME_DATE_TIME_FORMATTER.format(Instant.now()) + "_" + remoteFile.getName();
    }

    protected void writeLocalReportToFile(File file, P processingSummary) throws IOException {
        var reportStr = createLocalReport(processingSummary);
        var encrypted = documentEncryptionService.encrypt(reportStr.getBytes(StandardCharsets.UTF_8));
        Files.copy(new ByteArrayInputStream(encrypted), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    protected String createLocalReport(P processingSummary) throws JsonProcessingException {
        return inboundProcessingReportService.createLocalReport(processingSummary);
    }

    protected String createAcknowledgeContent(P processingSummary) throws JsonProcessingException {
        return inboundProcessingReportService.createRemoteReport(processingSummary);
    }

}
