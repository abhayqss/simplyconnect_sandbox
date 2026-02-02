package com.scnsoft.eldermark.dto.signature;

import java.util.List;

public class DocumentSignatureTemplatePreviewResponseDto {

    private byte[] data;

    private List<DocumentSignatureTemplateSignatureAreaDto> signatureAreas;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<DocumentSignatureTemplateSignatureAreaDto> getSignatureAreas() {
        return signatureAreas;
    }

    public void setSignatureAreas(List<DocumentSignatureTemplateSignatureAreaDto> signatureAreas) {
        this.signatureAreas = signatureAreas;
    }
}
