package com.scnsoft.eldermark.dto.document;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder_;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument_;

public class CommunityDocumentItemDto extends DocumentDto {

    private Long communityId;
    private Long folderId;
    @EntitySort(CommunityDocument_.LAST_MODIFIED_TIME)
    private Long lastModifiedDate;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
