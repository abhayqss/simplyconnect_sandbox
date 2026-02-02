package com.scnsoft.eldermark.beans.reports.model.assessment.housing;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class HousingAssessmentReport extends Report {

    private List<HousingAssessmentReportItem> items;

    public List<HousingAssessmentReportItem> getItems() {
        return items;
    }

    public void setItems(List<HousingAssessmentReportItem> items) {
        this.items = items;
    }
}
