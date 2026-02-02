package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Client;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "NotViewableCareTeam")
public class NotViewableCareTeam {

    @EmbeddedId
    private NotViewableCareTeam.Id id;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Client employee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Client client;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getEmployee() {
        return employee;
    }

    public void setEmployee(Client employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
        private Long employeeId;

        @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
        private Long clientId;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(employeeId, id.employeeId) &&
                    Objects.equals(clientId, id.clientId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(employeeId, clientId);
        }
    }
}
