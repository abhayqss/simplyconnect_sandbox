package com.scnsoft.eldermark.dto.lab;

import java.util.List;

public class LabResearchOrderBulkReviewListItemDto {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long orderDate;
    private List<LabResearchResultDocumentDto> documents;

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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public List<LabResearchResultDocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<LabResearchResultDocumentDto> documents) {
        this.documents = documents;
    }
}
