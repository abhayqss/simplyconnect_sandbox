package com.scnsoft.eldermark.service.healthpartners;

public class HealthPartnersFileProcessedEvent {
    private final String fileName;
    private final Long hpFileLogId;

    public HealthPartnersFileProcessedEvent(String fileName, Long hpFileLogId) {
        this.fileName = fileName;
        this.hpFileLogId = hpFileLogId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getHpFileLogId() {
        return hpFileLogId;
    }

    @Override
    public String toString() {
        return "HealthPartnersFileProcessedEvent{" +
                "fileName='" + fileName + '\'' +
                ", hpFileLogId=" + hpFileLogId +
                '}';
    }
}
