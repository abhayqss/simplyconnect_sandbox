package com.scnsoft.eldermark.entity.careteam.invitation;

import com.scnsoft.eldermark.entity.Employee;

public class InviteCareTeamMemberData extends BaseInviteCareTeamMemberData {

    private String email;
    private Employee createdByEmployee;
    private Long clientId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Employee getCreatedByEmployee() {
        return createdByEmployee;
    }

    public void setCreatedByEmployee(Employee createdByEmployee) {
        this.createdByEmployee = createdByEmployee;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
