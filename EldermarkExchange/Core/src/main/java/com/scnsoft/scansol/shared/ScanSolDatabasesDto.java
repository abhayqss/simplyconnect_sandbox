package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 11:06
 */
@JsonSerialize (include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolDatabasesDto {
    private List<ScanSolDatabaseDto> companies;

    public List<ScanSolDatabaseDto> getCompanies () {
        return companies;
    }

    public void setCompanies (List<ScanSolDatabaseDto> companies) {
        this.companies = companies;
    }
}
