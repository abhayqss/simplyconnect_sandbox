package com.scnsoft.eldermark.entity.inbound.healthpartners;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

import java.util.ArrayList;
import java.util.List;

public abstract class HpFileProcessingSummary<T extends HpRecordProcessingSummary> extends ProcessingSummary {

    private final HpFileType type;
    private String fileName;
    private int totalRecords;
    private int processedRecords;
    private List<T> recordProcessingSummaries = new ArrayList<>();

    @JsonView(ProcessingSummary.LocalView.class)
    private Long fileLogId;

    protected HpFileProcessingSummary(HpFileType type) {
        this.type = type;
    }

    public HpFileType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    public List<T> getRecordProcessingSummaries() {
        return recordProcessingSummaries;
    }

    public void setRecordProcessingSummaries(List<T> recordProcessingSummaries) {
        this.recordProcessingSummaries = recordProcessingSummaries;
    }

    public Long getFileLogId() {
        return fileLogId;
    }

    public void setFileLogId(Long fileLogId) {
        this.fileLogId = fileLogId;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return recordProcessingSummaries.stream().allMatch(hasOkStatus);
    }

    @Override
    protected String buildWarnMessage() {
        return "Some " + type.getDisplayName().toLowerCase() + " from file could not be processed";
    }

}
