package com.scnsoft.eldermark.hl7v2.facade;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.hl7v2.CcnRestClient;
import com.scnsoft.eldermark.hl7v2.dao.HL7MessageLogDao;
import com.scnsoft.eldermark.hl7v2.entity.HL7MessageLog;
import com.scnsoft.eldermark.hl7v2.processor.HL7v2MessageService;
import com.scnsoft.eldermark.hl7v2.processor.MessageProcessingResult;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
public class HL7v2MessageFacadeImpl implements HL7v2MessageFacade {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2MessageFacadeImpl.class);

    @Autowired
    private HL7v2MessageService hl7v2MessageService;

    @Autowired
    private HL7MessageLogDao hl7MessageLogDao;

    @Autowired
    private CcnRestClient ccnRestClient;

    private final TransactionTemplate requiresNewTransactionTemplate;

    @Autowired
    public HL7v2MessageFacadeImpl(PlatformTransactionManager transactionManager) {
        requiresNewTransactionTemplate = new TransactionTemplate(transactionManager);
        requiresNewTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public MessageAndLogProcessingResult processMessage(Message message, MessageSource messageSource) {
        logger.info("Processing HL7 message {}", messageSource);

        logger.info("Creating HL7MessageLog");
        var logId = createHL7Log(messageSource);
        logger.info("Created HL7MessageLog {}", logId);
        try {
            var result = hl7v2MessageService.processMessage(message, messageSource);

            logger.info("Saving message processing success for log id {}", logId);
            saveHL7LogSuccess(logId, result);
            logger.info("Saved message processing success for log id {}", logId);

            logger.info("Sending processing results to openxds-api asynchronously...");
            //todo using ccnRestClient just as openXds. Consider performing all the needed updates directly in this module?
            ccnRestClient.postAdt(result, logId);
            logger.info("Sent processing results to openxds-api");

            logger.info("Generating ACK...");
            var ack = message.generateACK();
            logger.info("Generated ACK: {}", ack.printStructure());

            return new MessageAndLogProcessingResult(message.generateACK(), logId, true);

        } catch (HL7Exception e) {
            logger.warn("Error during processing HL7 message", e);
            return new MessageAndLogProcessingResult(processErrorAndGenerateACK(logId, message, e), logId, false);
        } catch (Exception e) {
            logger.warn("Error during processing HL7 message", e);
            return new MessageAndLogProcessingResult(processErrorAndGenerateACK(logId, message, new HL7Exception(e)), logId, false);
        }
    }

    private Message processErrorAndGenerateACK(Long logId, Message message, HL7Exception e) {
        logger.info("Saving message processing fail for log id {}", logId);
        saveHL7LogFailure(logId, e);
        logger.info("Saved message processing fail for log id {}", logId);

        logger.info("Generating ACK...");
        var ack = generateErrorACK(message, e);
        try {
            var ackStructure = ack.printStructure();
            logger.info("Generated ACK: {}", ackStructure);
        } catch (HL7Exception ex) {
            logger.info("Failed to generate ACK structure", e);
        }
        return ack;
    }

    private Long createHL7Log(MessageSource messageSource) {
        return requiresNewTransactionTemplate.execute(tx -> {
            var log = new HL7MessageLog();
            log.setRawMessage(messageSource.getRawMessage());
            log.setErrorMessage("In progress yet");
            log.setOpenxdsApiErrorMessage("Not sent yet");
            log.setChannel(messageSource.getChannel().name());
            log.setSourceAddress(messageSource.getSourceAddress());
            log.setSourcePort(messageSource.getSourcePort());
            log.setReceivedDatetime(Instant.now());
            log.setResolvedIntegration(Optional.ofNullable(messageSource.getHl7v2IntegrationPartner()).map(Enum::name).orElse(null));
            log.setFileName(messageSource.getFileName());
            return hl7MessageLogDao.save(log).getId();
        });
    }

    private void saveHL7LogSuccess(Long logId, MessageProcessingResult result) {
        hl7MessageLogDao.saveProcessingSuccess(
                Instant.now(),
                result.getParsedAdtMessageId(),
                result.getClientId(),
                logId
        );
    }

    private void saveHL7LogFailure(Long logId, Exception exception) {
        hl7MessageLogDao.saveProcessingFail(ExceptionUtils.getStackTrace(exception), Instant.now(), logId);
    }

    private Message generateErrorACK(Message message, HL7Exception e) {
        try {
            return message.generateACK(AcknowledgmentCode.AE, e);
        } catch (HL7Exception | IOException ex) {
            return generateErrorACKManually(message, e);
        }
    }

    private Message generateErrorACKManually(Message message, HL7Exception e) {
        //todo implement
        return null;
    }
}
