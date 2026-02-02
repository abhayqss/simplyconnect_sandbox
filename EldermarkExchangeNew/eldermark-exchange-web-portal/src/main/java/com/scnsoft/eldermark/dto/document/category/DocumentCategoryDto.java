package com.scnsoft.eldermark.dto.document.category;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentCategorySecurityFieldsAware;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory_;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class DocumentCategoryDto implements DocumentCategorySecurityFieldsAware {

    private Long id;

    @Size(max = 50)
    @NotNull
    @DefaultSort
    @EntitySort(DocumentCategory_.NAME)
    private String name;

    @NotNull
    @Pattern(regexp = "(?i)#[0-9A-F]{6}")
    private String color;

    private Boolean canEdit;

    private Boolean canDelete;

    @NotNull
    private Long organizationId;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
