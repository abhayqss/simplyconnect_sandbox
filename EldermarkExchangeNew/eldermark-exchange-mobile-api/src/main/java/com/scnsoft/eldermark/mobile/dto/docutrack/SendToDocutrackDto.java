package com.scnsoft.eldermark.mobile.dto.docutrack;

public class SendToDocutrackDto {
    private String mediaSid;
    private String businessUnitCode;
    private String documentText;

    public String getMediaSid() {
        return mediaSid;
    }

    public void setMediaSid(String mediaSid) {
        this.mediaSid = mediaSid;
    }

    public String getBusinessUnitCode() {
        return businessUnitCode;
    }

    public void setBusinessUnitCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
    }

    public String getDocumentText() {
        return documentText;
    }

    public void setDocumentText(String documentText) {
        this.documentText = documentText;
    }
}
