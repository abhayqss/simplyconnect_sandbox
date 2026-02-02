package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AppointmentContactFilter {

    @NotNull
    private Long organizationId;

    private List<CareTeamRoleCode> roles;

    private List<EmployeeStatus> statuses;

    private Boolean withAppointmentsCreated;

    private Boolean withAppointmentsScheduled;

    private Long accessibleClientId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<CareTeamRoleCode> getRoles() {
        return roles;
    }

    public void setRoles(List<CareTeamRoleCode> roles) {
        this.roles = roles;
    }

    public List<EmployeeStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<EmployeeStatus> statuses) {
        this.statuses = statuses;
    }

    public Boolean getWithAppointmentsCreated() {
        return withAppointmentsCreated;
    }

    public void setWithAppointmentsCreated(Boolean withAppointmentsCreated) {
        this.withAppointmentsCreated = withAppointmentsCreated;
    }

    public Boolean getWithAppointmentsScheduled() {
        return withAppointmentsScheduled;
    }

    public void setWithAppointmentsScheduled(Boolean withAppointmentsScheduled) {
        this.withAppointmentsScheduled = withAppointmentsScheduled;
    }

    public Long getAccessibleClientId() {
        return accessibleClientId;
    }

    public void setAccessibleClientId(Long accessibleClientId) {
        this.accessibleClientId = accessibleClientId;
    }
}
