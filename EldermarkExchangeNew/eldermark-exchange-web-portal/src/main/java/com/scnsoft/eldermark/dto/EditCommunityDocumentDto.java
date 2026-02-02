package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;

import javax.validation.constraints.NotNull;

public class EditCommunityDocumentDto extends EditDocumentDto implements CommunityDocumentSecurityFieldsAware {

    private Long folderId;
    @NotNull
    private Long communityId;

    @Override
    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @Override
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }
}
