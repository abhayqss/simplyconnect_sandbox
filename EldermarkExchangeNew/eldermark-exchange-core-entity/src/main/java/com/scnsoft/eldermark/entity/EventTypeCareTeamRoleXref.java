package com.scnsoft.eldermark.entity;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.event.EventType;

@Entity
@Table(name = "EventType_CareTeamRole_Xref")
public class EventTypeCareTeamRoleXref implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected EventTypeCareTeamRoleXrefPK id;

    @Basic(optional = false)
    @Column(name = "responsibility", length = 50)
    @Enumerated(EnumType.STRING)
    private Responsibility responsibility;

    @ManyToOne
    @JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name="care_team_role_id", insertable = false, updatable = false)
    private CareTeamRole careTeamRole;

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }

    public EventTypeCareTeamRoleXrefPK getId() {
        return id;
    }

    public void setId(EventTypeCareTeamRoleXrefPK id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
    }
}
