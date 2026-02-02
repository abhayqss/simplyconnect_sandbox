package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.CreatedByIdAware;
import com.scnsoft.eldermark.beans.projection.HieConsentPolicyTypeAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAware;

public interface ClientSecurityAwareEntity extends IdAware, ClientSecurityFieldsAware, HieConsentPolicyTypeAware, CreatedByIdAware {
}
