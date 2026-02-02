package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.ClientHieConsentPolicyTypeAware;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

public interface EventSecurityAwareEntity extends IdAware, ClientIdAware, ClientHieConsentPolicyTypeAware, ClientCommunityIdAware {

    Long getEventTypeId();

}
