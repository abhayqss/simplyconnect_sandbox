package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface ClientLocationHistorySecurityService {

    boolean canAdd(ClientIdAware dto);

    boolean canViewList(Long clientId);

    boolean canView(Long locationId);

}
