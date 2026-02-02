package com.scnsoft.eldermark.services.inbound;

import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

public abstract class AbstractInboundFilesProcessingService<T, P extends ProcessingSummary> implements InboundFilesProcessingService {

    @Override
    @Scheduled(fixedDelayString = "${inboundfiles.checkPeriod}")
    public void loadAndProcess() {
        final List<T> inboundFiles = loadFiles();
        for (T inboundFile : inboundFiles) {
            doProcess(inboundFile);
        }
    }

    private void doProcess(T inboundFile) {
        try {
            P summary = process(inboundFile);
            summary.setProcessedAt(new Date());
            switch (summary.getStatus()) {
                case OK:
                    afterProcessingStatusOk(inboundFile, summary);
                    break;
                case WARN:
                    afterProcessingStatusWarn(inboundFile, summary);
                    break;
                default:
                    throw new IllegalArgumentException("Error processing summary status without actual exception");
            }
        } catch (Exception e) {
            afterProcessingStatusError(inboundFile, e);
        }
    }

    protected void fillProcessingSummaryErrorFields(ProcessingSummary summary, Exception ex) {
        summary.setStatus(ProcessingSummary.ProcessingStatus.ERROR);
        summary.setMessage(ex.getMessage());
        summary.setStackTrace(ExceptionUtils.getStackTrace(ex));
    }

    protected abstract List<T> loadFiles();

    protected abstract P process(T remoteFile) throws Exception;

    protected abstract void afterProcessingStatusOk(T remoteFile, P processingSummary);

    protected abstract void afterProcessingStatusWarn(T remoteFile, P processingSummary);

    protected abstract void afterProcessingStatusError(T remoteFile, Exception exception);
}
