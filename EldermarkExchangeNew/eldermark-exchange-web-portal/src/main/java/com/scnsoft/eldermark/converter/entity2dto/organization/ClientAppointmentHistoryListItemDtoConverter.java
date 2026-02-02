package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.client.appointment.ClientAppointmentHistoryListItemDto;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientAppointmentHistoryListItemDtoConverter implements ListAndItemConverter<ClientAppointment, ClientAppointmentHistoryListItemDto> {

    @Override
    public ClientAppointmentHistoryListItemDto convert(ClientAppointment source) {
        var target = new ClientAppointmentHistoryListItemDto();
        target.setId(source.getId());
        target.setModifiedDate(DateTimeUtils.toEpochMilli(source.getLastModifiedDate()));
        target.setAuthor(source.getCreator().getFullName());
        target.setAuthorRole(source.getCreator().getCareTeamRole().getName());
        target.setStatus(source.getAuditableStatus().getDisplayName());
        target.setArchived(source.getArchived());
        return target;
    }
}
