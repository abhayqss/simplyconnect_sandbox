package com.scnsoft.eldermark.entity.event;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Immutable
@Entity
@Table(name = "CTMViewableSettings")
public class CareTeamMemberViewableSettings {

    @EmbeddedId
    private CareTeamMemberViewableSettings.Id id;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "event_type_id", nullable = false, insertable = false, updatable = false)
    private Long eventTypeId;

    @Column(name = "can_view_event_type", columnDefinition = "int")
    private Boolean canViewEventType;

    @Column(name = "can_view_by_access_right", columnDefinition = "int")
    private Boolean canViewByAccessRight;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Boolean getCanViewEventType() {
        return canViewEventType;
    }

    public void setCanViewEventType(Boolean canViewEventType) {
        this.canViewEventType = canViewEventType;
    }

    public Boolean getCanViewByAccessRight() {
        return canViewByAccessRight;
    }

    public void setCanViewByAccessRight(Boolean canViewByAccessRight) {
        this.canViewByAccessRight = canViewByAccessRight;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
        private Long clientId;

        @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
        private Long employeeId;

        @Column(name = "event_type_id", nullable = false, insertable = false, updatable = false)
        private Long eventTypeId;

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getEventTypeId() {
            return eventTypeId;
        }

        public void setEventTypeId(Long eventTypeId) {
            this.eventTypeId = eventTypeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(clientId, id.clientId) &&
                    Objects.equals(employeeId, id.employeeId) &&
                    Objects.equals(eventTypeId, id.eventTypeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, employeeId, eventTypeId);
        }
    }
}
