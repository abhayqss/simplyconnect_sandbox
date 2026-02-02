package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.security.CareTeamSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class CareTeamMemberUIButtonsServiceImpl implements CareTeamMemberUIButtonsService {

    @Autowired
    private CareTeamSecurityService careTeamSecurityService;

    @Override
    public boolean canViewList(Long clientId, Long communityId) {
        return careTeamSecurityService.canViewList(clientId);
    }

    @Override
    public boolean canAdd(Long clientId, Long communityId, CareTeamFilter.Affiliation affiliation) {
        return careTeamSecurityService.canAddAnyRole(clientId, communityId, affiliation.toType());
    }

    @Override
    public boolean canEdit(CareTeamMember careTeamMember, CareTeamFilter filter) {
        if (careTeamMember instanceof ClientCareTeamMember) {
            var clientCtm = (ClientCareTeamMember) careTeamMember;
            return !isViewThroughMergedClient(clientCtm, filter) && careTeamSecurityService.canEdit(careTeamMember.getId(), CareTeamRoleService.ANY_TARGET_ROLE);
        }
        return careTeamSecurityService.canEdit(careTeamMember.getId(), CareTeamRoleService.ANY_TARGET_ROLE);
    }

    @Override
    public boolean canDelete(CareTeamMember careTeamMember, CareTeamFilter filter) {
        if (careTeamMember instanceof ClientCareTeamMember) {
            var clientCtm = (ClientCareTeamMember) careTeamMember;
            return !isViewThroughMergedClient(clientCtm, filter) && careTeamSecurityService.canDelete(careTeamMember.getId());
        }
        return careTeamSecurityService.canDelete(careTeamMember.getId());
    }

    private boolean isViewThroughMergedClient(ClientCareTeamMember clientCtm, CareTeamFilter filter) {
        Objects.requireNonNull(filter.getClientId());
        return !filter.getClientId().equals(clientCtm.getClientId());
    }
}
