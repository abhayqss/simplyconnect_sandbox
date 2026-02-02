package com.scnsoft.eldermark.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventTypeCareTeamRoleXrefPK implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @Column(name = "event_type_id")

    private long eventTypeId;
    @Basic(optional = false)

    @Column(name = "care_team_role_id")
    private long careTeamRoleId;

    public EventTypeCareTeamRoleXrefPK() {
    }

    public EventTypeCareTeamRoleXrefPK(long eventTypeId, long careTeamRoleId) {
        this.eventTypeId = eventTypeId;
        this.careTeamRoleId = careTeamRoleId;
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public long getCareTeamRoleId() {
        return careTeamRoleId;
    }

    public void setCareTeamRoleId(long careTeamRoleId) {
        this.careTeamRoleId = careTeamRoleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventTypeCareTeamRoleXrefPK that = (EventTypeCareTeamRoleXrefPK) o;

        if (getEventTypeId() != that.getEventTypeId()) {
            return false;
        }
        return getCareTeamRoleId() == that.getCareTeamRoleId();
    }

    @Override
    public int hashCode() {
        int result = (int) (getEventTypeId() ^ (getEventTypeId() >>> 32));
        result = 31 * result + (int) (getCareTeamRoleId() ^ (getCareTeamRoleId() >>> 32));
        return result;
    }
}