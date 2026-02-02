package com.scnsoft.eldermark.shared.palatiumcare;

import com.scnsoft.eldermark.shared.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationDto;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import java.util.List;

public class NotifyEmployeeDto extends EmployeeDto {

    private List<OrganizationDto>  organizationCareTeamMemberList;

    private List<NotifyResidentDto> residentCareTeamMemberList;

    public List<OrganizationDto> getOrganizationCareTeamMemberList() {
        return organizationCareTeamMemberList;
    }

    public void setOrganizationCareTeamMemberList(List<OrganizationDto> organizationCareTeamMemberList) {
        this.organizationCareTeamMemberList = organizationCareTeamMemberList;
    }

    public List<NotifyResidentDto> getResidentCareTeamMemberList() {
        return residentCareTeamMemberList;
    }

    public void setResidentCareTeamMemberList(List<NotifyResidentDto> residentCareTeamMemberList) {
        this.residentCareTeamMemberList = residentCareTeamMemberList;
    }
}
