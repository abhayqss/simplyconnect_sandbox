package com.scnsoft.eldermark.dto.document;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.ClientDocument_;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.data.domain.Sort;

import java.util.List;

public class BaseDocumentDto implements DocumentUtils.DocumentDtoAdjustableForIntegrations {

    private Long id;

    @EntitySort(ClientDocument_.DOCUMENT_TITLE)
    private String title;
    private String author;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(ClientDocument_.CREATION_TIME)
    private Long createdDate;
    private String mimeType;
    private Integer size;

    @EntitySort(ClientDocument_.DOCUMENT_TYPE)
    private String type;
    private String sharedWith;

    private String organizationTitle;
    private String organizationOid;
    private String communityTitle;
    private String communityOid;

    private List<DocumentCategoryItemDto> categories;
    private String description;

    private boolean canDelete;
    private boolean canEdit;

    private Boolean isTemporarilyDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrganizationTitle() {
        return organizationTitle;
    }

    public void setOrganizationTitle(String organizationTitle) {
        this.organizationTitle = organizationTitle;
    }

    public String getOrganizationOid() {
        return organizationOid;
    }

    public void setOrganizationOid(String organizationOid) {
        this.organizationOid = organizationOid;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }

    public String getCommunityOid() {
        return communityOid;
    }

    public void setCommunityOid(String communityOid) {
        this.communityOid = communityOid;
    }

    public String getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public List<DocumentCategoryItemDto> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategoryItemDto> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsTemporarilyDeleted() {
        return isTemporarilyDeleted;
    }

    public void setIsTemporarilyDeleted(Boolean isTemporarilyDeleted) {
        this.isTemporarilyDeleted = isTemporarilyDeleted;
    }
}
