package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;

public class FolderPermission {

    private Long employeeId;
    private DocumentFolderPermissionLevel permissionLevel;

    public FolderPermission() {
    }

    public FolderPermission(Long employeeId, DocumentFolderPermissionLevel permissionLevel) {
        this.employeeId = employeeId;
        this.permissionLevel = permissionLevel;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public DocumentFolderPermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(DocumentFolderPermissionLevel permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
