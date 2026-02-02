package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeMobileSecurityServiceImpl extends BaseSecurityService implements EmployeeMobileSecurityService {

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Override
    @Transactional(readOnly = true)
    public boolean canEdit(Long id) {

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return permissionFilter.getAllEmployeeIds().contains(id)
                && permissionFilter.hasPermission(Permission.ROLE_PARENT_GUARDIAN)
                && contactSecurityService.canEdit(id, CareTeamRoleService.ANY_TARGET_ROLE);
    }
}
