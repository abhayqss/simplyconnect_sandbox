package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAware;
import com.scnsoft.eldermark.service.client.SecuredClientProperty;

import java.util.Collection;

public interface ClientSecurityService {

    Long ANY_TARGET_COMMUNITY = -1L;

    boolean canAdd(ClientSecurityFieldsAware dto);

    boolean canEdit(Long clientId);

    boolean canViewList();

    boolean canView(Long clientId);

    boolean canView(Long clientId, PermissionFilter permissionFilter);

    boolean canViewRecordSearchList();

    boolean canEditSsn(Long clientId);

    Collection<SecuredClientProperty> getAccessibleSecuredProperties();
}
