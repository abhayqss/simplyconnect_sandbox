package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;

public interface PermissionFilterService {

    PermissionFilter createPermissionFilterForCurrentUser();

    PermissionFilter createPermissionFilterForUser(Long employeeId);

}
