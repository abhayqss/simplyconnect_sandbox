package com.scnsoft.eldermark.shared.carecoordination.careteam;

import com.scnsoft.eldermark.shared.carecoordination.patients.NotificationPreferencesDto;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pzhurba on 22-Oct-15.
 */
@XmlRootElement
public class CareTeamMemberDto implements Serializable {
    private Long careTeamMemberId;
    private Long careTeamRoleSelect;
    private Long careTeamEmployeeSelect;
    private String careTeamDescription;
    private List<NotificationPreferencesDto> notificationPreferences;
    private boolean canChangeEmployee;
    private boolean canChangeRole;
    private Boolean includeInFaceSheet;
    
    public Boolean getIncludeInFaceSheet() {
        return includeInFaceSheet;
    }

    public void setIncludeInFaceSheet(Boolean includeInFaceSheet) {
        this.includeInFaceSheet = includeInFaceSheet;
    }

    public Long getCareTeamMemberId() {

        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public Long getCareTeamRoleSelect() {
        return careTeamRoleSelect;
    }

    public void setCareTeamRoleSelect(Long careTeamRoleSelect) {
        this.careTeamRoleSelect = careTeamRoleSelect;
    }

    public Long getCareTeamEmployeeSelect() {
        return careTeamEmployeeSelect;
    }

    public void setCareTeamEmployeeSelect(Long careTeamEmployeeSelect) {
        this.careTeamEmployeeSelect = careTeamEmployeeSelect;
    }

    public List<NotificationPreferencesDto> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(List<NotificationPreferencesDto> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }

    public String getCareTeamDescription() {
        return careTeamDescription;
    }

    public void setCareTeamDescription(String careTeamDescription) {
        this.careTeamDescription = careTeamDescription;
    }

    public boolean isCanChangeEmployee() {
        return canChangeEmployee;
    }

    public void setCanChangeEmployee(boolean canChangeEmployee) {
        this.canChangeEmployee = canChangeEmployee;
    }

    public boolean isCanChangeRole() {
        return canChangeRole;
    }

    public void setCanChangeRole(boolean canChangeRole) {
        this.canChangeRole = canChangeRole;
    }
}
