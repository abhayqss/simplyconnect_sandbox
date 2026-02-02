package com.scnsoft.eldermark.mobile.converters.careteam;

import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.mobile.dto.careteam.BaseCareTeamContactItem;
import com.scnsoft.eldermark.mobile.dto.careteam.BaseCareTeamMemberDto;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class BaseCareTeamMemberDtoConverter {

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private ClientCareTeamSecurityService clientCareTeamSecurityService;

    @Autowired
    private CommunityCareTeamSecurityService communityCareTeamSecurityService;

    protected void fillBaseCareTeamMemberDto(CareTeamMember source, boolean isCanDeleteOnly, BaseCareTeamMemberDto target) {
        target.setId(source.getId());
        target.setRole(
                Optional.ofNullable(source.getCareTeamRole()).map(CareTeamRole::getName).orElse(null)
        );
        if (source instanceof ClientCareTeamMember) {
            target.setIsOnHold(((ClientCareTeamMember) source).getOnHold());
            target.setCanDelete(isCanDeleteOnly || clientCareTeamSecurityService.canDelete((ClientCareTeamMember) source));
        } else if (source instanceof CommunityCareTeamMember) {
            target.setIsOnHold(
                    communityCareTeamMemberService.isOnHoldCandidate(
                            ((CommunityCareTeamMember) source).getCommunity().getOrganizationId(),
                            source.getEmployee().getOrganizationId()
                    )
            );
            target.setCanDelete(isCanDeleteOnly || communityCareTeamSecurityService.canDelete((CommunityCareTeamMember) source));
        }
    }

    void fillBaseContact(Employee employee, BaseCareTeamContactItem target, boolean isOnHold) {
        target.setId(employee.getId());
        target.setIdentity(ConversationUtils.employeeIdToIdentity(employee.getId()));
        target.setTwilioUserSid(employee.getTwilioUserSid());

        if (employee.getAvatar() != null) {
            target.setAvatarId(employee.getAvatar().getId());
            target.setAvatarName(employee.getAvatar().getAvatarName());
        }

        target.setCommunityId(employee.getCommunityId());
        if (employee.getCommunity() != null) {
            target.setCommunityName(employee.getCommunity().getName());
            target.setCommunityLogoName(employee.getCommunity().getMainLogoPath());
        }

        target.setOrganizationId(employee.getOrganizationId());
        target.setOrganizationLogoName(employee.getOrganization().getMainLogoPath());

        target.setFullName(employee.getFullName());
        target.setFirstName(employee.getFirstName());
        target.setLastName(employee.getLastName());

        target.setCanView(contactSecurityService.canView(employee.getId()));
        target.setIsActive(employee.getStatus() == EmployeeStatus.ACTIVE);
        target.setStatusName(employee.getStatus().name());

        var currentEmployee = loggedUserService.getCurrentEmployee();

        if (!isOnHold) {
            var areChatsEnabled = chatSecurityService.areChatsAccessibleByEmployee(currentEmployee);
            if (areChatsEnabled) {
                if (chatSecurityService.canStart(Collections.singletonList(employee.getId()))) {
                    var existingConversationSid = chatService.findPersonalChatSid(currentEmployee.getId(), employee.getId())
                            .orElse(null);

                    var existingConversationConnected = false;

                    if (StringUtils.isNotEmpty(existingConversationSid)) {
                        target.setCanStartConversation(false);
                        existingConversationConnected = chatService.isConnected(existingConversationSid);
                        if (existingConversationConnected) {
                            target.setConversationSid(existingConversationSid);
                        }
                    } else {
                        target.setCanStartConversation(true);
                    }

                    if (videoCallSecurityService.areVideoCallsAccessibleByEmployee(currentEmployee)) {
                        if (StringUtils.isEmpty(existingConversationSid) || existingConversationConnected) {
                            target.setCanCall(videoCallSecurityService.canStart(
                                    null,
                                    Set.of(employee.getId()))
                            );
                        }
                    }
                }
            }
        }
    }
}
