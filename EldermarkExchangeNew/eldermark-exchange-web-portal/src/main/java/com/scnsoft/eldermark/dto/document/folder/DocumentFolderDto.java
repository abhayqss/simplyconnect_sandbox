package com.scnsoft.eldermark.dto.document.folder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderSecurityFieldsAware;
import com.scnsoft.eldermark.validation.SpELAssert;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        applyIf = "#isTrue(isSecurityEnabled)",
                        value = "#isNotEmpty(permissions)",
                        message = "Permissions should not be empty if security is enabled",
                        helpers = {BooleanUtils.class, CollectionUtils.class}
                )
        }
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentFolderDto implements DocumentFolderSecurityFieldsAware {

    private Long id;

    @NotNull
    private String name;

    private Long parentId;

    @NotNull
    private Long communityId;

    private Boolean isSecurityEnabled;

    @Valid
    private List<@Valid DocumentFolderPermissionDto> permissions;

    @JsonIgnore
    private List<Long> categoryIds;

    private List<DocumentCategoryItemDto> categories;

    private DocumentFolderType type;

    private Boolean canEdit;

    private Boolean canDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Boolean getIsSecurityEnabled() {
        return isSecurityEnabled;
    }

    public void setIsSecurityEnabled(Boolean securityEnabled) {
        isSecurityEnabled = securityEnabled;
    }

    public List<DocumentFolderPermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DocumentFolderPermissionDto> permissions) {
        this.permissions = permissions;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<DocumentCategoryItemDto> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategoryItemDto> categories) {
        this.categories = categories;
    }

    public DocumentFolderType getType() {
        return type;
    }

    public void setType(DocumentFolderType type) {
        this.type = type;
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
