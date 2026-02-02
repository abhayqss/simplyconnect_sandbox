package com.scnsoft.eldermark.converter.incident;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.incident.ClassMemberTypeDao;
import com.scnsoft.eldermark.dao.incident.RaceDao;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IncidentReportDtoWrapper;
import com.scnsoft.eldermark.dto.IndividualDto;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.incident.IncidentReportIncidentTypeFreeText;
import com.scnsoft.eldermark.services.IncidentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class IncidentReportDtoWrapperToEntityConverter implements Converter<IncidentReportDtoWrapper, IncidentReport> {

    @Autowired
    private IndividualListDtoToEntityConverter individualListDtoToEntityConverter;

    @Autowired
    private IncidentPlaceDtoToEntityConverter incidentPlaceDtoToEntityConverter;

    @Autowired
    private IncidentTypeDtoToEntityConverter incidentTypeDtoToEntityConverter;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private RaceDao raceDao;

    @Autowired
    private ClassMemberTypeDao classMemberTypeDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private EventDao eventDao;

    @Override
    public IncidentReport convert(IncidentReportDtoWrapper incidentReportWithEventIdDto) {
        final IncidentReportDto source = incidentReportWithEventIdDto.getIncidentReportDto();
        final IncidentReport target;
        target = new IncidentReport();
        if (source.getId() != null) {
            target.setId(source.getId());
            IncidentReport previous = incidentReportService.find(source.getId());
            copyEventData(target, previous);
        } else {
            fillEventData(target, incidentReportWithEventIdDto.getEventId());
        }
        target.setEmployee(incidentReportWithEventIdDto.getEmployee());
        target.setClassMemberType(classMemberTypeDao.getOne(source.getClientClassMemberTypeId()));
        target.setRin(source.getClientRIN());
        target.setBirthDate(source.getClientBirthDate() != null ? new Date(source.getClientBirthDate()) : null);
        target.setGender(ccdCodeDao.getReference(source.getClientGenderId()));
        target.setRace(raceDao.getOne(source.getClientRaceId()));
        target.setTransitionToCommunityDate(source.getClientTransitionToCommunityDate() != null ? new Date(source.getClientTransitionToCommunityDate()) : null);
        target.setClassMemberCurrentAddress(source.getClientClassMemberCurrentAddress());
        target.setAgencyName(source.getAgencyName());
        target.setAgencyAddress(source.getAgencyAddress());
        target.setQualityAdministrator(source.getQualityAdministrator());
        target.setCareManagerOrStaffWithPrimServRespAndTitle(source.getCareManagerOrStaffWithPrimServRespAndTitle());
        target.setCareManagerOrStaffPhone(source.getCareManagerOrStaffPhone());
        target.setCareManagerOrStaffEmail(source.getCareManagerOrStaffEmail());
        target.setMcoCareCoordinatorAndAgency(source.getMcoCareCoordinatorAndAgency());
        target.setMcoCareCoordinatorPhone(source.getMcoCareCoordinatorPhone());
        target.setMcoCareCoordinatorEmail(source.getMcoCareCoordinatorEmail());
        target.setIncidentDatetime(source.getIncidentDateTime() != null ? new Date(source.getIncidentDateTime()) : null);
        target.setIncidentDiscoveredDate(source.getIncidentDiscoveredDate() != null ? new Date(source.getIncidentDiscoveredDate()) : null);
        target.setWasIncidentCausedBySubstance(source.getWasIncidentCausedBySubstance());
        target.setWasProviderPresentOrScheduled(source.getWasProviderPresentOrScheduled());
        target.setNarrative(source.getIncidentNarrative());
        target.setAgencyResponseToIncident(source.getAgencyResponseToIncident());
        target.setReportAuthor(source.getReportAuthor());
        target.setReportCompletedDate(source.getReportCompletedDate() != null ? new Date(source.getReportCompletedDate()) : null);
        target.setReportDate(source.getReportDate() != null ? new Date(source.getReportDate()) : null);

        List<IndividualDto> individuals = source.getIncidentInvolvedIndividuals();
        if (individuals != null && !source.getIncidentInvolvedIndividuals().isEmpty()) {
            target.setIndividuals(individualListDtoToEntityConverter.convertList(source.getIncidentInvolvedIndividuals(), target));
        }

        target.setIncidentPlaceTypes(incidentPlaceDtoToEntityConverter.convertList(source.getIncidentPlaces(), target));
        List<IncidentReportIncidentTypeFreeText> incidentTypesList = new ArrayList<>();
        incidentTypesList.addAll(incidentTypeDtoToEntityConverter.convertList(source.getLevel1IncidentTypes(), target));
        incidentTypesList.addAll(incidentTypeDtoToEntityConverter.convertList(source.getLevel2IncidentTypes(), target));
        incidentTypesList.addAll(incidentTypeDtoToEntityConverter.convertList(source.getLevel3IncidentTypes(), target));

        target.setIncidentTypes(incidentTypesList);
        return target;
    }

    private void copyEventData(IncidentReport target, IncidentReport previous) {
        target.setEvent(previous.getEvent());
        target.setFirstName(previous.getFirstName());
        target.setMiddleName(previous.getMiddleName());
        target.setLastName(previous.getLastName());
    }

    private void fillEventData(IncidentReport target, Long eventId) {
        final Event event = eventDao.get(eventId);
        target.setEvent(event);
        target.setFirstName(event.getResident().getFirstName());
        target.setMiddleName(event.getResident().getMiddleName());
        target.setLastName(event.getResident().getLastName());
    }

}
