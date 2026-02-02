package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.CommunityIdAware;

public interface CommunityDocumentSecurityFieldsAware extends CommunityIdAware {
    Long getFolderId();

    static CommunityDocumentSecurityFieldsAware of(Long communityId, Long folderId) {
        return new CommunityDocumentSecurityFieldsAware() {
            @Override
            public Long getFolderId() {
                return folderId;
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }
        };
    }

    static CommunityDocumentSecurityFieldsAware of(Long communityId) {
        return of(communityId, null);
    }
}
