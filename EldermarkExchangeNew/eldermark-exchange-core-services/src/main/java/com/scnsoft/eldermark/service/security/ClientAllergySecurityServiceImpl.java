package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientAllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientAllergySecurityService")
@Transactional(readOnly = true)
public class ClientAllergySecurityServiceImpl extends AccessFlagsCheckingSecurityService implements ClientAllergySecurityService {

    private static final PermissionScopeProvider permissionsScope = AccessFlagsCheckingSecurityService.buildScopeProvider(
            ALLERGY_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
            ALLERGY_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            ALLERGY_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            ALLERGY_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            ALLERGY_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            ALLERGY_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            ALLERGY_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            ALLERGY_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            ALLERGY_VIEW_MERGED_IF_SELF_RECORD,
            null,
            ALLERGY_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = permissionsScope.getAllPermissions();

    @Autowired
    private ClientAllergyService clientAllergyService;

    @Override
    public boolean canView(Long id) {
        var allergy = clientAllergyService.findSecurityAwareEntity(id);
        return canViewByClientOrMerged(allergy.getClientId(),
                permissionsScope,
                AccessRight.Code.MY_PHR);
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    /* (non-Javadoc)
     *
     * @see CcdEntitySecurityService#canViewOfClientIfPresent(ClientIdAware)
     */
    @Override
    public boolean canViewOfClientIfPresent(ClientIdAware filter) {
        return filter.getClientId() == null || canViewByClientOrMerged(filter.getClientId(),
                permissionsScope,
                AccessRight.Code.MY_PHR);
    }
}
