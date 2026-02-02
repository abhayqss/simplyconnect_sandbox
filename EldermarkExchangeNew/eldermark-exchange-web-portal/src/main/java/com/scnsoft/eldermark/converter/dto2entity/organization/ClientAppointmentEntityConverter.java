package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.client.appointment.ClientAppointmentDto;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientAppointmentEntityConverter implements Converter<ClientAppointmentDto, ClientAppointment> {

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public ClientAppointment convert(ClientAppointmentDto source) {
        ClientAppointment target = new ClientAppointment();
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setStatus(source.getStatus());
        target.setIsPublic(source.getIsPublic());
        target.setLocation(source.getLocation());
        target.setType(source.getType());
        target.setServiceCategory(source.getServiceCategory());
        target.setReferralSource(source.getReferralSource());
        target.setReasonForVisit(source.getReasonForVisit());
        target.setDirectionsInstructions(source.getDirectionsInstructions());
        target.setNotes(source.getNotes());
        target.setClientId(source.getClientId());
        target.setClient(clientService.getById(source.getClientId()));
        target.setCreator(loggedUserService.getCurrentEmployee());
        target.setCreatorId(loggedUserService.getCurrentEmployeeId());
        target.setServiceProviders(
                !CollectionUtils.isEmpty(source.getServiceProviderIds())
                        ? source.getServiceProviderIds().stream().map(id -> employeeService.getEmployeeById(id)).collect(Collectors.toList())
                        : null
        );
        target.setDateFrom(DateTimeUtils.toInstant(source.getDateFrom()));
        target.setDateTo(DateTimeUtils.toInstant(source.getDateTo()));
        target.setReminders(source.getReminders());
        target.setNotificationMethods(source.getNotificationMethods());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setIsExternalProviderServiceProvider(source.getIsExternalProviderServiceProvider());
        return target;
    }

}
