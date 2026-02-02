package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;
import java.util.Map;

public class ComprehensiveReport extends Report{

    private Map<String, List<ComprehensiveReportRecord>> comprehensiveReportRecords;

    public Map<String, List<ComprehensiveReportRecord>> getComprehensiveReportRecords() {
        return comprehensiveReportRecords;
    }

    public void setComprehensiveReportRecords(Map<String, List<ComprehensiveReportRecord>> comprehensiveReportRecords) {
        this.comprehensiveReportRecords = comprehensiveReportRecords;
    }
}
