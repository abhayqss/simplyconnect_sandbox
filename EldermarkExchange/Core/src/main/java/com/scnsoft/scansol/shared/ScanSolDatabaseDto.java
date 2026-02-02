package com.scnsoft.scansol.shared;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Date: 15.05.15
 * Time: 11:06
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolDatabaseDto {
    private Long id;
    private String name;
    private String companyCode;
    private Boolean isInitialSync;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

	public Boolean getIsInitialSync() {
		return isInitialSync;
	}

	public void setIsInitialSync(Boolean isInitialSync) {
		this.isInitialSync = isInitialSync;
	}
}
