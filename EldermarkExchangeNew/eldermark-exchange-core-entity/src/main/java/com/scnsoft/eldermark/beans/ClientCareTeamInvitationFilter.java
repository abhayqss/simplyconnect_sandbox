package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;

import java.util.Set;

public class ClientCareTeamInvitationFilter {

    private Long clientId;
    private Set<ClientCareTeamInvitationStatus> statuses;
    private Long targetEmployeeId;


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Set<ClientCareTeamInvitationStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<ClientCareTeamInvitationStatus> statuses) {
        this.statuses = statuses;
    }

    public Long getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public void setTargetEmployeeId(Long targetEmployeeId) {
        this.targetEmployeeId = targetEmployeeId;
    }
}
