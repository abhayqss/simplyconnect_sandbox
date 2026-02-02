package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.careteam.CareTeamRole;

//@Entity
//todo will be moved to database as part of CCN-2777
public class EditableRoleMatrix {

    private CareTeamRole loggedInUserRole;
    private CareTeamRole editableRole;

    public EditableRoleMatrix() {
    }

    public EditableRoleMatrix(CareTeamRole loggedInUserRole, CareTeamRole editableRole) {
        this.loggedInUserRole = loggedInUserRole;
        this.editableRole = editableRole;
    }

    public CareTeamRole getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public void setLoggedInUserRole(CareTeamRole loggedInUserRole) {
        this.loggedInUserRole = loggedInUserRole;
    }

    public CareTeamRole getEditableRole() {
        return editableRole;
    }

    public void setEditableRole(CareTeamRole editableRole) {
        this.editableRole = editableRole;
    }
}
