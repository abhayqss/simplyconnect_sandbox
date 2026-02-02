package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "ReferralRequestAssignedHistory")
public class ReferralRequestAssignedHistory {

    @EmbeddedId
    private ReferralRequestAssignedHistory.Id id;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @Embeddable
    public static class Id implements Serializable {
        @ManyToOne
        @JoinColumn(name = "referral_request_id", nullable = false)
        private ReferralRequest referralRequest;

        @ManyToOne
        @JoinColumn(name = "assigned_employee_id")
        private Employee assignedEmployee;

        @Column(name = "datetime_till", nullable = false)
        private Instant datetimeTill;

        public Id() {
        }

        public Id(ReferralRequest referralRequest, Employee assignedEmployee, Instant datetimeTill) {
            this.referralRequest = referralRequest;
            this.assignedEmployee = assignedEmployee;
            this.datetimeTill = datetimeTill;
        }

        public ReferralRequest getReferralRequest() {
            return referralRequest;
        }

        public void setReferralRequest(ReferralRequest referralRequest) {
            this.referralRequest = referralRequest;
        }

        public Employee getAssignedEmployee() {
            return assignedEmployee;
        }

        public void setAssignedEmployee(Employee assignedEmployee) {
            this.assignedEmployee = assignedEmployee;
        }

        public Instant getDatetimeTill() {
            return datetimeTill;
        }

        public void setDatetimeTill(Instant datetimeTill) {
            this.datetimeTill = datetimeTill;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(referralRequest, id.referralRequest) &&
                    Objects.equals(assignedEmployee, id.assignedEmployee) &&
                    Objects.equals(datetimeTill, id.datetimeTill);
        }

        @Override
        public int hashCode() {

            return Objects.hash(referralRequest, assignedEmployee, datetimeTill);
        }
    }
}
