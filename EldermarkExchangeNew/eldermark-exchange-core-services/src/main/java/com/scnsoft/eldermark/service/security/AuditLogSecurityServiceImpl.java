package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.AUDIT_LOG_VIEW_IF_ASSOCIATED_ORGANIZATION;
import static com.scnsoft.eldermark.entity.security.Permission.ROLE_SUPER_ADMINISTRATOR;

@Service("auditLogSecurityService")
@Transactional(readOnly = true)
public class AuditLogSecurityServiceImpl extends BaseSecurityService implements AuditLogSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            ROLE_SUPER_ADMINISTRATOR,
            AUDIT_LOG_VIEW_IF_ASSOCIATED_ORGANIZATION);

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }
}
