package com.scnsoft.eldermark.service.medispan.dto;

import java.util.List;

public class MediSpanResponse<T> {

    private String customerTransactionId;
    private String webServiceTransactionId;
    private String processingSuccessful;
    private List<Object> validationMessages;
    private List<T> results;

    public String getCustomerTransactionId() {
        return customerTransactionId;
    }

    public void setCustomerTransactionId(String customerTransactionId) {
        this.customerTransactionId = customerTransactionId;
    }

    public String getWebServiceTransactionId() {
        return webServiceTransactionId;
    }

    public void setWebServiceTransactionId(String webServiceTransactionId) {
        this.webServiceTransactionId = webServiceTransactionId;
    }

    public String getProcessingSuccessful() {
        return processingSuccessful;
    }

    public void setProcessingSuccessful(String processingSuccessful) {
        this.processingSuccessful = processingSuccessful;
    }

    public List<Object> getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(List<Object> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
