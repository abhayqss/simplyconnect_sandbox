package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.signature.pdcflow.PdcFlowCallbackDto;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.document.signature.provider.DocumentSignatureProvider;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.PdcFlowCallbackService;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.PdcFlowJwtTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class PdcFlowCallbackFacadeImpl implements PdcFlowCallbackFacade {

    private static final Logger logger = LoggerFactory.getLogger(PdcFlowCallbackFacadeImpl.class);

    @Autowired
    private DocumentSignatureRequestService documentSignatureRequestService;

    @Autowired
    private DocumentSignatureProvider documentSignatureProvider;

    @Autowired
    private PdcFlowJwtTokenService pdcFlowJwtTokenService;

    @Autowired
    private PdcFlowCallbackService pdcFlowCallbackService;

    @Override
    public void processCallback(PdcFlowCallbackDto callbackDto, String auth) {
        logger.info("Processing PDCFlow callback {}", callbackDto);
        var logEntry = createLogEntry(callbackDto);

        DocumentSignatureRequest signature = null;
        try {
            var signatureOpt = documentSignatureRequestService.findByPdcflowSignatureId(callbackDto.getSignatureId());

            if (signatureOpt.isEmpty()) {
                logger.warn("Failed to resolve DocumentSignatureRequest by pdcflowSignatureId [{}]", callbackDto.getSignatureId());
                throw new RuntimeException("Failed to resolve DocumentSignatureRequest");
            }
            signature = signatureOpt.get();
            logger.info("Resolved DocumentSignatureRequest [{}] by pdcflowSignatureId [{}]", signature.getId(), callbackDto.getSignatureId());

            if (!pdcFlowJwtTokenService.validateToken(auth, signature.getId())) {
                logger.warn("Invalid auth token for DocumentSignatureRequest [{}]", signature.getId());
                throw new RuntimeException("Invalid auth token");
            }

            if (StringUtils.isEmpty(logEntry.getPdcflowErrorCode())) {
                //load errors from pdcflow because sometimes they are not sent in callbacks
                //(for example pin code failures when entered pin length doesn't match required pin length)
                var status = documentSignatureProvider.loadSignatureStatus(signature);

                //todo do we need to take into account statusDate?
                logEntry.setPdcflowErrorCode(status.getErrorCode());
                logEntry.setPdcflowErrorMessage(status.getErrorMessage());

                if (StringUtils.isNotEmpty(status.getErrorCode())) {
                    logger.info("Reloaded errorCode and errorMessage from API");
                    logEntry.setErrLoadedFromApi(true);
                }
            }
        } catch (Exception e) {
            logEntry.setProcessingErrorMessage(e.getMessage());
            logEntry.setSuccessful(false);
        }

        if (Boolean.FALSE.equals(logEntry.getSuccessful())) {
            pdcFlowCallbackService.saveInNewTransaction(logEntry);
            return;
        }

        try {
            documentSignatureRequestService.processStatusUpdateCallback(signature, logEntry);
            logEntry.setSuccessful(true);
        } catch (Exception e) {
            logger.warn("Failed to update signature [{}] status:", signature.getId(), e);
            logEntry.setSuccessful(false);
            logEntry.setProcessingErrorMessage(e.getMessage());
        }

        pdcFlowCallbackService.saveInNewTransaction(logEntry);

        logger.info("Processed PDCFlow callback");
    }

    private DocumentSignatureRequestPdcFlowCallbackLog createLogEntry(PdcFlowCallbackDto callbackDto) {
        var result = new DocumentSignatureRequestPdcFlowCallbackLog();
        result.setReceivedAt(Instant.now());
        result.setPdcflowSignatureId(callbackDto.getSignatureId());
        result.setPdcflowCompletionDate(
                Optional.ofNullable(callbackDto.getCompletionDate())
                        .map(completion -> completion.atZone(PdcFlowCallbackService.PDCFLOW_TIME_ZONE).toInstant())
                        .orElse(null)
        );
        result.setPdcflowErrorCode(callbackDto.getErrorCode());
        result.setPdcflowErrorMessage(callbackDto.getErrorMessage());

        return result;
    }
}
