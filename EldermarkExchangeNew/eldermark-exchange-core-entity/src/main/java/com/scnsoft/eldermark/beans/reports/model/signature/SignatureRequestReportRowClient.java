package com.scnsoft.eldermark.beans.reports.model.signature;

import java.util.List;

public class SignatureRequestReportRowClient {
    private Long clientId;
    private String clientName;
    private List<SignatureRequestReportRowDocument> documentRows;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<SignatureRequestReportRowDocument> getDocumentRows() {
        return documentRows;
    }

    public void setDocumentRows(List<SignatureRequestReportRowDocument> documentRows) {
        this.documentRows = documentRows;
    }
}
