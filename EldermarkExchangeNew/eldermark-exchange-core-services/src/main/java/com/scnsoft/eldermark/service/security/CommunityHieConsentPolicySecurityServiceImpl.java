package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.stereotype.Service;

@Service("communityHieConsentPolicySecurityService")
public class CommunityHieConsentPolicySecurityServiceImpl extends BaseSecurityService implements CommunityHieConsentPolicySecurityService {

    @Override
    public boolean canEdit(Long communityId) {
        return currentUserFilter().hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR);
    }
}
