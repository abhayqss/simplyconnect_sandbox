package com.scnsoft.eldermark.service.inbound;


import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
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
            summary.setProcessedAt(Instant.now());
            switch (summary.getStatus()) {
                case OK:
                    afterProcessingStatusOk(inboundFile, summary);
                    break;
                case WARN:
                    afterProcessingStatusWarn(inboundFile, summary);
                    break;
                case ERROR:
                    if (summary.getProcessingException() == null) {
                        throw new IllegalArgumentException("Error processing summary status without actual exception");
                    }
                    afterProcessingStatusErrorWithSummary(inboundFile, summary);
            }
        } catch (Exception e) {
            afterProcessingStatusError(inboundFile, e);
        }
    }

    protected void fillProcessingSummaryErrorFields(ProcessingSummary summary, Exception ex) {
        ProcessingSummarySupport.fillProcessingSummaryErrorFields(summary, ex);
    }

    protected abstract List<T> loadFiles();

    protected abstract P process(T remoteFile) throws Exception;

    protected abstract void afterProcessingStatusOk(T remoteFile, P processingSummary);

    protected abstract void afterProcessingStatusWarn(T remoteFile, P processingSummary);

    protected void afterProcessingStatusErrorWithSummary(T remoteFile, P processingSummary) {
        afterProcessingStatusError(remoteFile, processingSummary.getProcessingException());
    }

    protected abstract void afterProcessingStatusError(T remoteFile, Exception exception);
}
