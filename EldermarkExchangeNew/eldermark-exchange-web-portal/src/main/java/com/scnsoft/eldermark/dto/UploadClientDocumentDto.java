package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.document.SharingOption;

public class UploadClientDocumentDto extends BaseUploadDocumentDto implements ClientDocumentSecurityFieldsAware {

    private SharingOption sharingOption;
    @JsonIgnore
    private Long clientId;

    public SharingOption getSharingOption() {
        return sharingOption;
    }

    public void setSharingOption(SharingOption sharingOption) {
        this.sharingOption = sharingOption;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
