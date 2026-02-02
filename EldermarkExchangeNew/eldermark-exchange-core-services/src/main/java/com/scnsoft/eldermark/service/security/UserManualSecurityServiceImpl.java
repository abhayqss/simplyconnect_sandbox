package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service("userManualSecurityService")
public class UserManualSecurityServiceImpl extends BaseSecurityService implements UserManualSecurityService {

    @Override
    public boolean canView() {
        return hasAnyPermission(Arrays.asList(
                Permission.ROLE_SUPER_ADMINISTRATOR,
                Permission.VIEW_USER_MANUALS)
        );
    }

    @Override
    public boolean canUpload() {
        return hasAnyPermission(Collections.singleton(Permission.ROLE_SUPER_ADMINISTRATOR));
    }

    @Override
    public boolean canDelete() {
        return hasAnyPermission(Collections.singleton(Permission.ROLE_SUPER_ADMINISTRATOR));
    }
}
