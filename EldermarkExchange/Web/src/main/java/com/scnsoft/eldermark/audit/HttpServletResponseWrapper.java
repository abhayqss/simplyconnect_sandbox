package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.shared.DocumentType;

import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {
    private boolean isView;
    private long documentId;
    private DocumentType documentType;
    private long residentId;
    private boolean executedWithErrors;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void setResidentId(long residentId) {
        this.residentId = residentId;
    }

    public long getResidentId() {
        return residentId;
    }

    public boolean isExecutedWithErrors() {
        return executedWithErrors;
    }

    public void setExecutedWithErrors(boolean executedWithErrors) {
        this.executedWithErrors = executedWithErrors;
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }
}
