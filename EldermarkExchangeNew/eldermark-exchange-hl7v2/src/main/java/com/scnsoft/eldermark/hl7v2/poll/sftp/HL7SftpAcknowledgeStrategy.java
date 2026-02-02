package com.scnsoft.eldermark.hl7v2.poll.sftp;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.service.inbound.SftpAcknowledgeStrategy;

import java.io.File;

public class HL7SftpAcknowledgeStrategy implements SftpAcknowledgeStrategy {

    private final boolean shouldAcknowledge;
    private final String acknowledgeDirectory;

    public HL7SftpAcknowledgeStrategy(boolean shouldAcknowledge, String acknowledgeDirectory) {
        this.shouldAcknowledge = shouldAcknowledge;
        this.acknowledgeDirectory = acknowledgeDirectory;
    }

    @Override
    public boolean shouldAcknowledge(File file) {
        return shouldAcknowledge;
    }

    @Override
    public String getAcknowledgeDirectory(ProcessingSummary.ProcessingStatus processingStatus, String statusFolder) {
        return acknowledgeDirectory;
    }

    @Override
    public boolean shouldMoveOriginalFile() {
        return false;
    }

    @Override
    public String generateReportFileName(File file, ProcessingSummary.ProcessingStatus status) {
        return "ack-" + file.getName();
    }
}
