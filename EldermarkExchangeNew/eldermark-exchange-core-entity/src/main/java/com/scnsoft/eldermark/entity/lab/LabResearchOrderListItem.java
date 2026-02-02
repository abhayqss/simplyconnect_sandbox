package com.scnsoft.eldermark.entity.lab;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LabResearchOrderListItem {

    private Long id;
    private String createdPersonFirstName;
    private String createdPersonLastName;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientCommunityName;
    private Long clientAvatarId;
    private LabResearchOrderStatus status;
    private LabResearchOrderReason reason;
    private String requisitionNumber;
    private Instant createdDate;

    public LabResearchOrderListItem(Long id, String createdPersonFirstName, String createdPersonLastName, Long clientId, String clientFirstName, String clientLastName, String clientCommunityName, Long clientAvatarId, LabResearchOrderStatus status, LabResearchOrderReason reason, String requisitionNumber, Instant createdDate) {
        this.id = id;
        this.createdPersonFirstName = createdPersonFirstName;
        this.createdPersonLastName = createdPersonLastName;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientCommunityName = clientCommunityName;
        this.clientAvatarId = clientAvatarId;
        this.status = status;
        this.reason = reason;
        this.requisitionNumber = requisitionNumber;
        this.createdDate = createdDate;
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

    public String getClientCommunityName() {
        return clientCommunityName;
    }

    public void setClientCommunityName(String clientCommunityName) {
        this.clientCommunityName = clientCommunityName;
    }

    public Long getClientAvatarId() {
        return clientAvatarId;
    }

    public void setClientAvatarId(Long clientAvatarId) {
        this.clientAvatarId = clientAvatarId;
    }

    public LabResearchOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LabResearchOrderStatus status) {
        this.status = status;
    }

    public LabResearchOrderReason getReason() {
        return reason;
    }

    public void setReason(LabResearchOrderReason reason) {
        this.reason = reason;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedPersonFullName() {
        return Stream.of(createdPersonFirstName, createdPersonLastName).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public String getClientFullName() {
        return Stream.of(clientFirstName, clientLastName).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
