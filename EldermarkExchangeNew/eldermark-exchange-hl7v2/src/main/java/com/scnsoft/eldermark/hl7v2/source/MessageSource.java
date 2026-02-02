package com.scnsoft.eldermark.hl7v2.source;

import ca.uhn.hl7v2.model.v251.datatype.HD;

public class MessageSource {

    private String rawMessage;
    private String sourceAddress;
    private int sourcePort;
    private String messageControlId;
    private HD sendingApplication;
    private HD sendingFacility;
    private HD receivingApplication;
    private HD receivingFacility;
    private MessageSourceChannel channel;
    private HL7v2IntegrationPartner hl7v2IntegrationPartner;
    private String fileName;

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getMessageControlId() {
        return messageControlId;
    }

    public void setMessageControlId(String messageControlId) {
        this.messageControlId = messageControlId;
    }

    public HD getSendingApplication() {
        return sendingApplication;
    }

    public void setSendingApplication(HD sendingApplication) {
        this.sendingApplication = sendingApplication;
    }

    public HD getSendingFacility() {
        return sendingFacility;
    }

    public void setSendingFacility(HD sendingFacility) {
        this.sendingFacility = sendingFacility;
    }

    public HD getReceivingApplication() {
        return receivingApplication;
    }

    public void setReceivingApplication(HD receivingApplication) {
        this.receivingApplication = receivingApplication;
    }

    public HD getReceivingFacility() {
        return receivingFacility;
    }

    public void setReceivingFacility(HD receivingFacility) {
        this.receivingFacility = receivingFacility;
    }

    public MessageSourceChannel getChannel() {
        return channel;
    }

    public void setChannel(MessageSourceChannel channel) {
        this.channel = channel;
    }

    public HL7v2IntegrationPartner getHl7v2IntegrationPartner() {
        return hl7v2IntegrationPartner;
    }

    public void setHl7v2IntegrationPartner(HL7v2IntegrationPartner hl7v2IntegrationPartner) {
        this.hl7v2IntegrationPartner = hl7v2IntegrationPartner;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
