package com.scnsoft.eldermark.entity.lab.review;

import java.time.Instant;

public class LabResearchOrderDocumentWithOrderListItem {
    private Long id;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private Instant orderDate;
    private Long documentId;
    private String documentTitle;
    private String documentOriginalFileName;
    private String mimeType;

    public LabResearchOrderDocumentWithOrderListItem(Long id, Long clientId, String clientFirstName, String clientLastName, Instant orderDate, Long documentId, String documentTitle, String documentOriginalFileName, String mimeType) {
        this.id = id;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.orderDate = orderDate;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.documentOriginalFileName = documentOriginalFileName;
        this.mimeType = mimeType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentOriginalFileName() {
        return documentOriginalFileName;
    }

    public void setDocumentOriginalFileName(String documentOriginalFileName) {
        this.documentOriginalFileName = documentOriginalFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
