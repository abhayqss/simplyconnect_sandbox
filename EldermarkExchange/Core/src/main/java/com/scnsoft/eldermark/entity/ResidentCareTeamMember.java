package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.phr.AccessRight;

import javax.persistence.*;
import java.util.Set;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "ResidentCareTeamMember")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NamedQueries({
        @NamedQuery(name = "residentCareTeamMember.count", query = "select count(rctm.id) from ResidentCareTeamMember rctm " +
                "where rctm.resident.id = :residentId and rctm.employee.id in (:employeeIds)"),
        @NamedQuery(name = "residentCareTeamMember.delete", query = "delete from ResidentCareTeamMember where id in (:idsToDelete)"),
        @NamedQuery(name = "residentCareTeamMember.getIdsToDelete", query = "select rctm.id from ResidentCareTeamMember rctm " +
                "where rctm.resident.databaseId not in (select ao.primaryDatabaseId from AffiliatedOrganizations ao " +
                "where ao.primaryDatabaseId = rctm.resident.databaseId and ao.affiliatedDatabaseId = rctm.employee.databaseId) " +
                "and rctm.employee.databaseId <> rctm.resident.databaseId"),
        /* @NamedQuery(name = "residentCareTeamMember.getCareTeamMember",
                query = "select * from ResidentCareTeamMember rctm where rctm.employee.id = :employeeId and rctm.resident.id = :residentId") */
})
public class ResidentCareTeamMember extends CareTeamMember {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Resident resident;

    @Column(name = "resident_id", nullable = false, updatable = false, insertable = false)
    private Long residentId;

    @Column(name = "emergency_contact")
    private Boolean emergencyContact;

    @Column(name = "created_by_resident_id")
    private Long createdByResidentId;

    /**
     * The nature of the relationship between a patient and a CTM. Nullable
     * @see CareTeamRelation.Relation CareTeamRelation.Relation enum
     */
    @JoinColumn(name = "care_team_relation_id", referencedColumnName = "id")
    @ManyToOne
    private CareTeamRelation careTeamRelation;

    /**
     * Member role
     * @see CareTeamRelationship.Relationship CareTeamRelationship.Relationship enum
     */
    @JoinColumn(name = "care_team_relationship_id", referencedColumnName = "id")
    @ManyToOne
    private CareTeamRelationship careTeamRelationship;

    @ManyToMany
    @JoinTable(name = "CareTeamMember_AccessRight",
            joinColumns = @JoinColumn(name = "care_team_member_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "access_right_id", nullable = false))
    private Set<AccessRight> accessRights;
    
    @Column(name ="include_in_facesheet")
    private Boolean includeInFaceSheet;
    
    public Boolean getIncludeInFaceSheet() {
        return includeInFaceSheet;
    }

    public void setIncludeInFaceSheet(Boolean includeInFaceSheet) {
        this.includeInFaceSheet = includeInFaceSheet;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Boolean getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Long getCreatedByResidentId() {
        return createdByResidentId;
    }

    public void setCreatedByResidentId(Long createdByResidentId) {
        this.createdByResidentId = createdByResidentId;
    }

    public CareTeamRelation getCareTeamRelation() {
        return careTeamRelation;
    }

    public void setCareTeamRelation(CareTeamRelation careTeamRelation) {
        this.careTeamRelation = careTeamRelation;
    }

    public CareTeamRelationship getCareTeamRelationship() {
        return careTeamRelationship;
    }

    public void setCareTeamRelationship(CareTeamRelationship careTeamRelationship) {
        this.careTeamRelationship = careTeamRelationship;
    }

    public Set<AccessRight> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(Set<AccessRight> accessRights) {
        this.accessRights = accessRights;
    }
}
