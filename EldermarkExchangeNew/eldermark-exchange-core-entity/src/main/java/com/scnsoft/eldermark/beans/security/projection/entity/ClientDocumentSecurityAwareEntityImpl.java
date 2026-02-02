package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.util.List;

public class ClientDocumentSecurityAwareEntityImpl implements ClientDocumentSecurityAwareEntity {

    private final ClientDocument clientDocument;

    public ClientDocumentSecurityAwareEntityImpl(ClientDocument clientDocument) {
        this.clientDocument = clientDocument;
    }

    @Override
    public DocumentType getDocumentType() {
        return clientDocument.getDocumentType();
    }

    @Override
    public boolean getEldermarkShared() {
        return clientDocument.getEldermarkShared();
    }

    @Override
    public List<Long> getSharedWithOrganizationIds() {
        return clientDocument.getSharedWithOrganizationIds();
    }

    @Override
    public Long getLabResearchOrderId() {
        return clientDocument.getLabResearchOrderId();
    }

    @Override
    public Long getAuthorId() {
        return clientDocument.getAuthorId();
    }

    @Override
    public Long getClientCommunityId() {
        return clientDocument.getClient() == null ? null : clientDocument.getClient().getCommunityId();
    }

    @Override
    public Long getClientId() {
        return clientDocument.getClientId();
    }

    @Override
    public Boolean getIsCloud() {
        return clientDocument.getIsCloud();
    }

    @Override
    public HieConsentPolicyType getClientHieConsentPolicyType() {
        return clientDocument.getClient() == null ? null : clientDocument.getClient().getHieConsentPolicyType();
    }
}
