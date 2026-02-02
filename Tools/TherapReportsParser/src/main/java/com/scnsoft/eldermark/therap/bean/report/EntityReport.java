package com.scnsoft.eldermark.therap.bean.report;

import com.scnsoft.eldermark.therap.bean.summary.ProcessingSummary;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EntityReport {

    private int totalFiles;
    private int totalRecords;
    private int processedRecords;
    private EnumMap<ProcessingSummary.ProcessingStatus, Map<String, Long>> statistics;

    public EntityReport() {
        init();
    }

    private void init() {
        statistics = new EnumMap<>(ProcessingSummary.ProcessingStatus.class);
        statistics.put(ProcessingSummary.ProcessingStatus.WARN, new HashMap<>());
        statistics.put(ProcessingSummary.ProcessingStatus.ERROR, new HashMap<>());
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
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

    public EnumMap<ProcessingSummary.ProcessingStatus, Map<String, Long>> getStatistics() {
        return statistics;
    }

    public void setStatistics(EnumMap<ProcessingSummary.ProcessingStatus, Map<String, Long>> statistics) {
        this.statistics = statistics;
    }
}
