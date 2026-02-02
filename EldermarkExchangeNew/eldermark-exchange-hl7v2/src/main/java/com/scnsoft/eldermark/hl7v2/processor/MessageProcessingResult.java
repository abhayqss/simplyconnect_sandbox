package com.scnsoft.eldermark.hl7v2.processor;

public class MessageProcessingResult {
    private String adtType;
    private Long clientId;
    private Long parsedAdtMessageId;
    private boolean isClientNew;

    public String getAdtType() {
        return adtType;
    }

    public void setAdtType(String adtType) {
        this.adtType = adtType;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getParsedAdtMessageId() {
        return parsedAdtMessageId;
    }

    public void setParsedAdtMessageId(Long parsedAdtMessageId) {
        this.parsedAdtMessageId = parsedAdtMessageId;
    }

    public boolean isClientNew() {
        return isClientNew;
    }

    public void setClientNew(boolean clientNew) {
        isClientNew = clientNew;
    }
}
