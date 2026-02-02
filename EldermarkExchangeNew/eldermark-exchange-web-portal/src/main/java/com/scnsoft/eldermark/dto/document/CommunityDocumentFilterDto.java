package com.scnsoft.eldermark.dto.document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CommunityDocumentFilterDto {

    @Size(max = 256)
    private String title;
    @NotNull
    private Long communityId;
    private Long folderId;
    @Size(max = 3950)
    private String description;
    private List<Long> categoryIds;
    private Long fromDate;
    private Long toDate;
    private boolean includeNotCategorized;
    private boolean includeDeleted;

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

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
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
}
