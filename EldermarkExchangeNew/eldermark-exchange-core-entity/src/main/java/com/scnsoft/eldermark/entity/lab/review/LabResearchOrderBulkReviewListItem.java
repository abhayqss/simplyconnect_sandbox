package com.scnsoft.eldermark.entity.lab.review;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LabResearchOrderBulkReviewListItem {

    private Long id;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private Instant orderDate;
    private List<LabResearchOrderDocument> labResearchOrderDocuments;

    public LabResearchOrderBulkReviewListItem(Long id, Long clientId, String clientFirstName, String clientLastName, Instant orderDate, List<LabResearchOrderDocument> labResearchOrderDocuments) {
        this.id = id;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.orderDate = orderDate;
        this.labResearchOrderDocuments = labResearchOrderDocuments;
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

    public List<LabResearchOrderDocument> getLabResearchOrderDocuments() {
        return labResearchOrderDocuments;
    }

    public void setLabResearchOrderDocuments(List<LabResearchOrderDocument> labResearchOrderDocuments) {
        this.labResearchOrderDocuments = labResearchOrderDocuments;
    }

    public String getClientFullName() {
        return Stream.of(clientFirstName, clientLastName).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
