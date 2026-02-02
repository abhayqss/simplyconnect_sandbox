package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "CareTeamMemberModified_ReadByEmployeeStatus")
public class CareTeamMemberModifiedReadByEmployeeStatus {

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "care_team_member_id", nullable = false, insertable = false, updatable = false)
    private Long careTeamMemberId;

    @Column(name = "last_read_update_id", nullable = false)
    private Long lastReadUpdateId;


    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "employee_id", nullable = false)
        private Long employeeId;

        @Column(name = "care_team_member_id", nullable = false)
        private Long careTeamMemberId;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getCareTeamMemberId() {
            return careTeamMemberId;
        }

        public void setCareTeamMemberId(Long careTeamMemberId) {
            this.careTeamMemberId = careTeamMemberId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(employeeId, id.employeeId) && Objects.equals(careTeamMemberId, id.careTeamMemberId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, careTeamMemberId);
        }
    }
}
