package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.io.InputStream;
import java.util.List;

public interface DocumentSignaturePdfService {

    byte[] writeFieldsToPdf(List<DocumentSignatureRequestSubmittedField> coordinates, InputStream inputStream);

    List<Pair<Float, Float>> getPdfPageSizes(byte[] bytes);
}
