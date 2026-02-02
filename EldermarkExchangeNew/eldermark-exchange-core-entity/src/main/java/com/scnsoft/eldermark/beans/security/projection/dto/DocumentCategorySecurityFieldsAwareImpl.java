package com.scnsoft.eldermark.beans.security.projection.dto;

public class DocumentCategorySecurityFieldsAwareImpl implements DocumentCategorySecurityFieldsAware {

    private Long organizationId;

    public DocumentCategorySecurityFieldsAwareImpl(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
