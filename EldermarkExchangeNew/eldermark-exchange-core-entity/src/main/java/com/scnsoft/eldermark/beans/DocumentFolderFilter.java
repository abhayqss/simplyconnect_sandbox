package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;

import java.util.List;
import java.util.Set;

public class DocumentFolderFilter {

    private List<Long> communityIds;
    private List<DocumentFolderType> types;
    private Set<DocumentFolderPermissionLevelCode> permissionLevels;

    private PermissionFilter permissionFilter;

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public List<DocumentFolderType> getTypes() {
        return types;
    }

    public void setTypes(List<DocumentFolderType> types) {
        this.types = types;
    }

    public Set<DocumentFolderPermissionLevelCode> getPermissionLevels() {
        return permissionLevels;
    }

    public void setPermissionLevels(Set<DocumentFolderPermissionLevelCode> permissionLevels) {
        this.permissionLevels = permissionLevels;
    }

    public PermissionFilter getPermissionFilter() {
        return permissionFilter;
    }

    public void setPermissionFilter(PermissionFilter permissionFilter) {
        this.permissionFilter = permissionFilter;
    }
}
