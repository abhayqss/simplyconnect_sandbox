package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

import java.util.Set;

public interface ClientAppointmentSecurityFieldsAware extends ClientIdAware, IdAware {
    Long getCreatorId();
    Boolean getIsPublic();
    Set<Long> getServiceProviderIds();
}
