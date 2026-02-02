package com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api;

public class OverlayBoxApiDto {

    private short boxTypeId;
    private float startXPercent;
    private float startYPercent;
    private short documentPage;
    private BoxPropertiesApiDto boxProperties;

    public short getBoxTypeId() {
        return boxTypeId;
    }

    public void setBoxTypeId(short boxTypeId) {
        this.boxTypeId = boxTypeId;
    }

    public float getStartXPercent() {
        return startXPercent;
    }

    public void setStartXPercent(float startXPercent) {
        this.startXPercent = startXPercent;
    }

    public float getStartYPercent() {
        return startYPercent;
    }

    public void setStartYPercent(float startYPercent) {
        this.startYPercent = startYPercent;
    }

    public short getDocumentPage() {
        return documentPage;
    }

    public void setDocumentPage(short documentPage) {
        this.documentPage = documentPage;
    }

    public BoxPropertiesApiDto getBoxProperties() {
        return boxProperties;
    }

    public void setBoxProperties(BoxPropertiesApiDto boxProperties) {
        this.boxProperties = boxProperties;
    }
}
