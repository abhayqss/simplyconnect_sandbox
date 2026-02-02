package com.scnsoft.eldermark.entity.healthpartner;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "HealthPartnersFileLog")
public class HealthPartnersFileLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "received_datetime", nullable = false)
    private Instant receivedAt;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;

    @Column(name = "error_msg", columnDefinition = "varchar(max)")
    private String errorMessage;

    @Column(name = "processed_datetime")
    private Instant processedAt;

    @Column(name = "file_type")
    private String fileType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant received) {
        this.receivedAt = received;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
