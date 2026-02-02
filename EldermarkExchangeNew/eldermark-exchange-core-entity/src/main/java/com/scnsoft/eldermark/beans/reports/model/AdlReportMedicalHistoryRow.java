package com.scnsoft.eldermark.beans.reports.model;

import com.scnsoft.eldermark.beans.reports.enums.AdlReportField;

import java.util.SortedMap;

public class AdlReportMedicalHistoryRow extends AdlReportRow {

    private SortedMap<AdlReportField, String> chronicPainRows;

    public SortedMap<AdlReportField, String> getChronicPainRows() {
        return chronicPainRows;
    }

    public void setChronicPainRows(SortedMap<AdlReportField, String> chronicPainRows) {
        this.chronicPainRows = chronicPainRows;
    }
}
