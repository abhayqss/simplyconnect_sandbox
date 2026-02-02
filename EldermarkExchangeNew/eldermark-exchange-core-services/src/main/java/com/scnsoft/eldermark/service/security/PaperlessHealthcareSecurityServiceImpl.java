package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("experienceCenterSecurityService")
@Transactional(readOnly = true)
public class PaperlessHealthcareSecurityServiceImpl extends BaseSecurityService implements PaperlessHealthcareSecurityService {

    @Override
    public boolean canView() {
        var permissionFilter = currentUserFilter();
        return permissionFilter.hasPermission(Permission.PAPERLESS_HEALTHCARE_ACCESS_IF_ENABLED);
    }

}
