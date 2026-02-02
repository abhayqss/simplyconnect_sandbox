package com.scnsoft.eldermark.mobile.dto.document;

import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;

public class DocumentListItemDto extends BaseDocumentDto {

    private DocumentType documentType;

    private DocumentSignatureStatus signatureStatus;

    private boolean canSign;

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentSignatureStatus getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(DocumentSignatureStatus signatureStatus) {
        this.signatureStatus = signatureStatus;
    }

    public boolean isCanSign() {
        return canSign;
    }

    public void setCanSign(boolean canSign) {
        this.canSign = canSign;
    }
}
