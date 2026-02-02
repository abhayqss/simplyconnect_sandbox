package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.incident.*;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Component
@Transactional
public class IncidentReportEntityConverter implements Converter<IncidentReportDtoWrapper, IncidentReport> {

    @Autowired
    private IncidentReportEntityListConverter<IncidentIndividualDto, Individual> incidentIndividualEntityListConverter;

    @Autowired
    private IncidentReportEntityListConverter<TextDto, IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypeFreeTextEntityListConverter;

    @Autowired
    private IncidentReportEntityListConverter<TextDto, IncidentWeatherConditionTypeFreeText> incidentWeatherConditionEntityListConverter;

    @Autowired
    private IncidentReportEntityListConverter<IncidentWitnessDto, IncidentWitness> incidentWitnessEntityListConverter;

    @Autowired
    private IncidentReportEntityListConverter<MultipartFile, IncidentPicture> incidentPictureEntityConverter;

    @Autowired
    private Converter<IncidentVitalSignsDto, IncidentVitalSigns> incidentVitalSignsEntityConverter;

    @Autowired
    private IncidentNotificationsEntityListConverter incidentNotificationsEntityListConverter;

    @Autowired
    private IncidentReportEntityListConverter<CoordinatesDto, IncidentInjury> incidentInjuryEntityListConverter;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private EventService eventService;

    @Override
    public IncidentReport convert(IncidentReportDtoWrapper incidentReportWithEventIdDto) {
        final IncidentReportDto source = incidentReportWithEventIdDto.getIncidentReportDto();
        final IncidentReport target;
        target = new IncidentReport();
        if (source.getId() != null) {
            target.setId(source.getId());
            IncidentReport previous = incidentReportService.findById(source.getId());
            validateStatus(previous, IncidentReportStatus.valueOf(source.getStatusName()));
            copyEventData(target, previous);
            target.setTwilioConversationSid(previous.getTwilioConversationSid());
        } else {
            fillEventData(target, incidentReportWithEventIdDto.getEventId());
        }
        target.setEmployee(incidentReportWithEventIdDto.getEmployee());
        fillClientData(target, source.getClient());
        target.setIncidentDatetime(DateTimeUtils.toInstant(source.getIncidentDate()));
        target.setIncidentDiscoveredDate(DateTimeUtils.parseDateToLocalDate((source.getIncidentDiscoveredDate())));
        target.setWasProviderPresentOrScheduled(source.getWasProviderPresentOrScheduled());
        target.setNarrative(source.getIncidentDetails());
        target.setReportAuthor(source.getCompletedBy());
        target.setReportAuthorTitle(source.getCompletedByPosition());
        target.setReportAuthorPhone(source.getCompletedByPhone());
        target.setReportCompletedDate(DateTimeUtils.toInstant(source.getCompletedDate()));
        target.setReportDate(DateTimeUtils.toInstant(source.getReportDate()));
        target.setReportedBy(source.getReportedBy());
        target.setReportedByTitle(source.getReportedByPosition());
        target.setReportedByPhone(source.getReportedByPhone());
        target.setWereOtherIndividualsInvolved(source.getWereOtherIndividualsInvolved());
        target.setIndividuals(incidentIndividualEntityListConverter.convertList(source.getInvolvedIndividuals(), target));
        target.setIncidentPlaceTypes(incidentPlaceTypeFreeTextEntityListConverter.convertList(source.getPlaces(), target));
        target.setIncidentWeatherConditionTypes(incidentWeatherConditionEntityListConverter.convertList(source.getWeatherConditions(), target));
        target.setWasIncidentParticipantTakenToHospital(source.getWasIncidentParticipantTakenToHospital());
        target.setIncidentParticipantHospitalName(source.getIncidentParticipantHospitalName());
        target.setWitnesses(incidentWitnessEntityListConverter.convertList(source.getWitnesses(), target));
        target.setPictures(incidentPictureEntityConverter.convertList(source.getIncidentPictureFiles(), target));
        target.setWereApparentInjuries(source.getWereApparentInjuries());
        target.setIncidentInjuries(incidentInjuryEntityListConverter.convertList(source.getInjuries(), target));
        target.setInjuredClientCondition(source.getCurrentInjuredClientCondition());
        var vitalSigns = incidentVitalSignsEntityConverter.convert(source.getVitalSigns());
        if (vitalSigns != null) {
            vitalSigns.setIncidentReport(target);
        }
        target.setVitalSigns(vitalSigns);
        target.setNotifications(incidentNotificationsEntityListConverter.convertToList(source.getNotification(), target));
        target.setImmediateIntervention(source.getImmediateIntervention());
        target.setFollowUpInformation(source.getFollowUpInformation());
        return target;
    }

    private void validateStatus(IncidentReport previous, IncidentReportStatus curStatus) {
        var prevStatus = Optional.ofNullable(previous.getStatus()).orElseGet(() -> previous.getSubmitted() ? IncidentReportStatus.SUBMITTED : IncidentReportStatus.DRAFT);
        if (IncidentReportStatus.SUBMITTED == prevStatus && IncidentReportStatus.SUBMITTED != curStatus) {
            throw new ValidationException("Invalid report status");
        }
    }

    private void copyEventData(IncidentReport target, IncidentReport previous) {
        target.setEvent(previous.getEvent());
        target.setFirstName(previous.getFirstName());
        target.setMiddleName(previous.getMiddleName());
        target.setLastName(previous.getLastName());
    }

    private void fillEventData(IncidentReport target, Long eventId) {
        final Event event = eventService.findById(eventId);
        target.setEvent(event);
        target.setFirstName(event.getClient().getFirstName());
        target.setMiddleName(event.getClient().getMiddleName());
        target.setLastName(event.getClient().getLastName());
    }

    private void fillClientData(IncidentReport target, IncidentClientDto source) {
        if (source == null) {
            return;
        }
        target.setUnitNumber(source.getUnit());
        target.setClientPhone(source.getPhone());
        target.setSiteName(source.getSiteName());
        target.setClassMemberCurrentAddress(source.getAddress());
    }
}
