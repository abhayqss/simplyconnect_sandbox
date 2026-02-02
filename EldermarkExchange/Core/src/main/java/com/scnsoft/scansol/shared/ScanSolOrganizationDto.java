package com.scnsoft.scansol.shared;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Date: 14.05.15
 * Time: 10:00
 */
@JsonSerialize (include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolOrganizationDto {
	private Long id;
    private String legacyId;
    private String name;
    private Boolean isArchived;
    private Boolean moduleHie;
    private List<ScanSolRoleDto> roles;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getModuleHie() {
        return moduleHie;
    }

    public void setModuleHie(Boolean moduleHie) {
        this.moduleHie = moduleHie;
    }
    
    public List<ScanSolRoleDto> getRoles() {
		return roles;
	}

	public void setRoles(List<ScanSolRoleDto> roles) {
		this.roles = roles;
	}

}
