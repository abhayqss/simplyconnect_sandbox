package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationClientSecurityFieldsAdapter;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamInvitationSecurityFieldsAware;
import com.scnsoft.eldermark.beans.projection.IdAndOrganizationIdAndCommunityIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("careTeamInvitationSecurityService")
@Transactional(readOnly = true)
public class ClientCareTeamInvitationSecurityServiceImpl extends BaseSecurityService implements ClientCareTeamInvitationSecurityService {

    private final Set<Permission> VIEW_PERMISSIONS = EnumSet.of(
            ROLE_SUPER_ADMINISTRATOR,
            CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_ORGANIZATION,
            CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_COMMUNITY,
            CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_CLIENT_RECORD,
            CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_TARGET_EMPLOYEE
    );

    @Autowired
    private ClientCareTeamInvitationService careTeamInvitationService;

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_PERMISSIONS);
    }

    @Override
    public boolean canView(Long invitationId) {
        var invitation = Lazy.of(
                () -> careTeamInvitationService.findById(invitationId, ClientCareTeamInvitationSecurityFieldsAware.class)
        );

        var permissionFilter = currentUserFilter();

        return hasInvitationRelatedPermissions(permissionFilter,
                invitation,
                CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_TARGET_EMPLOYEE
        ) ||
                hasClientRelatedPermissions(
                        permissionFilter,
                        invitation.map(ClientCareTeamInvitationClientSecurityFieldsAdapter::new),
                        CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_ORGANIZATION,
                        CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_COMMUNITY,
                        CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_CLIENT_RECORD
                );
    }

    @Override
    public boolean canInvite(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var client = Lazy.of(() -> clientService.findById(clientId, IdAndOrganizationIdAndCommunityIdAware.class));
        return hasClientRelatedPermissions(
                permissionFilter,
                client,
                CLIENT_CARE_TEAM_INVITATION_ADD_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_INVITATION_ADD_IF_ASSOCIATED_COMMUNITY,
                CLIENT_CARE_TEAM_INVITATION_ADD_IF_SELF_CLIENT_RECORD
        );
    }

    @Override
    public boolean canResend(Long invitationId) {
        var invitation = careTeamInvitationService.findById(invitationId, ClientCareTeamInvitationSecurityFieldsAware.class);
        return careTeamInvitationService.canResend(invitation) && canResendOrCancel(invitation);
    }

    @Override
    public boolean canCancel(Long invitationId) {
        var invitation = careTeamInvitationService.findById(invitationId, ClientCareTeamInvitationSecurityFieldsAware.class);
        return careTeamInvitationService.canCancel(invitation) && canResendOrCancel(invitation);
    }

    private boolean canResendOrCancel(ClientCareTeamInvitationSecurityFieldsAware invitation) {
        var permissionFilter = currentUserFilter();
        var client = Lazy.of(() -> new ClientCareTeamInvitationClientSecurityFieldsAdapter(invitation));

        return hasClientRelatedPermissions(
                permissionFilter,
                client,
                CLIENT_CARE_TEAM_INVITATION_RESEND_CANCEL_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_CARE_TEAM_INVITATION_RESEND_CANCEL_IF_ASSOCIATED_COMMUNITY,
                CLIENT_CARE_TEAM_INVITATION_RESEND_CANCEL_IF_SELF_CLIENT_RECORD
        );
    }

    @Override
    public boolean canAcceptOrDecline(Long invitationId) {
        var invitation = careTeamInvitationService.findById(invitationId, ClientCareTeamInvitationSecurityFieldsAware.class);
        return careTeamInvitationService.canAcceptOrDecline(invitation) && canAcceptOrDecline(invitation);
    }

    private boolean canAcceptOrDecline(ClientCareTeamInvitationSecurityFieldsAware invitation) {
        var permissionFilter = currentUserFilter();

        return hasInvitationRelatedPermissions(permissionFilter, Lazy.of(invitation),
                CLIENT_CARE_TEAM_INVITATION_ACCEPT_DECLINE_IF_SELF_TARGET_EMPLOYEE);
    }

    private boolean hasClientRelatedPermissions(
            PermissionFilter permissionFilter,
            Lazy<? extends IdAndOrganizationIdAndCommunityIdAware> client,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission ifSelfClientRecordPermission
    ) {

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);

            if (isAnyCreatedUnderOrganization(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifSelfClientRecordPermission)) {
            var employees = permissionFilter.getEmployees(ifSelfClientRecordPermission);

            if (isSelfClientRecord(employees, client.get().getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasInvitationRelatedPermissions(
            PermissionFilter permissionFilter,
            Lazy<? extends ClientCareTeamInvitationSecurityFieldsAware> invitation,
            Permission ifSelfTargetEmployeePermission
    ) {

        if (permissionFilter.hasPermission(ifSelfTargetEmployeePermission)) {
            var employees = permissionFilter.getEmployees(ifSelfTargetEmployeePermission);

            if (isSelfEmployeeRecord(employees, invitation.get().getTargetEmployeeId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean existsInbound(Long clientId) {
        var allEmployeeIds = currentUserFilter().getAllEmployeeIds();
        return careTeamInvitationService.existsInbound(clientId, allEmployeeIds);
    }


    @Override
    public boolean existsAccessibleToTargetEmployee(Long employeeId) {
        return careTeamInvitationService.existsAccessibleToTargetEmployee(employeeId, currentUserFilter());
    }
}
