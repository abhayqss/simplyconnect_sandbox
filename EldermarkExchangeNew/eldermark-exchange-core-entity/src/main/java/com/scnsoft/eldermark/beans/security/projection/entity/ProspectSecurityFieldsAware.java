package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAndCommunityIdAware;

public interface ProspectSecurityFieldsAware extends OrganizationIdAndCommunityIdAware {

    static ProspectSecurityFieldsAware of(Long organizationId, Long communityId) {
        return new ProspectSecurityFieldsAware() {
            @Override
            public Long getCommunityId() {
                return communityId;
            }

            @Override
            public Long getOrganizationId() {
                return organizationId;
            }
        };
    }
}
