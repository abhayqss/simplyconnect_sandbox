package com.scnsoft.eldermark.hl7v2.poll.sftp;

import ca.uhn.hl7v2.parser.PipeParser;
import com.scnsoft.eldermark.hl7v2.facade.HL7v2MessageFacade;
import com.scnsoft.eldermark.hl7v2.poll.MessagePollingUtils;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractInboundFilesProcessingService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

//todo refactor AbstractInboundFilesProcessingService approach. It doesn't seem to fit perfectly
public class InboundHL7FilesProcessingServiceMessages extends AbstractInboundFilesProcessingService<File, HL7FileProcessingSummary> {
    private static final Logger logger = LoggerFactory.getLogger(InboundHL7FilesProcessingServiceMessages.class);

    private final SftpInboundHL7FileGateway inboundFileGateway;
    private final HL7v2IntegrationPartner integrationPartner;
    private final HL7v2MessageFacade hl7v2MessageFacade;

    private final PipeParser pipeParser;
    private final DocumentEncryptionService documentEncryptionService;

    public InboundHL7FilesProcessingServiceMessages(SftpInboundHL7FileGateway fileGateway,
                                                    HL7v2IntegrationPartner integrationPartner,
                                                    HL7v2MessageFacade hl7v2MessageFacade,
                                                    PipeParser pipeParser,
                                                    DocumentEncryptionService documentEncryptionService
    ) {
        this.inboundFileGateway = fileGateway;
        this.integrationPartner = integrationPartner;
        this.hl7v2MessageFacade = hl7v2MessageFacade;
        this.pipeParser = pipeParser;
        this.documentEncryptionService = documentEncryptionService;
    }

    @Override
    protected List<File> loadFiles() {
        return inboundFileGateway.loadFiles();
    }

    @Override
    protected HL7FileProcessingSummary process(File localFile) throws Exception {
        logger.info("Processing {} for {}", localFile.getName(), integrationPartner);

        var decrypted  = documentEncryptionService.decrypt(Files.readAllBytes(localFile.toPath()));
        var messageString = new String(decrypted, integrationPartner.getFileContentCharset());
        var hl7Message = pipeParser.parse(messageString);

        var messageSource = MessagePollingUtils.createMessageSource(
                messageString,
                hl7Message,
                integrationPartner,
                inboundFileGateway);
        messageSource.setFileName(localFile.getName());

        var response = hl7v2MessageFacade.processMessage(hl7Message, messageSource);

        var summary = new HL7FileProcessingSummary();
        summary.setFileName(localFile.getName());
        MessagePollingUtils.fillSummary(summary, response);

        return summary;
    }

    @Override
    protected void afterProcessingStatusOk(File remoteFile, HL7FileProcessingSummary processingSummary) {
        logger.info("Successfully processed {}", remoteFile.getName());
        inboundFileGateway.afterProcessingStatusOk(remoteFile, processingSummary);
    }


    @Override
    protected void afterProcessingStatusWarn(File remoteFile, HL7FileProcessingSummary processingSummary) {
        logger.info("Processed {} with warnings ", remoteFile.getName());
        inboundFileGateway.afterProcessingStatusWarn(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(File remoteFile, Exception exception) {
        logger.info("Processed {} with error, exception is: {}", remoteFile.getName(), ExceptionUtils.getStackTrace(exception));
        final HL7FileProcessingSummary summary = new HL7FileProcessingSummary();
        summary.setFileName(remoteFile.getName());
        summary.setProcessedAt(Instant.now());
        fillProcessingSummaryErrorFields(summary, exception);
        inboundFileGateway.afterProcessingStatusError(remoteFile, summary);
    }
}
