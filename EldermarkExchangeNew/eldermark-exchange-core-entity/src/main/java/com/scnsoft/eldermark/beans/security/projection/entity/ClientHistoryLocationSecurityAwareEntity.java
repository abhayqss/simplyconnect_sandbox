package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ClientHieConsentPolicyTypeAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.ClientOrganizationIdAware;

public interface ClientHistoryLocationSecurityAwareEntity extends ClientIdAware, ClientCommunityIdAware,
        ClientOrganizationIdAware, ClientHieConsentPolicyTypeAware {
    Long getClientCreatedById();
}
