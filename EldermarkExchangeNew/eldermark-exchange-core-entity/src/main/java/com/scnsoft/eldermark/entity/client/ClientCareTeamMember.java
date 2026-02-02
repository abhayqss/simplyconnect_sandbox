package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamRelation;
import com.scnsoft.eldermark.entity.careteam.CareTeamRelationship;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ResidentCareTeamMember")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ClientCareTeamMember extends CareTeamMember {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "resident_id", nullable = false, updatable = false, insertable = false)
    private Long clientId;

    @Column(name = "emergency_contact")
    private Boolean emergencyContact;

    @Column(name = "created_by_resident_id")
    private Long createdByClientId;

    @JoinColumn(name = "care_team_relationship_id", referencedColumnName = "id")
    @ManyToOne
    private CareTeamRelationship careTeamRelationship;

    @ManyToMany
    @JoinTable(name = "CareTeamMember_AccessRight", joinColumns = @JoinColumn(name = "care_team_member_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "access_right_id", nullable = false))
    private Set<AccessRight> accessRights;

    @Column(name = "include_in_facesheet")
    private Boolean includeInFaceSheet;

    /**
     * The nature of the relationship between a patient and a CTM. Nullable
     * @see CareTeamRelation.Relation CareTeamRelation.Relation enum
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_team_relation_id")
    private CareTeamRelation careTeamRelation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "clientCareTeamMember")
    private List<ClientPrimaryContact> primaryContacts;

    @Column(name = "on_hold", nullable = false)
    private boolean onHold;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getIncludeInFaceSheet() {
        return includeInFaceSheet;
    }

    public void setIncludeInFaceSheet(Boolean includeInFaceSheet) {
        this.includeInFaceSheet = includeInFaceSheet;
    }

    public Boolean getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Long getCreatedByClientId() {
        return createdByClientId;
    }

    public void setCreatedByClientId(Long createdByClientId) {
        this.createdByClientId = createdByClientId;
    }

    public CareTeamRelationship getCareTeamRelationship() {
        return careTeamRelationship;
    }

    public void setCareTeamRelationship(CareTeamRelationship careTeamRelationship) {
        this.careTeamRelationship = careTeamRelationship;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CareTeamRelation getCareTeamRelation() {
        return careTeamRelation;
    }

    public void setCareTeamRelation(CareTeamRelation careTeamRelationId) {
        this.careTeamRelation = careTeamRelationId;
    }

    public Set<AccessRight> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(Set<AccessRight> accessRights) {
        this.accessRights = accessRights;
    }

    public List<ClientPrimaryContact> getPrimaryContacts() {
        return primaryContacts;
    }

    public void setPrimaryContacts(List<ClientPrimaryContact> primaryContacts) {
        this.primaryContacts = primaryContacts;
    }

    public boolean getOnHold() {
        return onHold;
    }

    public void setOnHold(boolean onHold) {
        this.onHold = onHold;
    }
}
