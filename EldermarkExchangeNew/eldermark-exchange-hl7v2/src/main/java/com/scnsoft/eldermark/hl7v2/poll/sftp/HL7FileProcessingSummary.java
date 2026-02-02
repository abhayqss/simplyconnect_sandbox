package com.scnsoft.eldermark.hl7v2.poll.sftp;

import com.scnsoft.eldermark.hl7v2.poll.HL7ProcessingSummary;

public class HL7FileProcessingSummary extends HL7ProcessingSummary {

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
