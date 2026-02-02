package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;

public interface DocumentFolderSecurityFieldsAware extends CommunityIdAware {

    Long getParentId();

    static DocumentFolderSecurityFieldsAware of(Long communityId, Long parentId) {
        return new DocumentFolderSecurityFieldsAware() {

            @Override
            public Long getParentId() {
                return parentId;
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }
        };
    }

    static DocumentFolderSecurityFieldsAware of(Long communityId) {
        return of(communityId, null);
    }
}
