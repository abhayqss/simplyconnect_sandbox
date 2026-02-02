package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.events.EventNotificationListItemDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.event.EventNotificationMessage;
import com.scnsoft.eldermark.entity.event.GroupedEventNotification;
import com.scnsoft.eldermark.service.EventNotificationService;
import com.scnsoft.eldermark.service.security.ContactSecurityService;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventNotificationListItemDtoConverter implements ListAndItemConverter<GroupedEventNotification, EventNotificationListItemDto> {

    private Set<NotificationType> deliverableNotificationTypes = Set.of(NotificationType.SMS, NotificationType.EMAIL, NotificationType.FAX, NotificationType.SECURITY_MESSAGE);

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private ContactSecurityService contactSecurityService;

    @Override
    public EventNotificationListItemDto convert(GroupedEventNotification source) {
        EventNotificationListItemDto target = new EventNotificationListItemDto();
        target.setResponsibility(source.getResponsibility().getDescription());
        target.setCareTeamMemberRole(Optional.ofNullable(source.getCareTeamRole()).map(CareTeamRole::getName).orElse(null));
        var messages = eventNotificationService.find(source.getEvent().getId(), Optional.ofNullable(source.getEmployee()).map(Employee::getId).orElse(null), Optional.ofNullable(source.getCareTeamRole()).map(CareTeamRole::getId).orElse(null));
        target.setChannels(messages.stream().map(eventNotificationMessage -> eventNotificationMessage.getNotificationType().getDescription()).collect(Collectors.joining(", ")));
        target.setDateCreated(messages.stream().map(EventNotificationMessage::getSentDatetime).filter(Objects::nonNull).findFirst().map(DateTimeUtils::toEpochMilli).orElse(null));
        target.setHint(getHint(source, messages));
        if (source.getEmployee() != null) {
            var employee = source.getEmployee();
            target.setContactFirstName(employee.getFirstName());
            target.setContactLastName(employee.getLastName());
            target.setContactFullName(employee.getFullName());
            target.setOrganization(employee.getOrganization().getName());
            target.setContactAvatarId(Optional.ofNullable(employee.getAvatar()).map(Avatar::getId).orElse(null));
            target.setContactId(employee.getId());
            target.setContactPhone(PersonTelecomUtils.findValue(source.getEmployee().getPerson(), PersonTelecomCode.MC).orElse(null));
            target.setContactEmail(PersonTelecomUtils.findValue(source.getEmployee().getPerson(), PersonTelecomCode.EMAIL).orElse(null));
            target.setCanViewContact(contactSecurityService.canView(source.getEmployee().getId()));
        }
        return target;
    }

    private String getHint(GroupedEventNotification source, List<EventNotificationMessage> messages) {
        Client client = source.getEvent().getClient();
        String initials = ClientUtils.getInitials(client, ".");
        String messageDetails = messages.stream()
                .filter(eventNotificationMessage -> deliverableNotificationTypes.contains(eventNotificationMessage.getNotificationType()) && StringUtils.isNotBlank(eventNotificationMessage.getDestination()))
                .map(eventNotificationMessage -> String.format("%s sent to: %s", eventNotificationMessage.getNotificationType().getDescription(), eventNotificationMessage.getDestination()))
                .collect(Collectors.joining(", "));

        String info = String.format("A new event has been logged to the Simply Connect platform for %s and You are %s for this event type.", initials, source.getResponsibility().getDescription());
        return (StringUtils.isNotBlank(messageDetails) ? messageDetails + ".\n " : "") + info;
    }
}
