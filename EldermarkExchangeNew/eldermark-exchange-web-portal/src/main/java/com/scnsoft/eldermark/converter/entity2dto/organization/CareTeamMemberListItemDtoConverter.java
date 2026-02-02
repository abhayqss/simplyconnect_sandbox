package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.dto.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.facade.CareTeamMemberUIButtonsService;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class CareTeamMemberListItemDtoConverter implements BiFunction<CareTeamMember, CareTeamFilter, CareTeamMemberListItemDto> {

    @Autowired
    private CareTeamMemberUIButtonsService careTeamMemberUIButtonsService;

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSecurityService chatSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Override
    public CareTeamMemberListItemDto apply(CareTeamMember source, CareTeamFilter careTeamFilter) {
        CareTeamMemberListItemDto target = new CareTeamMemberListItemDto();
        target.setId(source.getId());
        if (source.getEmployee().getAvatar() != null) {
            target.setAvatarId(source.getEmployee().getAvatar().getId());
        }
        target.setEmployeeCommunityId(source.getEmployee().getCommunityId());
        target.setCommunityName(Optional.ofNullable(source.getEmployee().getCommunity()).map(Community::getName).orElse(null));
        target.setOrganizationId(source.getEmployee().getOrganizationId());
        target.setOrganizationName(source.getEmployee().getOrganization().getName());
        target.setDescription(source.getDescription());
        target.setContactName(source.getEmployee().getFullName());
        target.setCanViewContact(contactSecurityService.canView(source.getEmployeeId()));
        target.setRoleName(source.getCareTeamRole().getName());
        target.setIsActive(source.getEmployee().getStatus() == EmployeeStatus.ACTIVE);
        target.setEmployeeId(source.getEmployee().getId());

        if (source instanceof ClientCareTeamMember) {
            var clientCtm = (ClientCareTeamMember) source;
            target.setClientId(clientCtm.getClient().getId());
            target.setCanViewClient(clientSecurityService.canView(clientCtm.getClient().getId()));
            target.setIsPrimaryContact(CollectionUtils.isNotEmpty(clientCtm.getPrimaryContacts()));
            target.setIsOnHold(clientCtm.getOnHold());
        }

        Person person = source.getEmployee().getPerson();
        target.setEmail(PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL).orElse(null));
        target.setPhone(PersonTelecomUtils.findValue(person, PersonTelecomCode.MC).orElse(null));

        target.setCanEdit(careTeamMemberUIButtonsService.canEdit(source, careTeamFilter));
        target.setCanDelete(careTeamMemberUIButtonsService.canDelete(source, careTeamFilter));

        target.setIsConversationAllowed(
                chatSecurityService.canStart(Collections.singletonList(source.getEmployeeId()))
        );

        chatService.findPersonalChatSid(loggedUserService.getCurrentEmployeeId(), source.getEmployeeId())
                .ifPresent(target::setConversationSid);

        target.setIsVideoCallAllowed(videoCallSecurityService.canStart(null, Set.of(source.getEmployeeId())));

        return target;
    }
}
