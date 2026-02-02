package com.scnsoft.eldermark.service.inbound;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

import java.io.File;

public interface SftpAcknowledgeStrategy {

    boolean shouldAcknowledge(File file);

    String getAcknowledgeDirectory(ProcessingSummary.ProcessingStatus processingStatus, String statusFolder);

    boolean shouldMoveOriginalFile();

    String generateReportFileName(File file, ProcessingSummary.ProcessingStatus status);
}
