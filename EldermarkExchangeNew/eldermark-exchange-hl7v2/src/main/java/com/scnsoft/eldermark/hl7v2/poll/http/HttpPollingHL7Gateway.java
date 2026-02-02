package com.scnsoft.eldermark.hl7v2.poll.http;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.hl7v2.poll.PollingHL7InboundFileGateway;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.hl7v2.source.MessageSourceChannel;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpPollingHL7Gateway implements PollingHL7InboundFileGateway<HL7HttpPollProcessingSummary> {
    private static final Logger logger = LoggerFactory.getLogger(HttpPollingHL7Gateway.class);

    private final boolean pollingEnabled;
    private final HL7v2IntegrationPartner integrationPartner;
    private final String fetchMessageEndpoint;
    private final HttpPollBodyGenerator httpPollBodyGenerator;
    private final HttpPollResponseAnalyzer httpPollResponseAnalyzer;
    private final HttpClient httpClient;
    private final InboundProcessingReportService inboundProcessingReportService;

    private final String reportExtension;
    protected final File localStorageBaseDir;
    protected final String localStorageBaseDirPath;
    protected final File localStorageOkDir;
    protected final File localStorageWarnDir;
    protected final File localStorageErrorDir;

    private final int maxPollAtOnce;
    private final DocumentEncryptionService documentEncryptionService;

    public HttpPollingHL7Gateway(
            boolean pollingEnabled,
            HL7v2IntegrationPartner integrationPartner, HttpClient httpClient,
            String fetchMessageEndpoint,
            HttpPollBodyGenerator httpPollBodyGenerator,
            HttpPollResponseAnalyzer httpPollResponseAnalyzer,
            InboundProcessingReportService inboundProcessingReportService,
            String localStorageBaseDirPath,
            String okDirName,
            String warnDirName,
            String errorDirName,
            String reportExtension,
            int maxPollAtOnce,
            DocumentEncryptionService documentEncryptionService) {
        this.pollingEnabled = pollingEnabled;
        this.integrationPartner = integrationPartner;
        this.httpClient = httpClient;
        this.fetchMessageEndpoint = fetchMessageEndpoint;
        this.httpPollBodyGenerator = httpPollBodyGenerator;
        this.httpPollResponseAnalyzer = httpPollResponseAnalyzer;
        this.inboundProcessingReportService = inboundProcessingReportService;
        this.reportExtension = reportExtension;

        this.localStorageBaseDir = new File(localStorageBaseDirPath);
        this.localStorageBaseDirPath = localStorageBaseDirPath;
        localStorageOkDir = initFolder(localStorageBaseDir, okDirName);
        localStorageWarnDir = initFolder(localStorageBaseDir, warnDirName);
        localStorageErrorDir = initFolder(localStorageBaseDir, errorDirName);

        this.maxPollAtOnce = maxPollAtOnce;
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
            saveRequestsToLocalStorage();
            return loadRequestsFromLocalStorage();
        } catch (IOException e) {
            throw new HttpPollingHl7GatewayException(e);
        }
    }

    private void saveRequestsToLocalStorage() {
        if (!pollingEnabled) {
            logger.info("Polling is not enabled for {}, won't poll", integrationPartner.name());
            return;
        }
        for (int i = 0; i < maxPollAtOnce; ++i) {
            var hasMoreMessages = true;
            var body = httpPollBodyGenerator.generateBody();

            logger.info("Fetching new HL7 message from {}", fetchMessageEndpoint);
            var fetchMessageHttpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body.getContent()))
                    .uri(URI.create(fetchMessageEndpoint))
                    .setHeader("User-Agent", "Simply Connect Software")
                    .setHeader("Content-Type", body.getContentType())
                    .build();
            try {
                var response = httpClient.send(fetchMessageHttpRequest, HttpResponse.BodyHandlers.ofString());

                if (httpPollResponseAnalyzer.isNoFurtherMessagesResponse(response)) {
                    hasMoreMessages = false;
                    logger.info("No further messages response from {}", fetchMessageEndpoint);
                } else {
                    logger.info("Received HL7 message from {}", fetchMessageEndpoint);
                    saveAndEncryptResponseToFile(body.getIdentifier(), response);
                }
            } catch (IOException | InterruptedException e) {
                logger.warn("Exception during http poll from {}: ", fetchMessageEndpoint, e);
                hasMoreMessages = false;
            }

            if (!hasMoreMessages) {
                break;
            }
        }
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    private List<File> loadRequestsFromLocalStorage() throws IOException {
        List<File> requestFiles;
        try (Stream<Path> paths = Files.list(localStorageBaseDir.toPath())) {
            requestFiles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());
        }
        return requestFiles;
    }

    private void saveAndEncryptResponseToFile(String identifier, HttpResponse<String> response) throws IOException {
        var filePath = Paths.get(localStorageBaseDirPath, integrationPartner.name() + "_" + identifier + ".pollMsg");
        var encrypted = documentEncryptionService.encrypt(response.body().getBytes(StandardCharsets.UTF_8));
        Files.copy(new ByteArrayInputStream(encrypted), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void afterProcessingStatusOk(File remoteFile, HL7HttpPollProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusWarn(File remoteFile, HL7HttpPollProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusError(File remoteFile, HL7HttpPollProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    private void afterProcessing(File remoteFile, HL7HttpPollProcessingSummary summary) {
        var folderName = resolveStatusFolderName(summary.getStatus());
        moveFileToFolder(remoteFile, folderName);
        saveLocalReport(folderName, summary);
    }

    private void moveFileToFolder(File remoteFile, String folderName) {
        try {
            Files.move(remoteFile.toPath(), Paths.get(folderName, remoteFile.getName()));
        } catch (IOException e) {
            logger.warn("Failed to move message file {} to {}", remoteFile.getName(), folderName, e);
        }
    }

    @Override
    public void fillMessageSource(MessageSource messageSource) {
        messageSource.setChannel(MessageSourceChannel.HTTP_POLL);
        messageSource.setSourceAddress(fetchMessageEndpoint);
    }

    private void saveLocalReport(String folderName, HL7HttpPollProcessingSummary summary) {
        try {
            var reportStr = inboundProcessingReportService.createLocalReport(summary);
            var encrypted = documentEncryptionService.encrypt(reportStr.getBytes(StandardCharsets.UTF_8));
            var reportFilePath = Paths.get(folderName, summary.getFileName() + reportExtension);
            writeToFile(reportFilePath, encrypted);
        } catch (Exception e) {
            logger.warn("Failed to write report to file", e);
        }
    }

    protected void writeToFile(Path filePath, byte[] content) throws IOException {
        Files.copy(new ByteArrayInputStream(content), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private String resolveStatusFolderName(ProcessingSummary.ProcessingStatus status) {
        switch (status) {
            case OK:
                return localStorageOkDir.toPath().toString();
            case WARN:
                return localStorageWarnDir.toPath().toString();
            case ERROR:
                return localStorageErrorDir.toPath().toString();
        }
        throw new HttpPollingHl7GatewayException("Unknown status " + status);
    }
}
