package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 11:06
 */
@JsonSerialize (include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolEmployeesDto {
    private List<ScanSolBaseEmployeeInfoDto> employees;

    public List<ScanSolBaseEmployeeInfoDto> getEmployees() {
        return employees;
    }

    public void setEmployees(List<ScanSolBaseEmployeeInfoDto> employees) {
        this.employees = employees;
    }
}
