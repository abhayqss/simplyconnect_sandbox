package com.scnsoft.eldermark.exchange.fk;

public class ContactsForeignKeys {
    private Long facilityOrganizationId;
    private Long residentId;
    private Long roleCodeId;
    private Long relationshipCodeId;

    public Long getFacilityOrganizationId() {
        return facilityOrganizationId;
    }

    public void setFacilityOrganizationId(Long facilityOrganizationId) {
        this.facilityOrganizationId = facilityOrganizationId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getRoleCodeId() {
        return roleCodeId;
    }

    public void setRoleCodeId(Long roleCodeId) {
        this.roleCodeId = roleCodeId;
    }

    public Long getRelationshipCodeId() {
        return relationshipCodeId;
    }

    public void setRelationshipCodeId(Long relationshipCodeId) {
        this.relationshipCodeId = relationshipCodeId;
    }
}
