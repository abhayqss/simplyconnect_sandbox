package com.scnsoft.eldermark.entity.inbound.therap.summary;

import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;

import java.util.ArrayList;
import java.util.List;

public abstract class TherapEntitiesProcessingSummary<T extends TherapEntityFileProcessingSummary> extends ProcessingSummary {

    private int foundFiles;
    private int processedFiles;
    private List<T> filesProcessingSummary = new ArrayList<>();

    public int getFoundFiles() {
        return foundFiles;
    }

    public void setFoundFiles(int foundFiles) {
        this.foundFiles = foundFiles;
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(int processedFiles) {
        this.processedFiles = processedFiles;
    }

    public List<T> getFilesProcessingSummary() {
        return filesProcessingSummary;
    }

    public void setFilesProcessingSummary(List<T> filesProcessingSummary) {
        this.filesProcessingSummary = filesProcessingSummary;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return FluentIterable.from(filesProcessingSummary).allMatch(hasOkStatus);
    }

    @Override
    protected String buildWarnMessage() {
        return "Some of " + getEntityType() + " files were processed with issues";
    }

    protected abstract String getEntityType();
}
