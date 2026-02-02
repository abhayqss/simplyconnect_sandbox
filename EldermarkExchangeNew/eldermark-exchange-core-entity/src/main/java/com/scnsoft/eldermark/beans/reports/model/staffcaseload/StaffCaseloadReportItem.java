package com.scnsoft.eldermark.beans.reports.model.staffcaseload;

import java.util.List;

public class StaffCaseloadReportItem {

    private String employeeName;
    private Integer numberOfIndividuals;
    private List<ResidentStaffCaseLoadItem> residents;

    public StaffCaseloadReportItem(
        String employeeName,
        Integer numberOfIndividuals,
        List<ResidentStaffCaseLoadItem> residents
    ) {
        this.employeeName = employeeName;
        this.numberOfIndividuals = numberOfIndividuals;
        this.residents = residents;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Integer getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public void setNumberOfIndividuals(Integer numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
    }

    public List<ResidentStaffCaseLoadItem> getResidents() {
        return residents;
    }

    public void setResidents(List<ResidentStaffCaseLoadItem> residents) {
        this.residents = residents;
    }
}
