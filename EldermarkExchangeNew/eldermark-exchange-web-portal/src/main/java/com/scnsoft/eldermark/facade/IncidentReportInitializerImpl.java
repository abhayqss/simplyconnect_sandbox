package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.IncidentClientDto;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.TextDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.IncidentPlaceTypeService;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class IncidentReportInitializerImpl implements IncidentReportInitializer {

    @Autowired
    private EventService eventService;

    @Autowired
    private IncidentPlaceTypeService incidentPlaceTypeService;

    @Autowired
    private IncidentReportService incidentReportService;

    private static final String INCIDENT_PLACE_TYPE_OTHER_NAME = "Other (specify)";

    @Override
    public IncidentReportDto initIncidentReport(Long eventId, Employee employee) {
        Event event = eventService.findById(eventId);

        var incidentReportDto = new IncidentReportDto();
        incidentReportDto.setCompletedBy(employee.getFullName());
        incidentReportDto.setCompletedByPosition(getCareTeamRole(employee, event));
        incidentReportDto.setCompletedByPhone(getPhone(employee.getPerson()));
        incidentReportDto.setCompletedDate(DateTimeUtils.toEpochMilli(Instant.now()));
        incidentReportDto.setClient(createIncidentClientDto(event.getClient()));
        incidentReportDto.setIncidentDate(event.getEventDateTime() != null ? event.getEventDateTime().toEpochMilli() : null);
        incidentReportDto.setPlaces(createIncidentPlaceTypeDtos(event));
        incidentReportDto.setIncidentDetails(createDetails(event.getSituation(), event.getBackground(), event.getAssessment()));
        incidentReportDto.setEventId(eventId);

        return incidentReportDto;
    }

    private String getCareTeamRole(Employee employee, Event event) {
        String careTeamRole = incidentReportService.getResidentCareTeamMemberRoleByEmployeeIdAndResidentId
                (employee.getId(), event.getClientId());
        if (careTeamRole == null) {
            careTeamRole = incidentReportService.getCommunityCareTeamMemberRoleByEmployeeId(employee.getId(), employee.getCommunityId());
        }
        if (careTeamRole == null) {
            careTeamRole = employee.getCareTeamRole().getName();
        }
        return careTeamRole;
    }

    private IncidentClientDto createIncidentClientDto(Client client) {
        var clientDto = new IncidentClientDto();
        clientDto.setFullName(client.getFullName());
        clientDto.setUnit(client.getUnitNumber());
        clientDto.setPhone(getPhone(client.getPerson()));
        clientDto.setSiteName(client.getCommunity() != null ? client.getCommunity().getName() : null);
        clientDto.setAddress(getAddress(client.getCommunity().getAddresses()));
        return clientDto;
    }

    private String getPhone(Person person) {
        return PersonTelecomUtils.findValue(person, PersonTelecomCode.MC)
                .orElseGet(() -> PersonTelecomUtils.findValue(person, PersonTelecomCode.HP).orElse(null));
    }

    private String getAddress(List<CommunityAddress> addresses) {
        return Stream.ofNullable(addresses)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .findFirst()
                .map(addr -> addr.getDisplayAddress(", "))
                .orElse(null);
    }

    private List<TextDto> createIncidentPlaceTypeDtos(Event event) {
        if (StringUtils.isNotBlank(event.getLocation())) {
            IncidentPlaceType incidentPlaceType = incidentPlaceTypeService.getByName(INCIDENT_PLACE_TYPE_OTHER_NAME);
            TextDto incidentPlaceTypeDto = new TextDto();
            incidentPlaceTypeDto.setId(incidentPlaceType.getId());
            incidentPlaceTypeDto.setText(event.getLocation());
            return List.of(incidentPlaceTypeDto);
        }
        return null;
    }

    private String createDetails(String situation, String background, String assessment) {
        StringBuilder result = new StringBuilder();
        appendIfNotBlankWithNewLine(result, "Situation", situation);
        appendIfNotBlankWithNewLine(result, "Background", background);
        appendIfNotBlankWithNewLine(result, "Assessment", assessment);
        return result.toString();
    }

    private void appendIfNotBlankWithNewLine(StringBuilder sb, String label, String value) {
        if (StringUtils.isNotBlank(value)) {
            sb.append(label);
            sb.append(": ");
            sb.append(value);
            sb.append(System.lineSeparator());
        }
    }

}
