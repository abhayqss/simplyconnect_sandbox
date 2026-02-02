package com.scnsoft.eldermark.mobile.converters.client;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.mobile.dto.client.ClientAssociatedContactDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientListItemDto;
import com.scnsoft.eldermark.mobile.projection.client.MobileClientListInfo;
import com.scnsoft.eldermark.service.security.ChatSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

public abstract class BaseClientDtoConverter {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    protected LoggedUserService loggedUserService;

    protected void fillListItem(MobileClientListInfo source, ClientListItemDto dto, PermissionFilter permissionFilter) {
        dto.setId(source.getId());
        dto.setAvatarId(source.getAvatarId());
        dto.setAvatarName(source.getAvatarAvatarName());

        dto.setCommunityId(source.getCommunityId());
        dto.setCommunityName(source.getCommunityName());
        dto.setOrganizationId(source.getOrganizationId());
        dto.setOrganizationName(source.getOrganizationName());

        dto.setFirstName(source.getFirstName());
        dto.setMiddleName(source.getMiddleName());
        dto.setLastName(source.getLastName());
        dto.setFullName(CareCoordinationUtils.getFullName(
                source.getFirstName(), source.getMiddleName(), source.getLastName())
        );

        dto.setCanView(clientSecurityService.canView(source.getId(), permissionFilter));

        if (source.getAssociatedEmployeeId() != null) {
            var associatedContactDto = new ClientAssociatedContactDto();
            associatedContactDto.setId(source.getAssociatedEmployeeId());
            associatedContactDto.setTwilioUserSid(source.getAssociatedEmployeeTwilioUserSid());

            if (dto.getCanView()) {
                var currentEmployee = loggedUserService.getCurrentEmployee();
                var areChatEnabled = chatSecurityService.areChatsAccessibleByEmployee(currentEmployee);
                var sourceIdWrapped = Collections.singletonList(source.getAssociatedEmployeeId());
                if (areChatEnabled) {
                    if (chatSecurityService.canStart(sourceIdWrapped, permissionFilter)) {
                        var existingConversationSid = chatService.findPersonalChatSid(currentEmployee.getId(), source.getAssociatedEmployeeId())
                                .orElse(null);

                        var existingConversationConnected = false;

                        if (StringUtils.isNotEmpty(existingConversationSid)) {
                            associatedContactDto.setCanStartConversation(false);
                            existingConversationConnected = chatService.isConnected(existingConversationSid);
                            if (existingConversationConnected) {
                                associatedContactDto.setConversationSid(existingConversationSid);
                            }
                        } else {
                            associatedContactDto.setCanStartConversation(true);
                        }

                        if (videoCallSecurityService.areVideoCallsAccessibleByEmployee(currentEmployee)) {
                            if (StringUtils.isEmpty(existingConversationSid) || existingConversationConnected) {
                                associatedContactDto.setCanCall(videoCallSecurityService.canStart(
                                        null,
                                        sourceIdWrapped,
                                        permissionFilter)
                                );
                            }
                        }
                    }
                }
            }

            dto.setAssociatedContact(associatedContactDto);
        }

        dto.setIsFavourite(source.getIsFavourite());
    }
}
