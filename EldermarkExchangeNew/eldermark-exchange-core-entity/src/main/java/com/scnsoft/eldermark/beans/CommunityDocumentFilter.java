package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.beans.security.PermissionFilter;

import java.time.Instant;
import java.util.Set;

public class CommunityDocumentFilter {

    private String title;
    private Long communityId;
    private Long folderId;
    private String description;
    private Set<Long> categoryChainIds;
    private Instant fromDate;
    private Instant toDate;
    private boolean includeNotCategorized;
    private boolean includeDeleted;

    private PermissionFilter permissionFilter;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getCategoryChainIds() {
        return categoryChainIds;
    }

    public void setCategoryChainIds(Set<Long> categoryChainIds) {
        this.categoryChainIds = categoryChainIds;
    }

    public Instant getFromDate() {
        return fromDate;
    }

    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public Instant getToDate() {
        return toDate;
    }

    public void setToDate(Instant toDate) {
        this.toDate = toDate;
    }

    public boolean getIncludeNotCategorized() {
        return includeNotCategorized;
    }

    public void setIncludeNotCategorized(boolean includeNotCategorized) {
        this.includeNotCategorized = includeNotCategorized;
    }

    public boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public PermissionFilter getPermissionFilter() {
        return permissionFilter;
    }

    public void setPermissionFilter(PermissionFilter permissionFilter) {
        this.permissionFilter = permissionFilter;
    }
}
