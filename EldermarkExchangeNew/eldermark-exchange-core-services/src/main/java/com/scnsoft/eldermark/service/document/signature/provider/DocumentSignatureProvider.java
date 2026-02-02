package com.scnsoft.eldermark.service.document.signature.provider;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureFieldPdcFlowTypeAware;
import com.scnsoft.eldermark.dto.singature.SignatureStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;

public interface DocumentSignatureProvider {

    void prepareSubmittedFieldFromTemplate(
            DocumentSignatureFieldPdcFlowTypeAware templateField,
            DocumentSignatureRequestSubmittedField submittedField
    );

    void sendSignatureRequest(DocumentSignatureRequest signatureRequest,
                              byte[] documentContent) throws DocumentSignatureProviderException;

    void cancelRequest(DocumentSignatureRequest signatureRequest);

    SignatureStatus loadSignatureStatus(DocumentSignatureRequest signatureRequest);

    byte[] getSignedDocument(DocumentSignatureRequest signatureRequest);
}
