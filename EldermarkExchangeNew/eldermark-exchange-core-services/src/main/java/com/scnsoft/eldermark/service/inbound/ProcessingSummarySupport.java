package com.scnsoft.eldermark.service.inbound;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ProcessingSummarySupport {
    private ProcessingSummarySupport() {
    }

    public static void fillProcessingSummaryErrorFields(ProcessingSummary summary, Exception ex) {
        summary.setStatus(ProcessingSummary.ProcessingStatus.ERROR);

        summary.setStackTrace(ExceptionUtils.getStackTrace(ex));
        summary.setProcessingException(ex);

        summary.setMessage(resolveMessage(ex));
    }

    private static String resolveMessage(Exception ex) {
        if (ex instanceof CsvDataTypeMismatchException && ex.getMessage() == null) {
            return ex.getCause().getMessage();
        }

        return ex.getMessage();
    }
}
