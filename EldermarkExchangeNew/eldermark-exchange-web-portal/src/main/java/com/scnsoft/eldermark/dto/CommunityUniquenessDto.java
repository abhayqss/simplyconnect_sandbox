package com.scnsoft.eldermark.dto;

public class CommunityUniquenessDto {
    private Boolean oid;
    private Boolean name;

    public CommunityUniquenessDto(Boolean oid, Boolean name) {
        this.oid = oid;
        this.name = name;
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
}
