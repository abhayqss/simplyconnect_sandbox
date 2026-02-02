package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "CareTeamMemberModified_ListReadByEmployeeStatus")
public class CareTeamMemberModifiedListReadByEmployeeStatus {

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "last_read_update_id", nullable = false)
    private Long lastReadUpdateId;


    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "employee_id", nullable = false)
        private Long employeeId;

        @Column(name = "resident_id", nullable = false)
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
            return Objects.equals(employeeId, id.employeeId) && Objects.equals(clientId, id.clientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, clientId);
        }
    }
}
