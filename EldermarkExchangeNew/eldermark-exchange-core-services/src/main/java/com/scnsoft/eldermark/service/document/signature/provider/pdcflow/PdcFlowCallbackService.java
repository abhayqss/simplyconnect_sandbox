package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;

import java.time.ZoneId;

public interface PdcFlowCallbackService {

    //todo verify with PDCFlow
    ZoneId PDCFLOW_TIME_ZONE = ZoneId.of("America/Denver");

    DocumentSignatureRequestPdcFlowCallbackLog saveInNewTransaction(DocumentSignatureRequestPdcFlowCallbackLog callbackLog);
}
