package com.scnsoft.eldermark.therap.bean.summary;

public abstract class ProcessingSummary {

    private ProcessingStatus status;

    private String message;

    private String stackTrace;

    public enum ProcessingStatus {
        OK,
        WARN,
        ERROR
    }
//
//    public static class RemoteView {}
//    public static class LocalView {}
//
//    protected Predicate<ProcessingSummary> hasOkStatus = new Predicate<ProcessingSummary>() {
//        @Override
//        public boolean apply(ProcessingSummary summary) {
//            return ProcessingStatus.OK.equals(summary.getStatus());
//        }
//    };
//
//    public void propagateStatusAndMessage() {
//        if (shouldSetOkStatus()) {
//            setStatus(ProcessingStatus.OK);
//        } else {
//            setStatus(ProcessingStatus.WARN);
//            setMessage(buildWarnMessage());
//        }
//    }
//
//    protected abstract boolean shouldSetOkStatus();
//    protected abstract String buildWarnMessage();

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
}
