package com.scnsoft.eldermark.dto;

public class OrganizationUniquenessDto {
    private Boolean oid;
    private Boolean name;
    private Boolean companyId;

    public OrganizationUniquenessDto(Boolean oid, Boolean name, Boolean companyId) {
        this.oid = oid;
        this.name = name;
        this.companyId = companyId;
    }

    public Boolean getOid() {
        return oid;
    }

    public void setOid(Boolean oid) {
        this.oid = oid;
    }

    public Boolean getName() {
        return name;
    }

    public void setName(Boolean name) {
        this.name = name;
    }

    public Boolean getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Boolean companyId) {
        this.companyId = companyId;
    }
}
