package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ClientHieConsentPolicyTypeAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.document.DocumentType;

import java.util.List;

public interface ClientDocumentSecurityAwareEntity extends ClientIdAware, ClientCommunityIdAware, ClientHieConsentPolicyTypeAware {
    DocumentType getDocumentType();

    boolean getEldermarkShared();

    List<Long> getSharedWithOrganizationIds();

    Long getLabResearchOrderId();

    Long getAuthorId();

    Boolean getIsCloud();
}
