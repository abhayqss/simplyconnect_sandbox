package com.scnsoft.eldermark.beans.reports.model.staffcaseload;

import com.scnsoft.eldermark.entity.EmployeeStatus;

import java.util.List;

public class StaffCareTeamsReportItem {

    private String employeeName;
    private EmployeeStatus employeeStatus;
    private Integer numberOfCareTeams;
    private List<ResidentStaffCareTeamItem> residents;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(EmployeeStatus employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public Integer getNumberOfCareTeams() {
        return numberOfCareTeams;
    }

    public void setNumberOfCareTeams(Integer numberOfCareTeams) {
        this.numberOfCareTeams = numberOfCareTeams;
    }

    public List<ResidentStaffCareTeamItem> getResidents() {
        return residents;
    }

    public void setResidents(List<ResidentStaffCareTeamItem> residents) {
        this.residents = residents;
    }
}
