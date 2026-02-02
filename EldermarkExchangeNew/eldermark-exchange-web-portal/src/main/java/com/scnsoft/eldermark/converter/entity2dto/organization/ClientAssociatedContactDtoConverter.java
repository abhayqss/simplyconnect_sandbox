package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.projection.NamesAvatarIdAware;
import com.scnsoft.eldermark.dto.ClientAssociatedContactDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.security.ChatSecurityService;
import com.scnsoft.eldermark.service.security.ContactSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientAssociatedContactDtoConverter implements Converter<Client, ClientAssociatedContactDto> {

    @Autowired
    private EmployeeService employeeService;

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

    @Override
    public ClientAssociatedContactDto convert(Client source) {
        var target = new ClientAssociatedContactDto();
        if (CollectionUtils.isEmpty(source.getAssociatedEmployeeIds())) {
            target.setCanCreate(contactSecurityService.canAddAssociatedClientContact(source.getOrganizationId(), source.getCommunityId()));
        } else {
            var associatedEmployeeId = source.getAssociatedEmployeeIds().get(0);
            target.setId(associatedEmployeeId);

            var associatedEmployee = employeeService.findById(associatedEmployeeId, NamesAvatarIdAware.class);
            target.setFullName(associatedEmployee.getFullName());
            target.setAvatarId(associatedEmployee.getAvatarId());
            target.setCanView(contactSecurityService.canView(associatedEmployeeId));

            var loggedEmployeeId = loggedUserService.getCurrentEmployeeId();

            var canStartConversation = chatSecurityService.canStart(List.of(associatedEmployeeId, loggedEmployeeId));
            if (canStartConversation) {
                target.setConversationSid(chatService.findPersonalChatSid(loggedEmployeeId, associatedEmployeeId).orElse(null));
                if (StringUtils.isEmpty(target.getConversationSid())) {
                    target.setCanStartConversation(true);
                }
            }

            target.setCanStartVideoCall(videoCallSecurityService.canStart(null, List.of(associatedEmployeeId, loggedEmployeeId)));
            target.setCanViewCallHistory(videoCallSecurityService.canViewHistory());
            target.setChatEnabled(source.getOrganization().isChatEnabled());
        }
        return target;
    }
}
