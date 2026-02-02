package com.scnsoft.eldermark.hl7v2.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "HL7MessageLog")
public class HL7MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "raw_message", nullable = false, columnDefinition = "varchar(max)")
    private String rawMessage;

    @Column(name = "received_datetime", nullable = false)
    private Instant receivedDatetime;

    @Column(name = "channel", nullable = false)
    private String channel;

    @Column(name = "source_address")
    private String sourceAddress;

    @Column(name = "source_port")
    private Integer sourcePort;

    @Column(name = "resolved_integration")
    private String resolvedIntegration;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "processed_datetime")
    private Instant processedDatetime;

    @Column(name = "error_message", columnDefinition = "varchar(max)")
    private String errorMessage;

    @Column(name = "openxds_api_success", nullable = false)
    private boolean openxdsApiSuccess;

    @Column(name = "openxds_api_error_message", columnDefinition = "varchar(max)")
    private String openxdsApiErrorMessage;

    @Column(name = "affected_client1_id")
    private Long affectedClient1Id;

    @Column(name = "affected_client2_id")
    private Long affectedClient2Id;

    @Column(name = "adt_message_id")
    private Long adtMessageId;

    @Column(name = "file_name")
    private String fileName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Instant getReceivedDatetime() {
        return receivedDatetime;
    }

    public void setReceivedDatetime(Instant receivedDatetime) {
        this.receivedDatetime = receivedDatetime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getResolvedIntegration() {
        return resolvedIntegration;
    }

    public void setResolvedIntegration(String resolvedIntegration) {
        this.resolvedIntegration = resolvedIntegration;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Instant getProcessedDatetime() {
        return processedDatetime;
    }

    public void setProcessedDatetime(Instant processedDatetime) {
        this.processedDatetime = processedDatetime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isOpenxdsApiSuccess() {
        return openxdsApiSuccess;
    }

    public void setOpenxdsApiSuccess(boolean openxdsApiSuccess) {
        this.openxdsApiSuccess = openxdsApiSuccess;
    }

    public String getOpenxdsApiErrorMessage() {
        return openxdsApiErrorMessage;
    }

    public void setOpenxdsApiErrorMessage(String openxdsApiErrorMessage) {
        this.openxdsApiErrorMessage = openxdsApiErrorMessage;
    }

    public Long getAffectedClient1Id() {
        return affectedClient1Id;
    }

    public void setAffectedClient1Id(Long affectedClient1Id) {
        this.affectedClient1Id = affectedClient1Id;
    }

    public Long getAffectedClient2Id() {
        return affectedClient2Id;
    }

    public void setAffectedClient2Id(Long affectedClient2Id) {
        this.affectedClient2Id = affectedClient2Id;
    }

    public Long getAdtMessageId() {
        return adtMessageId;
    }

    public void setAdtMessageId(Long adtMessageId) {
        this.adtMessageId = adtMessageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
