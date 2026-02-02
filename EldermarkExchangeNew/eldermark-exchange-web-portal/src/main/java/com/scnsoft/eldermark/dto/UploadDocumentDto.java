package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.validation.SpELAssert;

import javax.validation.constraints.Size;

@SpELAssert(
    value = "(clientId != null || communityId != null) && !(clientId != null && communityId != null)",
    message = "The document must be linked to either the client or the community"
)
public class UploadDocumentDto extends BaseUploadDocumentDto implements ClientDocumentSecurityFieldsAware, CommunityDocumentSecurityFieldsAware {

    private Long clientId;

    private Long communityId;

    private Long folderId;

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

    @Override
    @Size(max = 256)
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @Size(max = 3950)
    public String getDescription() {
        return super.getDescription();
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }
}
