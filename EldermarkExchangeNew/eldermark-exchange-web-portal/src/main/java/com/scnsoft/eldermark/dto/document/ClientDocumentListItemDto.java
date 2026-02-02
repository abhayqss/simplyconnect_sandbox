package com.scnsoft.eldermark.dto.document;

public class ClientDocumentListItemDto extends BaseDocumentDto {

    private DocumentSignatureDto signature;

    public DocumentSignatureDto getSignature() {
        return signature;
    }

    public void setSignature(DocumentSignatureDto signature) {
        this.signature = signature;
    }
}
