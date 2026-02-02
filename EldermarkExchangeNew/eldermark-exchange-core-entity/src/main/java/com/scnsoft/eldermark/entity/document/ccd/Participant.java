package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@AttributeOverride(name="legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 30))
@NamedQueries({
        @NamedQuery(name = "participant.listResponsibleParties", query = "select l from Participant l " +
                "where l.client.id IN :residentIds and is_responsible_party is true"),
        @NamedQuery(name = "participant.listCcdParticipants", query = "select l from Participant l " +
                "where l.client.id IN :residentIds and l.legacyTable = :legacyTable order by l.priority"),
        @NamedQuery(name = "participant.responsibleParties", query = "select l from Participant l" +
                " where l.client.id = :residentId and is_responsible_party is true"),
        @NamedQuery(name = "participant.ccdParticipants", query = "select l from Participant l" +
                " where l.client.id = :residentId and l.legacyTable = :legacyTable order by l.priority")
})
public class Participant extends LegacyTableAwareEntity implements ContactWithRelationship {
    private static final long serialVersionUID = 1L;

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
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
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
