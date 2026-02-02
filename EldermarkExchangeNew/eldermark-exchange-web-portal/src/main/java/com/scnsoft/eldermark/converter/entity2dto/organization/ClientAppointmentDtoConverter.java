package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dto.client.appointment.ClientAppointmentDto;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentServiceCategory;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.security.ClientAppointmentSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientAppointmentDtoConverter implements Converter<ClientAppointment, ClientAppointmentDto> {

    @Autowired
    private ClientAppointmentSecurityService clientAppointmentSecurityService;

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public ClientAppointmentDto convert(ClientAppointment source) {
        var target = new ClientAppointmentDto();
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setStatus(source.getStatus());
        target.setStatusTitle(source.getStatus().getDisplayName());
        target.setIsPublic(source.getIsPublic());
        target.setLocation(source.getLocation());
        target.setType(source.getType());
        target.setTypeTitle(source.getType().getDisplayName());
        target.setServiceCategory(source.getServiceCategory());
        target.setServiceCategoryTitle(Optional.ofNullable(source.getServiceCategory())
                .map(ClientAppointmentServiceCategory::getDisplayName).orElse(null));
        target.setReferralSource(source.getReferralSource());
        target.setReasonForVisit(source.getReasonForVisit());
        target.setDirectionsInstructions(source.getDirectionsInstructions());
        target.setNotes(source.getNotes());
        target.setClientId(source.getClientId());
        target.setCanViewClient(clientSecurityService.canView(source.getClientId()));
        target.setClientName(source.getClient().getFullName());
        target.setClientDOB(DateTimeUtils.formatLocalDate(source.getClient().getBirthDate()));
        target.setCommunityId(source.getClient().getCommunityId());
        target.setCommunityName(Optional.ofNullable(source.getClient().getCommunity()).map(Community::getName).orElse(null));
        target.setOrganizationId(source.getClient().getOrganizationId());
        target.setOrganizationName(source.getClient().getOrganization().getName());
        target.setCreatorId(source.getCreatorId());
        target.setCreatorName(source.getCreator().getFullName());
        if (CollectionUtils.isNotEmpty(source.getServiceProviderIds())) {
            target.setServiceProviderIds(source.getServiceProviderIds());
            target.setServiceProviderNames(employeeDao.findByIdIn(source.getServiceProviderIds(), IdNamesAware.class).stream().map(IdNamesAware::getFullName).collect(Collectors.toList()));
        }
        target.setDateFrom(DateTimeUtils.toEpochMilli(source.getDateFrom()));
        target.setDateTo(DateTimeUtils.toEpochMilli(source.getDateTo()));
        target.setReminders(source.getReminders());
        if (CollectionUtils.isNotEmpty(source.getReminders())) {
            target.setReminderTitles(source.getReminders().stream()
                    .map(ClientAppointmentReminder::getDisplayName).collect(Collectors.toSet()));
        }
        target.setNotificationMethods(source.getNotificationMethods());
        if (CollectionUtils.isNotEmpty(source.getNotificationMethods())) {
            target.setNotificationMethodTitles(source.getNotificationMethods().stream()
                    .map(ClientAppointmentNotificationMethod::getDisplayName).collect(Collectors.toSet()));
        }
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setIsExternalProviderServiceProvider(source.getIsExternalProviderServiceProvider());
        boolean canEdit = BooleanUtils.isFalse(source.getArchived()) && clientAppointmentSecurityService.canEdit(source.getId());
        target.setCanEdit(canEdit && clientAppointmentService.validForEdit(source.getStatus()));
        target.setCanCancel(canEdit && clientAppointmentService.validForCancel(source.getStatus(), source.getDateTo()));
        target.setCanDuplicate(clientAppointmentService.validForDuplicate(source.getStatus()) && BooleanUtils.isFalse(source.getArchived()));
        target.setCanComplete(clientAppointmentSecurityService.canComplete(source.getId()) && BooleanUtils.isFalse(source.getArchived()));
        return target;
    }

}
