package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.dictionary.TextDto;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
import com.scnsoft.eldermark.entity.incident.IncidentType;
import com.scnsoft.eldermark.services.IncidentReportAdditionalDataService;
import com.scnsoft.eldermark.services.IncidentReportService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.incident.IncidentPlaceTypeService;
import com.scnsoft.eldermark.services.incident.IncidentTypeService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IncidentReportInitializer {

    @Autowired
    private EventService eventService;

    @Autowired
    private IncidentPlaceTypeService incidentPlaceTypeService;

    @Autowired
    private IncidentTypeService incidentTypeService;

    @Autowired
    private IncidentReportAdditionalDataService incidentReportAdditionalDataService;

    @Autowired
    private IncidentReportService incidentReportService;

    private static final String INCIDENT_PLACE_TYPE_OTHER_NAME = "Other (specify)";
    private static final Integer INCIDENT_TYPE_INJURY_LEVEL = 2;
    private static final String INCIDENT_TYPE_INJURY_NAME = "Injury";
    private static final Map<String, Pair<Integer, String>> eventTypesToIncidentTypes = new HashMap<String, Pair<Integer, String>>() {
        {
            put("ER Visit", new Pair<>(2, "Unexpected hospital visit/admission"));
            put("Hospitalization", new Pair<>(2, "Medical hospitalization"));
            put("Serious injury", new Pair<>(2, "Injury"));
            put("Fire or event that requires relocation of services for more than 24 hours", new Pair<>(2, "Fire"));
            put("Physical aggression toward another resulting in pain, injury or emotional distress",
                    new Pair<>(3, "Physical violence/aggression"));
            put("Suspected abuse", new Pair<>(1, "Suspected mistreatment (abuse, neglect)"));
            put("Suspected exploitation of a vulnerable adult",
                    new Pair<>(1, "Suspected mistreatment (abuse, neglect)"));
        }
    };

    public IncidentReportDto initIncidentReport(Long eventId, Employee employee) {
        IncidentReportDto incidentReportDto = new IncidentReportDto();
        Event event = eventService.getById(eventId);

        String careTeamRole = incidentReportService.getResidentCareTeamMemberRoleByEmployeeIdAndResidentId
                (employee.getId(), event.getResident().getId());
        if (careTeamRole == null) {
            careTeamRole = incidentReportService.getCommunityCareTeamMemberRoleByEmployeeId(employee.getId(), employee.getCommunityId());
        }
        if (careTeamRole == null) {
            careTeamRole = employee.getCareTeamRole().getName();
        }
        incidentReportDto.setReportAuthor(employee.getFullName() + ", " + careTeamRole);
        CareCoordinationResident resident = event.getResident();

        incidentReportDto.setClientName(resident.getFirstName() + " " + resident.getLastName());
        incidentReportDto
                .setClientBirthDate(resident.getBirthDate() != null ? resident.getBirthDate().getTime() : null);
        incidentReportDto.setClientGenderId(resident.getGender() != null ? resident.getGender().getId() : null);
        setClassMemberAddress(incidentReportDto, resident);
        incidentReportDto
                .setIncidentDateTime(event.getEventDatetime() != null ? event.getEventDatetime().getTime() : null);
        setIncidentType(incidentReportDto, event);
        setLocation(incidentReportDto, event);
        incidentReportDto.setIncidentNarrative(
                buildNarrative(event.getSituation(), event.getBackground(), event.getAssessment()));
        setInjury(incidentReportDto, event);
        setMedications(incidentReportDto, resident);
        setDiagnoses(incidentReportDto, resident);

        return incidentReportDto;
    }

    private void setClassMemberAddress(IncidentReportDto incidentReportDto, CareCoordinationResident resident) {
        if (CollectionUtils.isNotEmpty(resident.getPerson().getAddresses())) {
            PersonAddress address = resident.getPerson().getAddresses().get(0);
            StringBuilder classMemberAddress = new StringBuilder();
            classMemberAddress
                    .append(StringUtils.isNotBlank(address.getStreetAddress()) ? address.getStreetAddress() + " " : "");
            classMemberAddress.append(StringUtils.isNotBlank(address.getCity()) ? address.getCity() + " " : "");
            classMemberAddress.append(StringUtils.isNotBlank(address.getState()) ? address.getState() + " " : "");
            classMemberAddress
                    .append(StringUtils.isNotBlank(address.getPostalCode()) ? address.getPostalCode() + " " : "");
            incidentReportDto.setClientClassMemberCurrentAddress(classMemberAddress.toString());
        }
    }

    private void setIncidentType(IncidentReportDto incidentReportDto, Event event) {
        String eventTypeDescription = event.getEventType().getDescription();
        if (eventTypesToIncidentTypes.containsKey(eventTypeDescription)) {
            Pair<Integer, String> incidentTypeToCheck = eventTypesToIncidentTypes.get(eventTypeDescription);
            IncidentType incidentType = incidentTypeService.getByIncidentLevelAndName(incidentTypeToCheck.getFirst(),
                    incidentTypeToCheck.getSecond());
            List<TextDto> incidentTypeDtos = new ArrayList<>();
            TextDto incidentTypeDto = new TextDto();
            incidentTypeDto.setId(incidentType.getId());
            incidentTypeDtos.add(incidentTypeDto);
            switch (incidentTypeToCheck.getFirst()) {
                case 1:
                    incidentReportDto.setLevel1IncidentTypes(incidentTypeDtos);
                    break;
                case 2:
                    incidentReportDto.setLevel2IncidentTypes(incidentTypeDtos);
                    break;
                case 3:
                    incidentReportDto.setLevel3IncidentTypes(incidentTypeDtos);
                    break;
            }
        }
    }

    private void setLocation(IncidentReportDto incidentReportDto, Event event) {
        if (StringUtils.isNotBlank(event.getLocation())) {
            IncidentPlaceType incidentPlaceType = incidentPlaceTypeService.getByName(INCIDENT_PLACE_TYPE_OTHER_NAME);
            List<TextDto> incidentPlaceTypesDtos = new ArrayList<>();
            TextDto incidentPlaceTypeDto = new TextDto();
            incidentPlaceTypeDto.setId(incidentPlaceType.getId());
            incidentPlaceTypeDto.setText(event.getLocation());
            incidentPlaceTypesDtos.add(incidentPlaceTypeDto);
            incidentReportDto.setIncidentPlaces(incidentPlaceTypesDtos);
        }
    }

    private void setInjury(IncidentReportDto incidentReportDto, Event event) {
        if (event.getIsInjury()) {
            IncidentType incidentType = incidentTypeService.getByIncidentLevelAndName(INCIDENT_TYPE_INJURY_LEVEL,
                    INCIDENT_TYPE_INJURY_NAME);
            List<TextDto> incidentTypesLevel2Dtos = incidentReportDto.getLevel2IncidentTypes();
            if (incidentTypesLevel2Dtos == null) {
                incidentTypesLevel2Dtos = new ArrayList<>();
                incidentReportDto.setLevel2IncidentTypes(incidentTypesLevel2Dtos);
            }
            TextDto incidentTypeDto = new TextDto();
            incidentTypeDto.setId(incidentType.getId());
            incidentTypesLevel2Dtos.add(incidentTypeDto);
        }
    }

    private void setMedications(IncidentReportDto incidentReportDto, CareCoordinationResident resident) {
        List<String> medicationStrings = incidentReportAdditionalDataService.listMedicationStrings(resident.getId());
        incidentReportDto.setActiveMedications(medicationStrings);
    }

    private void setDiagnoses(IncidentReportDto incidentReportDto, CareCoordinationResident resident) {
        List<String> diagnosesStrings = incidentReportAdditionalDataService.listProblemObservationStrings(resident.getId());
        incidentReportDto.setCurrentDiagnoses(diagnosesStrings);
    }

    private String buildNarrative(String situation, String background, String assessment) {
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
