package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.EmployeeBasic_;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;

public class ContactListItemDto {

    private Long id;

    @DefaultSort
    @EntitySort.List(
            {
                    @EntitySort(EmployeeBasic_.FIRST_NAME),
                    @EntitySort(EmployeeBasic_.LAST_NAME)
            }
    )
    private String fullName;

    @EntitySort(joined = {EmployeeBasic_.CARE_TEAM_ROLE, CareTeamRole_.NAME})
    private String systemRoleTitle;

    private TypeDto status;

    @EntitySort(EmployeeBasic_.LOGIN_NAME)
    private String login;

    private String email;
    
    private String phone;

    @EntitySort(EmployeeBasic_.LAST_SESSION_DATE_TIME)
    private Long lastSessionDate;

    private ContactMembershipDto memberships;

    private boolean canEdit;

    private boolean canEditRole;

    private Long avatarId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSystemRoleTitle() {
        return systemRoleTitle;
    }

    public void setSystemRoleTitle(String systemRole) {
        this.systemRoleTitle = systemRole;
    }

    public TypeDto getStatus() {
        return status;
    }

    public void setStatus(TypeDto status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactMembershipDto getMemberships() {
        return memberships;
    }

    public void setMemberships(ContactMembershipDto memberships) {
        this.memberships = memberships;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanEditRole() {
        return canEditRole;
    }

    public void setCanEditRole(boolean canEditRole) {
        this.canEditRole = canEditRole;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Long getLastSessionDate() {
        return lastSessionDate;
    }

    public void setLastSessionDate(final Long lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }
}
