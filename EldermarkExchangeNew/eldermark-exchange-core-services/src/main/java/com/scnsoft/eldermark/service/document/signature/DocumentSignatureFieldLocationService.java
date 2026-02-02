package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.entity.signature.BaseDocumentSignatureField;
import com.scnsoft.eldermark.entity.signature.BaseDocumentSignatureFieldLocation;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureFieldUiLocation;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldLocation;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;

public interface DocumentSignatureFieldLocationService {
    void fillUiLocation(DocumentSignatureFieldUiLocation target, BaseDocumentSignatureFieldLocation source, Pair<Float, Float> pageSize);
    void fillFromUiLocation(DocumentSignatureTemplateFieldLocation target, DocumentSignatureFieldUiLocation source, Pair<Float, Float> pageSize);
    void fillUiLocation(DocumentSignatureFieldUiLocation target, BaseDocumentSignatureFieldLocation source, List<Pair<Float, Float>> pageSizes, int pageOffset);
}
