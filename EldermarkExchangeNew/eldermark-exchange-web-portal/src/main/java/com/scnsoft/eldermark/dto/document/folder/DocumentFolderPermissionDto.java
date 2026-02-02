package com.scnsoft.eldermark.dto.document.folder;

import javax.validation.constraints.NotNull;

public class DocumentFolderPermissionDto {

    private Long id;

    @NotNull
    private Long contactId;

    private String contactLogin;

    private String contactFullName;

    private Long contactSystemRoleId;

    private String contactSystemRoleTitle;

    @NotNull
    private Long permissionLevelId;

    private String permissionLevelTitle;

    private Boolean canEdit;

    private Boolean canDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactLogin() {
        return contactLogin;
    }

    public void setContactLogin(String contactLogin) {
        this.contactLogin = contactLogin;
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public Long getContactSystemRoleId() {
        return contactSystemRoleId;
    }

    public void setContactSystemRoleId(Long contactSystemRoleId) {
        this.contactSystemRoleId = contactSystemRoleId;
    }

    public String getContactSystemRoleTitle() {
        return contactSystemRoleTitle;
    }

    public void setContactSystemRoleTitle(String contactSystemRoleTitle) {
        this.contactSystemRoleTitle = contactSystemRoleTitle;
    }

    public Long getPermissionLevelId() {
        return permissionLevelId;
    }

    public void setPermissionLevelId(Long permissionLevelId) {
        this.permissionLevelId = permissionLevelId;
    }

    public String getPermissionLevelTitle() {
        return permissionLevelTitle;
    }

    public void setPermissionLevelTitle(String permissionLevelTitle) {
        this.permissionLevelTitle = permissionLevelTitle;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
}
