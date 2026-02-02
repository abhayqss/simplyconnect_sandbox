package com.scnsoft.eldermark.openxds.api.dto;

public class XdsDocumentRegistrySyncItem {

    private Long documentId;
    private boolean isError;
    private String errorMessage;
    private String registryResponse;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRegistryResponse() {
        return registryResponse;
    }

    public void setRegistryResponse(String registryResponse) {
        this.registryResponse = registryResponse;
    }
}
