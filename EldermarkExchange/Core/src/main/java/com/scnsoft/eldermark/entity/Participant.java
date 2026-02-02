package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@AttributeOverride(name="legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 30))
@NamedQueries({
        @NamedQuery(name = "participant.listResponsibleParties", query = "select l from Participant l " +
                "where l.resident.id IN :residentIds and is_responsible_party is true"),
        @NamedQuery(name = "participant.listCcdParticipants", query = "select l from Participant l " +
                "where l.resident.id IN :residentIds and l.legacyTable = :legacyTable order by l.priority"),
        @NamedQuery(name = "participant.responsibleParties", query = "select l from Participant l" +
                " where l.resident.id = :residentId and is_responsible_party is true"),
        @NamedQuery(name = "participant.ccdParticipants", query = "select l from Participant l" +
                " where l.resident.id = :residentId and l.legacyTable = :legacyTable order by l.priority")
})
public class Participant extends LegacyTableAwareEntity implements ContactWithRelationship {
    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @ManyToOne
    @JoinColumn(name = "role_code_id")
    private CcdCode roleCode;

    @ManyToOne
    @JoinColumn(name = "relationship_code_id")
    private CcdCode relationship;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "is_responsible_party")
    private Boolean isResponsibleParty;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Resident resident;

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public CcdCode getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(CcdCode roleCode) {
        this.roleCode = roleCode;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public CcdCode getRelationship() {
        return relationship;
    }

    public void setRelationship(CcdCode relationship) {
        this.relationship = relationship;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getResponsibleParty() {
        return isResponsibleParty;
    }

    public void setResponsibleParty(Boolean responsibleParty) {
        isResponsibleParty = responsibleParty;
    }
}
