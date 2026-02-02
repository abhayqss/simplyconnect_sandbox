package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientCareTeamFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.IncidentClientDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.VideoCallService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class IncidentClientDtoConverter implements BiFunction<IncidentReport, PermissionFilter, IncidentClientDto> {
    private static final Logger logger = LoggerFactory.getLogger(IncidentClientDtoConverter.class);

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public IncidentClientDto apply(IncidentReport source, PermissionFilter permissionFilter) {
        if (source == null) {
            return null;
        }
        var target = new IncidentClientDto();
        target.setId(source.getEvent().getClientId());
        target.setFullName(CareCoordinationUtils.getFullName(source.getFirstName(), source.getLastName()));
        target.setUnit(source.getUnitNumber());
        target.setPhone(source.getClientPhone());
        target.setSiteName(source.getSiteName());
        target.setAddress(source.getClassMemberCurrentAddress());
        if (source.getEvent().getClient().getAvatar() != null) {
            target.setAvatarId(source.getEvent().getClient().getAvatar().getId());
        }
        target.setCanView(clientSecurityService.canView(target.getId(), permissionFilter));
        target.setCommunityId(source.getEvent().getClient().getCommunityId());

        var filter = new AccessibleChatClientCareTeamFilter();
        filter.setClientId(source.getEvent().getClientId());
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        if (source.getEvent().getClient().getHieConsentPolicyType() != HieConsentPolicyType.OPT_OUT &&
                Boolean.TRUE.equals(source.getEvent().getClient().getActive())) {
            var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
            logger.info("clientCareTeamMemberService.hasVideoCallAccessibleClientCareTeamMembers start");
            var start = System.currentTimeMillis();
            target.setHasAssignedCareTeamMembersWithEnabledVideoConversations(
                    videoCallService.isVideoCallEnabled(currentEmployeeId) &&
                            clientCareTeamMemberService.hasVideoCallAccessibleClientCareTeamMembers(permissionFilter, filter));
            logger.info("clientCareTeamMemberService.hasVideoCallAccessibleClientCareTeamMembers end in {}ms", System.currentTimeMillis() - start);

            if (target.getHasAssignedCareTeamMembersWithEnabledVideoConversations()) {
                target.setHasAssignedCareTeamMembersWithEnabledConversations(true);
            } else {
                logger.info("clientCareTeamMemberService.hasChatAccessibleClientCareTeamMembers start");
                start = System.currentTimeMillis();
                target.setHasAssignedCareTeamMembersWithEnabledConversations(
                        chatService.isChatEnabled(currentEmployeeId) &&
                                clientCareTeamMemberService.hasChatAccessibleClientCareTeamMembers(permissionFilter, filter));
                logger.info("clientCareTeamMemberService.hasChatAccessibleClientCareTeamMembers end in {}ms", System.currentTimeMillis() - start);
            }
        }


        target.setIsActive(source.getEvent().getClient().getActive());
        target.setHieConsentPolicyName(source.getEvent().getClient().getHieConsentPolicyType());

        return target;
    }
}
