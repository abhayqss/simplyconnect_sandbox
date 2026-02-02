package com.scnsoft.eldermark.entity.signature;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name = "DocumentSignatureRequestPdcFlowCallbackLog")
public class DocumentSignatureRequestPdcFlowCallbackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "pdcflow_signature_id", nullable = false, columnDefinition = "decimal")
    private BigInteger pdcflowSignatureId;

    @Column(name = "pdcflow_completion_date")
    private Instant pdcflowCompletionDate;

    @Column(name = "pdcflow_error_code")
    private String pdcflowErrorCode;

    @Column(name = "pdcflow_error_message")
    private String pdcflowErrorMessage;

    @Column(name = "received_at")
    private Instant receivedAt;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "processing_err_msg")
    private String processingErrorMessage;

    //todo implement status checks during app statup and create log entries with isRecovery = true
    @Column(name = "is_recovery")
    private Boolean isRecovery;

    @Column(name = "err_loaded_from_api")
    private Boolean errLoadedFromApi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getPdcflowSignatureId() {
        return pdcflowSignatureId;
    }

    public void setPdcflowSignatureId(BigInteger pdcflowSignatureId) {
        this.pdcflowSignatureId = pdcflowSignatureId;
    }

    public Instant getPdcflowCompletionDate() {
        return pdcflowCompletionDate;
    }

    public void setPdcflowCompletionDate(Instant pdcflowCompletionDate) {
        this.pdcflowCompletionDate = pdcflowCompletionDate;
    }

    public String getPdcflowErrorCode() {
        return pdcflowErrorCode;
    }

    public void setPdcflowErrorCode(String pdcflowErrorCode) {
        this.pdcflowErrorCode = pdcflowErrorCode;
    }

    public String getPdcflowErrorMessage() {
        return pdcflowErrorMessage;
    }

    public void setPdcflowErrorMessage(String pdcflowErrorMessage) {
        this.pdcflowErrorMessage = pdcflowErrorMessage;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Boolean getSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    public String getProcessingErrorMessage() {
        return processingErrorMessage;
    }

    public void setProcessingErrorMessage(String processingErrorMessage) {
        this.processingErrorMessage = processingErrorMessage;
    }

    public Boolean getRecovery() {
        return isRecovery;
    }

    public void setRecovery(Boolean recovery) {
        isRecovery = recovery;
    }

    public Boolean getErrLoadedFromApi() {
        return errLoadedFromApi;
    }

    public void setErrLoadedFromApi(Boolean errLoadedFromApi) {
        this.errLoadedFromApi = errLoadedFromApi;
    }
}
