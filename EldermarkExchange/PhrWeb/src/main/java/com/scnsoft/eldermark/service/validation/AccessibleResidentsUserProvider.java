package com.scnsoft.eldermark.service.validation;

import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AccessibleResidentsUserProvider extends BasePhrService implements AccessibleResidentsProvider {

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Override
    public Collection<Long> getAccessibleResidentsOrThrow(Long userId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        return getResidentIdsOrThrow(userId);
    }

    @Override
    public Collection<Long> getAccessibleResidentsOrThrow(Long userId, AccessRight.Code accessRightCode) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, accessRightCode);
        return getResidentIdsOrThrow(userId);
    }

    @Override
    public Long getMainResidentOrThrow(Long userId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        return getResidentIdOrThrow(userId);
    }

    @Override
    public Long getMainResidentOrThrow(Long userId, AccessRight.Code accessRightCode) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, accessRightCode);
        return getResidentIdOrThrow(userId);
    }
}
