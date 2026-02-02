package com.scnsoft.eldermark.hl7v2.poll.sftp;

import ca.uhn.hl7v2.HL7Exception;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.hl7v2.poll.PollingHL7InboundFileGateway;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.hl7v2.source.MessageSourceChannel;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractSftpInboundFileGateway;
import com.scnsoft.eldermark.service.inbound.InboundProcessingReportService;
import com.scnsoft.eldermark.service.inbound.SftpInboundFileGatewayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpInboundHL7FileGateway extends AbstractSftpInboundFileGateway<HL7FileProcessingSummary> implements PollingHL7InboundFileGateway<HL7FileProcessingSummary> {
    private static final Logger logger = LoggerFactory.getLogger(SftpInboundHL7FileGateway.class);

    public SftpInboundHL7FileGateway(SessionManager sessionManager,
                                     InboundProcessingReportService inboundProcessingReportService,
                                     SftpInboundFileGatewayConfig config,
                                     DocumentEncryptionService documentEncryptionService) {
        super(sessionManager, inboundProcessingReportService, config, documentEncryptionService);
    }

    @Override
    protected String createAcknowledgeContent(HL7FileProcessingSummary processingSummary) throws JsonProcessingException {
        try {
            return processingSummary.getResponseMessage().encode();
        } catch (HL7Exception e) {
            logger.error("Failed to encode response message", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fillMessageSource(MessageSource messageSource) {
        messageSource.setChannel(MessageSourceChannel.SFTP);
        messageSource.setSourceAddress(sessionManager.getSessionFactory().getHostname());
        messageSource.setSourcePort(sessionManager.getSessionFactory().getPort());
    }
}
