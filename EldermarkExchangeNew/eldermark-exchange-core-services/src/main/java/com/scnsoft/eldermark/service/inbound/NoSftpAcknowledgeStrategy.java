package com.scnsoft.eldermark.service.inbound;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

import java.io.File;

public class NoSftpAcknowledgeStrategy implements SftpAcknowledgeStrategy {
    @Override
    public boolean shouldAcknowledge(File file) {
        return false;
    }

    @Override
    public String getAcknowledgeDirectory(ProcessingSummary.ProcessingStatus processingStatus, String statusFolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldMoveOriginalFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateReportFileName(File file, ProcessingSummary.ProcessingStatus status) {
        throw new UnsupportedOperationException();
    }
}
