package com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api;

import java.math.BigInteger;
import java.util.List;

public class OverlayApiDto {

    private BigInteger overlayId;
    private BigInteger originalDocumentId;
    private String overlayName;
    private List<OverlayBoxApiDto> overlayBoxDefinitionList;

    public BigInteger getOverlayId() {
        return overlayId;
    }

    public void setOverlayId(BigInteger overlayId) {
        this.overlayId = overlayId;
    }

    public BigInteger getOriginalDocumentId() {
        return originalDocumentId;
    }

    public void setOriginalDocumentId(BigInteger originalDocumentId) {
        this.originalDocumentId = originalDocumentId;
    }

    public String getOverlayName() {
        return overlayName;
    }

    public void setOverlayName(String overlayName) {
        this.overlayName = overlayName;
    }

    public List<OverlayBoxApiDto> getOverlayBoxDefinitionList() {
        return overlayBoxDefinitionList;
    }

    public void setOverlayBoxDefinitionList(List<OverlayBoxApiDto> overlayBoxDefinitionList) {
        this.overlayBoxDefinitionList = overlayBoxDefinitionList;
    }
}
