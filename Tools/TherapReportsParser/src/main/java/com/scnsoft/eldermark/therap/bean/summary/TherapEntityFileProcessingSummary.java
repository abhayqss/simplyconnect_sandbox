package com.scnsoft.eldermark.therap.bean.summary;

import java.util.ArrayList;
import java.util.List;

public abstract class TherapEntityFileProcessingSummary<T extends TherapEntityRecordProcessingSummary> extends ProcessingSummary {

    private String fileName;
    private int totalRecords;
    private int processedRecords;
    private List<T> recordsProcessingSummary = new ArrayList<>();

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

    public List<T> getRecordsProcessingSummary() {
        return recordsProcessingSummary;
    }

    public void setRecordsProcessingSummary(List<T> recordsProcessingSummary) {
        this.recordsProcessingSummary = recordsProcessingSummary;
    }

//    @Override
//    protected boolean shouldSetOkStatus() {
//        return FluentIterable.from(recordsProcessingSummary).allMatch(hasOkStatus) && totalRecords == processedRecords;
//    }
//
//    @Override
//    protected String buildWarnMessage() {
//        return "Some records from " + getEntityType() + " file could not be processed";
//    }
//
//    protected abstract String getEntityType();

}
