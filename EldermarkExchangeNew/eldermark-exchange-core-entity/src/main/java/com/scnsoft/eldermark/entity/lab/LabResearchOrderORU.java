package com.scnsoft.eldermark.entity.lab;

import com.scnsoft.eldermark.entity.xds.message.ORUR01;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "LabResearchOrderORU")
public class LabResearchOrderORU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_research_order_id")
    private LabResearchOrder labOrder;

    @Column(name = "oru_log_file_name")
    private String oruLogFileName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oru_id")
    private ORUR01 oru;

    @Column(name = "received_datetime")
    private Instant receivedDatetime;

    @Column(name = "success")
    private boolean success;

    @Column(name = "is_testing")
    private boolean isTesting;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "error_message")
    private String errorMessage;

    @Transient
    private Exception processingException;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabResearchOrder getLabOrder() {
        return labOrder;
    }

    public void setLabOrder(LabResearchOrder labOrder) {
        this.labOrder = labOrder;
    }

    public String getOruLogFileName() {
        return oruLogFileName;
    }

    public void setOruLogFileName(String oruLogFileName) {
        this.oruLogFileName = oruLogFileName;
    }

    public ORUR01 getOru() {
        return oru;
    }

    public void setOru(ORUR01 oru) {
        this.oru = oru;
    }

    public Instant getReceivedDatetime() {
        return receivedDatetime;
    }

    public void setReceivedDatetime(Instant receivedDatetime) {
        this.receivedDatetime = receivedDatetime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(Exception processingException) {
        this.processingException = processingException;
    }
}
