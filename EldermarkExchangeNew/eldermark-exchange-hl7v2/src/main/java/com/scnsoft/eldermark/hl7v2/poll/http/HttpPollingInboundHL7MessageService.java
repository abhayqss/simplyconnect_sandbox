package com.scnsoft.eldermark.hl7v2.poll.http;

import ca.uhn.hl7v2.parser.PipeParser;
import com.scnsoft.eldermark.hl7v2.facade.HL7v2MessageFacade;
import com.scnsoft.eldermark.hl7v2.poll.MessagePollingUtils;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractInboundFilesProcessingService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

//todo refactor AbstractInboundFilesProcessingService approach. It doesn't seem to fit perfectly
public class HttpPollingInboundHL7MessageService
        extends AbstractInboundFilesProcessingService<File, HL7HttpPollProcessingSummary> {
    private static final Logger logger = LoggerFactory.getLogger(HttpPollingInboundHL7MessageService.class);

    private final HttpPollingHL7Gateway httpPollingHL7Gateway;

    private final HL7v2MessageFacade hl7v2MessageFacade;

    private final Converter<String, String> dataToHl7MessageConverter;

    private final HL7v2IntegrationPartner integrationPartner;

    private final PipeParser pipeParser;

    private final DocumentEncryptionService documentEncryptionService;

    public HttpPollingInboundHL7MessageService(
            HttpPollingHL7Gateway httpPollingHL7Gateway,
            HL7v2MessageFacade hl7v2MessageFacade,
            Converter<String, String> dataToHl7MessageConverter,
            HL7v2IntegrationPartner integrationPartner,
            PipeParser pipeParser,
            DocumentEncryptionService documentEncryptionService) {
        this.httpPollingHL7Gateway = httpPollingHL7Gateway;
        this.hl7v2MessageFacade = hl7v2MessageFacade;
        this.dataToHl7MessageConverter = dataToHl7MessageConverter;
        this.integrationPartner = integrationPartner;
        this.pipeParser = pipeParser;
        this.documentEncryptionService = documentEncryptionService;
    }

    @Override
    @Scheduled(fixedDelayString = "${http.poll.checkPeriod}")
    public void loadAndProcess() {
        super.loadAndProcess();
    }

    @Override
    protected List<File> loadFiles() {
        return httpPollingHL7Gateway.loadFiles();
    }

    @Override
    protected HL7HttpPollProcessingSummary process(File requestFile) throws Exception {
        logger.info("Processing http polled  {} for {}", requestFile.getName(), integrationPartner);

        var summary = new HL7HttpPollProcessingSummary();
        summary.setFileName(requestFile.getName());

        try {
            var decrypted  = documentEncryptionService.decrypt(Files.readAllBytes(requestFile.toPath()));
            var fileContent = new String(decrypted, StandardCharsets.UTF_8);

            var messageString = dataToHl7MessageConverter.convert(fileContent);
            var hl7Message = pipeParser.parse(messageString);

            var messageSource = MessagePollingUtils.createMessageSource(
                    messageString,
                    hl7Message,
                    integrationPartner,
                    httpPollingHL7Gateway);
            messageSource.setFileName(requestFile.getName());

            var response = hl7v2MessageFacade.processMessage(hl7Message, messageSource);

            MessagePollingUtils.fillSummary(summary, response);
        } catch (Exception e) {
            logger.warn("Processing error ", e);
            fillProcessingSummaryErrorFields(summary, e);
        }

        return summary;
    }

    @Override
    protected void afterProcessingStatusOk(File remoteFile, HL7HttpPollProcessingSummary processingSummary) {
        logger.info("Processed polled message with OK status for {}", integrationPartner);
        httpPollingHL7Gateway.afterProcessingStatusOk(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusWarn(File remoteFile, HL7HttpPollProcessingSummary processingSummary) {
        logger.info("Processed polled message with WARN status for {}", integrationPartner);
        httpPollingHL7Gateway.afterProcessingStatusWarn(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(File remoteFile, Exception exception) {
        logger.info("Processed polled message with ERROR status for {}", integrationPartner);
        var summary = new HL7HttpPollProcessingSummary();
        summary.setFileName(remoteFile.getName());
        summary.setReceivedDatetime(Instant.now());
        fillProcessingSummaryErrorFields(summary, exception);

        afterProcessingStatusErrorWithSummary(remoteFile, summary);
    }

    @Override
    protected void afterProcessingStatusErrorWithSummary(File remoteFile, HL7HttpPollProcessingSummary processingSummary) {
        logger.info("Processed polled message with ERROR status for {}", integrationPartner);
        httpPollingHL7Gateway.afterProcessingStatusError(remoteFile, processingSummary);
    }
}
