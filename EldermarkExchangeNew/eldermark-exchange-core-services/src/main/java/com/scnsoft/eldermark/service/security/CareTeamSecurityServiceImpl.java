package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.CareTeamSecurityFieldsAware;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("careTeamSecurityService")
@Transactional(readOnly = true)
public class CareTeamSecurityServiceImpl extends BaseSecurityService implements CareTeamSecurityService {

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private ClientCareTeamSecurityService clientCareTeamSecurityService;

    @Autowired
    private CommunityCareTeamSecurityService communityCareTeamSecurityService;

    @Override
    public boolean canView(Long careTeamMemberId) {
        var careTeamMember = careTeamMemberService.findById(careTeamMemberId);

        if (careTeamMember instanceof ClientCareTeamMember) {
            return clientCareTeamSecurityService.canView((ClientCareTeamMember) careTeamMember);
        }

        if (careTeamMember instanceof CommunityCareTeamMember) {
            return communityCareTeamSecurityService.canView((CommunityCareTeamMember) careTeamMember);
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    @Override
    public boolean canViewList(Long clientId) {
        if (clientId != null) {
            return clientCareTeamSecurityService.canViewList(clientId);
        }
        return communityCareTeamSecurityService.canViewList();
    }

    @Override
    public boolean canEdit(Long careTeamMemberId, Long targetCareTeamRoleId) {
        var careTeamMember = careTeamMemberService.findById(careTeamMemberId);

        if (careTeamMember instanceof ClientCareTeamMember) {
            return clientCareTeamSecurityService.canEdit((ClientCareTeamMember) careTeamMember, targetCareTeamRoleId);
        }

        if (careTeamMember instanceof CommunityCareTeamMember) {
            return communityCareTeamSecurityService.canEdit((CommunityCareTeamMember) careTeamMember, targetCareTeamRoleId);
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    @Override
    public boolean canAdd(CareTeamSecurityFieldsAware dto) {
        if (dto.getClientId() != null) {
            return clientCareTeamSecurityService.canAdd(dto);
        }

        if (dto.getCommunityId() != null) {
            return communityCareTeamSecurityService.canAdd(dto);
        }

        throw new ValidationException("Either clientId or communityId must be provided");
    }

    @Override
    public boolean canDelete(Long careTeamMemberId) {
        return canDelete(careTeamMemberService.findById(careTeamMemberId), currentUserFilter());
    }

    @Override
    public boolean canDelete(CareTeamMember careTeamMember, PermissionFilter permissionFilter) {
        //todo check possible proxy issues
        if (careTeamMember instanceof ClientCareTeamMember) {
            return clientCareTeamSecurityService.canDelete((ClientCareTeamMember) careTeamMember, permissionFilter);
        }

        if (careTeamMember instanceof CommunityCareTeamMember) {
            return communityCareTeamSecurityService.canDelete((CommunityCareTeamMember) careTeamMember, permissionFilter);
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    @Override
    public boolean canListContactsForCareTeamInOrganization(Long requestedOrganizationId, Long clientId, Long communityId) {
        var filter = currentUserFilter();
        var linkedEmployees = filter.getEmployees();
        if (!isAnyCreatedUnderOrganization(linkedEmployees, requestedOrganizationId)) {
            if (!filter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return false;
            }
        }

        return canAddAnyRole(clientId, communityId, AffiliatedCareTeamType.REGULAR_AND_PRIMARY);
    }

    @Override
    public boolean canAddAnyRole(Long clientId, Long communityId, AffiliatedCareTeamType type) {
        if (clientId != null) {
            return clientCareTeamSecurityService.canAddAnyRoleAndEmployee(clientId, type);
        }

        if (communityId != null) {
            return communityCareTeamSecurityService.canAddAnyRoleAndEmployee(communityId, type);
        }

        return false;
    }
}
