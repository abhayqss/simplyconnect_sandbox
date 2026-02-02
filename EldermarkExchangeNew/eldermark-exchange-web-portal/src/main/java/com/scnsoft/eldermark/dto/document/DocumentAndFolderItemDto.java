package com.scnsoft.eldermark.dto.document;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder_;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;

import java.util.List;

public class DocumentAndFolderItemDto {

    private String id;

    private Long folderId;

    private Long templateId;

    @DefaultSort
    @EntitySort(CommunityDocumentAndFolder_.TITLE)
    private String title;

    private String description;

    private String author;

    @EntitySort(CommunityDocumentAndFolder_.LAST_MODIFIED_TIME)
    private Long lastModifiedDate;

    private String mimeType;

    @EntitySort(CommunityDocumentAndFolder_.SIZE)
    private Integer size;

    private DocumentAndFolderType type;

    private DocumentSignatureTemplateStatus statusName;

    private String statusTitle;

    private List<DocumentCategoryItemDto> categories;

    private boolean canView;

    private boolean canDelete;

    private boolean canEdit;

    private boolean canAssign;

    private boolean canCopy;

    private boolean isSecurityEnabled;

    private boolean isTemporarilyDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    public DocumentAndFolderType getType() {
        return type;
    }

    public void setType(DocumentAndFolderType type) {
        this.type = type;
    }

    public List<DocumentCategoryItemDto> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategoryItemDto> categories) {
        this.categories = categories;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanAssign() {
        return canAssign;
    }

    public void setCanAssign(boolean canAssign) {
        this.canAssign = canAssign;
    }

    public boolean getIsSecurityEnabled() {
        return isSecurityEnabled;
    }

    public void setIsSecurityEnabled(boolean securityEnabled) {
        isSecurityEnabled = securityEnabled;
    }

    public boolean getIsTemporarilyDeleted() {
        return isTemporarilyDeleted;
    }

    public void setIsTemporarilyDeleted(boolean isTemporarilyDeleted) {
        this.isTemporarilyDeleted = isTemporarilyDeleted;
    }

    public DocumentSignatureTemplateStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(final DocumentSignatureTemplateStatus statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(final String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public boolean getCanCopy() {
        return canCopy;
    }

    public void setCanCopy(boolean canCopy) {
        this.canCopy = canCopy;
    }
}
