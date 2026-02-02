package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dto.client.appointment.ClientAppointmentListItemDto;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.security.ClientAppointmentSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientAppointmentListItemDtoConverter implements ListAndItemConverter<ClientAppointment, ClientAppointmentListItemDto> {

    @Autowired
    private ClientAppointmentSecurityService clientAppointmentSecurityService;

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public ClientAppointmentListItemDto convert(ClientAppointment source) {
        var target = new ClientAppointmentListItemDto();
        target.setId(source.getId());
        target.setDateFrom(DateTimeUtils.toEpochMilli(source.getDateFrom()));
        target.setDateTo(DateTimeUtils.toEpochMilli(source.getDateTo()));
        boolean canView = clientAppointmentSecurityService.canView(source.getId());
        target.setCanView(canView);
        target.setStatusTitle(source.getStatus().getDisplayName());
        target.setStatusName(source.getStatus().name());
        var client = source.getClient();
        target.setClientId(client.getId());
        target.setCanViewClient(clientSecurityService.canView(client.getId()));
        target.setClientName(client.getFullName());

        if (canView) {
            target.setTitle(source.getTitle());
            target.setTypeTitle(source.getType().getDisplayName());
            target.setTypeName(source.getType().name());

            target.setClientGender(CcdUtils.displayName(client.getGender()));
            target.setClientDOB(DateTimeUtils.formatLocalDate(client.getBirthDate()));

            target.setCreator(source.getCreator().getFullName());
            if (CollectionUtils.isNotEmpty(source.getServiceProviderIds())) {
                target.setServiceProviders(employeeDao.findByIdIn(source.getServiceProviderIds(), IdNamesAware.class).stream().map(IdNamesAware::getFullName).collect(Collectors.toList()));
            }
            target.setLocation(source.getLocation());
            target.setCommunityName(client.getCommunity().getName());
            target.setReasonForVisit(source.getReasonForVisit());
            target.setDirectionsInstructions(source.getDirectionsInstructions());
            target.setCancellationReason(source.getCancellationReason());

            boolean canEdit = BooleanUtils.isFalse(source.getArchived()) && clientAppointmentSecurityService.canEdit(source.getId());
            target.setCanEdit(canEdit && clientAppointmentService.validForEdit(source.getStatus()));
            target.setCanCancel(canEdit && clientAppointmentService.validForCancel(source.getStatus(), source.getDateTo()));
            target.setCanDuplicate(clientAppointmentService.validForDuplicate(source.getStatus()));
        }

        return target;
    }

}
