package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsCareReceiverProvider;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsUserProvider;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public abstract class BasePhrFacade {

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private AccessibleResidentsCareReceiverProvider accessibleResidentsCareReceiverProvider;

    @Autowired
    private AccessibleResidentsUserProvider accessibleResidentsUserProvider;

    protected void validateAssociation(Long residentId, AccessRight.Code accessCode) {
        if (!hasAssociation(residentId, accessCode)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    protected boolean hasAssociation(Long residentId, AccessRight.Code accessCode) {
        return careTeamSecurityUtils.isAssociatedWithCurrentUser(residentId, accessCode);
    }

    protected void validateAssociation(Long residentId) {
        if (!careTeamSecurityUtils.isAssociatedWithCurrentUser(residentId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    protected boolean hasAssociation(Long residentId) {
        return careTeamSecurityUtils.isAssociatedWithCurrentUser(residentId);
    }

    protected boolean currentUserHasAccessRightToUser(Long userId, AccessRight.Code accessRightCode) {
        return careTeamSecurityUtils.checkAccessToUserInfo(userId, accessRightCode);
    }

    protected boolean currentUserHasAccessRightToReceiver(Long receiverId, AccessRight.Code accessRightCode) {
        return careTeamSecurityUtils.checkAccessToCareTeamMember(receiverId, accessRightCode);
    }

    protected Collection<Long> getReceiverResidentIds(Long receiverId, AccessRight.Code accessRightCode) {
        return accessibleResidentsCareReceiverProvider.getAccessibleResidentsOrThrow(receiverId, accessRightCode);
    }

    protected Collection<Long> getReceiverResidentIds(Long receiverId) {
        return accessibleResidentsCareReceiverProvider.getAccessibleResidentsOrThrow(receiverId);
    }

    protected Collection<Long> getUserResidentIds(Long userId, AccessRight.Code accessRightCode) {
        return accessibleResidentsUserProvider.getAccessibleResidentsOrThrow(userId, accessRightCode);
    }

    protected Collection<Long> getUserResidentIds(Long userId) {
        return accessibleResidentsUserProvider.getAccessibleResidentsOrThrow(userId);
    }

    protected final Long getReceiverMainResidentId(Long receiverId, AccessRight.Code accessRightCode) {
        return accessibleResidentsCareReceiverProvider.getMainResidentOrThrow(receiverId, accessRightCode);
    }

    protected final Long getReceiverMainResidentId(Long receiverId) {
        return accessibleResidentsCareReceiverProvider.getMainResidentOrThrow(receiverId);
    }

    protected final Long getUserMainResidentId(Long userId, AccessRight.Code accessRightCode) {
        return accessibleResidentsUserProvider.getMainResidentOrThrow(userId, accessRightCode);
    }

    protected final Long getUserMainResidentId(Long userId) {
        return accessibleResidentsUserProvider.getMainResidentOrThrow(userId);
    }

    public CareTeamSecurityUtils getCareTeamSecurityUtils() {
        return careTeamSecurityUtils;
    }
}
