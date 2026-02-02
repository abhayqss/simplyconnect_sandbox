package com.scnsoft.eldermark.beans.security.projection.dto;

public class ClientSecurityFieldsAwareImpl implements ClientSecurityFieldsAware {

    private final Long organizationId;
    private final Long communityId;

    public ClientSecurityFieldsAwareImpl(Long organizationId, Long communityId) {
        this.organizationId = organizationId;
        this.communityId = communityId;
    }


    @Override
    public Long getCommunityId() {
        return communityId;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }
}
