package com.scnsoft.eldermark.beans.reports.model;

import java.util.ArrayList;
import java.util.List;

public class AdlReport extends Report {
    private List<AdlReportRow> adlRows;
    private List<AdlReportRow> iadlRows;
    private List<AdlReportMedicalHistoryRow> medicalHistoryRows;

    public AdlReport() {
        this.adlRows = new ArrayList<>();
        this.iadlRows = new ArrayList<>();
        this.medicalHistoryRows = new ArrayList<>();
    }

    public List<AdlReportRow> getAdlRows() {
        return adlRows;
    }

    public void setAdlRows(List<AdlReportRow> adlRows) {
        this.adlRows = adlRows;
    }

    public List<AdlReportRow> getIadlRows() {
        return iadlRows;
    }

    public void setIadlRows(List<AdlReportRow> iadlRows) {
        this.iadlRows = iadlRows;
    }

    public List<AdlReportMedicalHistoryRow> getMedicalHistoryRows() {
        return medicalHistoryRows;
    }

    public void setMedicalHistoryRows(List<AdlReportMedicalHistoryRow> medicalHistoryRows) {
        this.medicalHistoryRows = medicalHistoryRows;
    }
}
