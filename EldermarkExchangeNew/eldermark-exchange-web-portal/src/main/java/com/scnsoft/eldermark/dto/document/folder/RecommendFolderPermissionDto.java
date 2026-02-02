package com.scnsoft.eldermark.dto.document.folder;

public class RecommendFolderPermissionDto {

    private Long contactId;
    private String contactFullName;
    private DocumentFolderPermissionLevelDto permissionLevel;
    private Boolean canEdit;
    private Boolean canDelete;

    public RecommendFolderPermissionDto(
        Long contactId,
        String contactFullName,
        DocumentFolderPermissionLevelDto permissionLevel,
        Boolean canEdit,
        Boolean canDelete
    ) {
        this.contactId = contactId;
        this.contactFullName = contactFullName;
        this.permissionLevel = permissionLevel;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public DocumentFolderPermissionLevelDto getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(DocumentFolderPermissionLevelDto permissionLevel) {
        this.permissionLevel = permissionLevel;
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
