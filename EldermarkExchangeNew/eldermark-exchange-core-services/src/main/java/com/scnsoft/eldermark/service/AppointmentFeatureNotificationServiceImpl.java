package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentFeatureNotificationServiceImpl implements AppointmentFeatureNotificationService {

    @Autowired
    private ExchangeMailService mailService;
    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Override
    public void send(Organization organization) {
        var appointments = clientAppointmentService.findAllFutureByOrganizationId(organization.getId(), ClientAppointment.class);
        var appointmentPairs = appointments.stream()
                .map(appointment -> {
                    var pairs = new HashSet<Pair<String, String>>();
                    var client = appointment.getClient();
                    var clientEmail = PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.EMAIL).orElse(null);
                    if (StringUtils.isNotBlank(clientEmail)) {
                        pairs.add(Pair.of(clientEmail, client.getFullName()));
                    }
                    if (CollectionUtils.isEmpty(appointment.getServiceProviders()) && CollectionUtils.isEmpty(appointment.getServiceProviderIds())) {
                        var creator = appointment.getCreator();
                        var creatorEmail = PersonTelecomUtils.findValue(creator.getPerson(), PersonTelecomCode.EMAIL).orElse(null);
                        if (StringUtils.isNotBlank(creatorEmail)) {
                            pairs.add(Pair.of(creatorEmail, creator.getFullName()));
                        }
                    } else {
                        appointment.getServiceProviders()
                                .forEach(serviceProvider -> {
                                    var serviceProviderEmail = PersonTelecomUtils.findValue(serviceProvider.getPerson(), PersonTelecomCode.EMAIL).orElse(null);
                                    if (StringUtils.isNotBlank(serviceProviderEmail)) {
                                        pairs.add(Pair.of(serviceProviderEmail, serviceProvider.getFullName()));
                                    }
                                });
                    }
                    return pairs;
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        appointmentPairs.forEach(appointmentPair ->
                        mailService.sendAppointmentFeatureDisableNotification(appointmentPair.getFirst(), appointmentPair.getSecond(), organization.getName())
        );
    }
}
