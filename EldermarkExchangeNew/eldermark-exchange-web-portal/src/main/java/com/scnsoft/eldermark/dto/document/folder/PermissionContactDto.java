package com.scnsoft.eldermark.dto.document.folder;

public class PermissionContactDto {

    private Long contactId;
    private String contactLogin;
    private String contactFullName;
    private Long contactSystemRoleId;
    private String contactSystemRoleTitle;

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
}
