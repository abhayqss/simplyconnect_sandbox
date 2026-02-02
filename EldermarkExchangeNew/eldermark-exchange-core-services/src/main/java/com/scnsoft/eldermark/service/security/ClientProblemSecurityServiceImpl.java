package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientProblemSecurityService")
@Transactional(readOnly = true)
public class ClientProblemSecurityServiceImpl extends AccessFlagsCheckingSecurityService implements ClientProblemSecurityService {


    private static final PermissionScopeProvider permissionsScope = AccessFlagsCheckingSecurityService.buildScopeProvider(
            PROBLEM_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
            PROBLEM_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            PROBLEM_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            PROBLEM_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            PROBLEM_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            PROBLEM_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            PROBLEM_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            PROBLEM_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            PROBLEM_VIEW_MERGED_IF_SELF_RECORD,
            PROBLEM_VIEW_MERGED_IF_ACCESSIBLE_REFERRAL_REQUEST,
            PROBLEM_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = permissionsScope.getAllPermissions();

    @Autowired
    private ClientProblemService clientProblemService;

    @Override
    public boolean canView(Long id) {
        var problem = clientProblemService.findSecurityAwareEntity(id);
        return canViewByClientOrMerged(problem.getClientId(),
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
        return filter.getClientId() == null || canViewByClientOrMerged(filter.getClientId(), permissionsScope, AccessRight.Code.MY_PHR);
    }
}
