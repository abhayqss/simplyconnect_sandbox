package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestPdcFlowCallbackLogDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PdcFlowCallbackServiceImpl implements PdcFlowCallbackService {

    @Autowired
    private DocumentSignatureRequestPdcFlowCallbackLogDao documentSignatureRequestPdcFlowCallbackLogDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DocumentSignatureRequestPdcFlowCallbackLog saveInNewTransaction(DocumentSignatureRequestPdcFlowCallbackLog callbackLog) {
        return documentSignatureRequestPdcFlowCallbackLogDao.save(callbackLog);
    }
}
