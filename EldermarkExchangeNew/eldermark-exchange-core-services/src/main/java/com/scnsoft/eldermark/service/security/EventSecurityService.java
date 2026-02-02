package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.EventSecurityFieldsAware;

public interface EventSecurityService {

    boolean canAdd(EventSecurityFieldsAware dto);

    boolean canAddToClient(Long clientId);

    boolean canEdit(Long eventId);

    boolean canViewList();

    boolean canView(Long eventId);

}
