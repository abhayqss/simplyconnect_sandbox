package com.scnsoft.eldermark.entity.inbound;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.time.Instant;
import java.util.function.Predicate;

public abstract class ProcessingSummary {

    private ProcessingStatus status;
    private String message;

    private Instant processedAt;

    @JsonView(ProcessingSummary.LocalView.class)
    private String stackTrace;

    @JsonIgnore
    private Exception processingException;

    public enum ProcessingStatus {
        OK,
        WARN,
        ERROR
    }

    public static class RemoteView {
    }

    public static class LocalView {
    }

    protected final Predicate<ProcessingSummary> hasOkStatus =
            summary -> ProcessingStatus.OK.equals(summary.getStatus());

    public void propagateStatusAndMessage() {
        if (shouldSetOkStatus()) {
            setStatus(ProcessingStatus.OK);
        } else {
            setStatus(ProcessingStatus.WARN);
            setMessage(buildWarnMessage());
        }
    }

    protected abstract boolean shouldSetOkStatus();

    protected abstract String buildWarnMessage();

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(Exception processingException) {
        this.processingException = processingException;
    }
}
