package com.scnsoft.eldermark.dto.singature;

import java.util.List;

public class DocumentTemplatePreview {
    private byte[] data;
    private List<DocumentSignatureFieldData> signatureAreas;
    private DocumentSignatureTemplateContext templateContext;

    public DocumentTemplatePreview(byte[] data, List<DocumentSignatureFieldData> signatureAreas, DocumentSignatureTemplateContext templateContext) {
        this.data = data;
        this.signatureAreas = signatureAreas;
        this.templateContext = templateContext;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<DocumentSignatureFieldData> getSignatureAreas() {
        return signatureAreas;
    }

    public void setSignatureAreas(List<DocumentSignatureFieldData> signatureAreas) {
        this.signatureAreas = signatureAreas;
    }

    public DocumentSignatureTemplateContext getTemplateContext() {
        return templateContext;
    }

    public void setTemplateContext(DocumentSignatureTemplateContext templateContext) {
        this.templateContext = templateContext;
    }
}
