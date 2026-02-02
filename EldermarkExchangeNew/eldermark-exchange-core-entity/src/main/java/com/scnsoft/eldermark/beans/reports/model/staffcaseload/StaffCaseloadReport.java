package com.scnsoft.eldermark.beans.reports.model.staffcaseload;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class StaffCaseloadReport extends Report {

    private List<StaffCaseloadReportItem> staffCaseload;
    private List<StaffCareTeamsReportItem> staffCareTeams;

    public List<StaffCaseloadReportItem> getStaffCaseload() {
        return staffCaseload;
    }

    public void setStaffCaseload(List<StaffCaseloadReportItem> staffCaseload) {
        this.staffCaseload = staffCaseload;
    }

    public List<StaffCareTeamsReportItem> getStaffCareTeams() {
        return staffCareTeams;
    }

    public void setStaffCareTeams(List<StaffCareTeamsReportItem> staffCareTeams) {
        this.staffCareTeams = staffCareTeams;
    }
}
