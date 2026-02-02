package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("docutrackSecurityService")
@Transactional(readOnly = true)
public class DocutrackSecurityServiceImpl extends BaseSecurityService implements DocutrackSecurityService {

    private static final Set<Permission> CONFIGURE_PERMISSIONS = EnumSet.of(
            DOCUTRACK_CONFIGURE_ALL,
            DOCUTRACK_CONFIGURE_IF_ASSOCIATED_ORGANIZATION
    );

    @Override
    public boolean canConfigureDocutrackInOrg(Long organizationId) {
        var filter = currentUserFilter();

        if (filter.hasPermission(DOCUTRACK_CONFIGURE_ALL)) {
            return true;
        }

        if (filter.hasPermission(Permission.DOCUTRACK_CONFIGURE_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(Permission.DOCUTRACK_CONFIGURE_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canConfigureDocutrackInAnyOrg() {
       return hasAnyPermission(CONFIGURE_PERMISSIONS);
    }
}
