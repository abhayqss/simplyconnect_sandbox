package com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api;

import java.math.BigInteger;

public class DocumentApiDto {
    private BigInteger documentId;
    private String documentName;
    private byte[] documentBase64String;
    private BigInteger overlayId;


    public BigInteger getDocumentId() {
        return documentId;
    }

    public void setDocumentId(BigInteger documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public byte[] getDocumentBase64String() {
        return documentBase64String;
    }

    public void setDocumentBase64String(byte[] documentBase64String) {
        this.documentBase64String = documentBase64String;
    }

    public BigInteger getOverlayId() {
        return overlayId;
    }

    public void setOverlayId(BigInteger overlayId) {
        this.overlayId = overlayId;
    }
}
