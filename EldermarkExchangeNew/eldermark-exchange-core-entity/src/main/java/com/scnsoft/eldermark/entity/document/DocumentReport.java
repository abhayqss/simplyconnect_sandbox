package com.scnsoft.eldermark.entity.document;

import java.io.ByteArrayInputStream;

import com.scnsoft.eldermark.entity.document.DocumentType;

public class DocumentReport {
    private String documentTitle;
    private DocumentType documentType;
    private ByteArrayInputStream inputStream; //todo change to byte[]
    private String mimeType;

    public String getDocumentTitle() {
        return documentTitle;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public ByteArrayInputStream getInputStream() {
        return inputStream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void setInputStream(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
