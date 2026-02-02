package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 11:06
 */
@JsonSerialize (include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolOrganizationsDto {
    @JsonProperty("communities")
    private List<ScanSolOrganizationDto> organizations;

    public List<ScanSolOrganizationDto> getOrganizations () {
        return organizations;
    }

    public void setOrganizations(List<ScanSolOrganizationDto> organizations) {
        this.organizations = organizations;
    }
}
