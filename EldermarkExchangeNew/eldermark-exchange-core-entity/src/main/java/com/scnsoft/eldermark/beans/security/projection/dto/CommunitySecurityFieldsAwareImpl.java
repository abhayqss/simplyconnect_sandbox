package com.scnsoft.eldermark.beans.security.projection.dto;

public class CommunitySecurityFieldsAwareImpl implements CommunitySecurityFieldsAware {

    private final Long organizationId;

    public CommunitySecurityFieldsAwareImpl(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }
}
