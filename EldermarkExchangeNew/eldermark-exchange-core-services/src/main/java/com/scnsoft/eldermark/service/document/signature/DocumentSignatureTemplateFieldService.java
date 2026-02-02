package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;

import java.util.List;
import java.util.Map;

public interface DocumentSignatureTemplateFieldService {

    Map<String, Object> findDefaults(DocumentSignatureTemplateContext context);

    List<DocumentSignatureRequestSubmittedField> createScSubmittedFields(DocumentSignatureTemplateContext context);

    void addPdcFlowFieldsToRequest(
            DocumentSignatureTemplateContext context,
            DocumentSignatureRequest request
    );
}
