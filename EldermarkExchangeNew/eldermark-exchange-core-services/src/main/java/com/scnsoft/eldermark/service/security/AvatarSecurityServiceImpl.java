package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.service.AvatarService;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.careteam.invitation.ClientCareTeamInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service("avatarSecurityService")
@Transactional(readOnly = true)
public class AvatarSecurityServiceImpl extends BaseSecurityService implements AvatarSecurityService {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Autowired
    private ProspectSecurityService prospectSecurityService;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private ClientCareTeamInvitationSecurityService clientCareTeamInvitationSecurityService;

    @Override
    public boolean canView(Long avatarId) {
        var avatar = avatarService.findSecurityAware(avatarId);

        if (avatar.getClientId() != null) {
            if (clientSecurityService.canView(avatar.getClientId())
                    || chatSecurityService.existsConversationWithClient(avatar.getClientId())
                    || clientCareTeamInvitationSecurityService.existsInbound(avatar.getClientId())) {
                return true;
            }
        }

        if (avatar.getEmployeeId() != null) {
            if (contactSecurityService.canView(avatar.getEmployeeId())
                    || chatSecurityService.canStart(Set.of(avatar.getEmployeeId()))
                    || videoCallSecurityService.canStart(null, Set.of(avatar.getEmployeeId()))
                    || chatSecurityService.existsConversationWithEmployee(avatar.getEmployeeId())
                    || clientCareTeamInvitationSecurityService.existsAccessibleToTargetEmployee(avatar.getEmployeeId())) {
                return true;
            }
        }

        if (avatar.getProspectId() != null) {
            if (prospectSecurityService.canView(avatar.getProspectId())) {
                return true;
            }
        }

        if (avatar.getSecondOccupantProspectId() != null) {
            if (prospectSecurityService.canView(avatar.getSecondOccupantProspectId())) {
                return true;
            }
        }

        return false;
    }
}
