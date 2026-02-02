package com.scnsoft.eldermark.dto.conversation;

import java.util.Set;

public class CreateConversationDto {

    private Set<Long> employeeIds;
    private Long participatingClientId;

    private String friendlyName;

    private Long incidentReportId;

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Long getParticipatingClientId() {
        return participatingClientId;
    }

    public void setParticipatingClientId(Long participatingClientId) {
        this.participatingClientId = participatingClientId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public Long getIncidentReportId() {
        return incidentReportId;
    }

    public void setIncidentReportId(Long incidentReportId) {
        this.incidentReportId = incidentReportId;
    }
}
