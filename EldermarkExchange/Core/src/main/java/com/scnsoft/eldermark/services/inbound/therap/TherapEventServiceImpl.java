package com.scnsoft.eldermark.services.inbound.therap;

import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapEventCSV;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.services.consana.EventCreatedQueueProducer;
import com.scnsoft.eldermark.services.converters.inbound.TherapCsvEventConverterImpl;
import com.scnsoft.eldermark.services.exceptions.TherapBusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapEventServiceImpl implements TherapEventService {

    private static final Logger logger = LoggerFactory.getLogger(TherapEventServiceImpl.class);

    private final OrganizationService organizationService;
    private final CommunityCrudService communityService;
    private final CareCoordinationResidentService careCoordinationResidentService;
    private final EventService eventService;
    private final EventDao eventDao;
    private final EventNotificationService eventNotificationService;
    private final TherapCsvEventConverterImpl therapCsvEventConverter;
    private final EventCreatedQueueProducer eventCreatedQueueProducer;

    @Autowired
    public TherapEventServiceImpl(OrganizationService organizationService,
                                  CommunityCrudService communityService,
                                  CareCoordinationResidentService careCoordinationResidentService,
                                  EventService eventService,
                                  EventDao eventDao, EventNotificationService eventNotificationService, TherapCsvEventConverterImpl therapCsvEventConverter, EventCreatedQueueProducer eventCreatedQueueProducer) {
        this.organizationService = organizationService;
        this.communityService = communityService;
        this.careCoordinationResidentService = careCoordinationResidentService;
        this.eventService = eventService;
        this.eventDao = eventDao;
        this.eventNotificationService = eventNotificationService;
        this.therapCsvEventConverter = therapCsvEventConverter;
        this.eventCreatedQueueProducer = eventCreatedQueueProducer;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Event> createEvents(TherapEventCSV eventCSV, Long idfResidentId) {
        validateInput(eventCSV);

        Long organizationId;
        List<CareCoordinationResident> residents;
        if (idfResidentId == null) {
            logger.info("Resident by idfFormId [{}] not found, creating new", eventCSV.getIdFormId());

            organizationId = organizationService.getOrCreateOrganizationFromSchema(therapCsvEventConverter.convertToOrganizationSchema(eventCSV));
            Long communityId = communityService.getOrCreateCommunityFromSchema(organizationId, therapCsvEventConverter.convertToCommunitySchema(eventCSV));
            residents = careCoordinationResidentService.getOrCreateResident(communityId, therapCsvEventConverter.convertToPatientSchema(eventCSV));
        } else {
            residents = Collections.singletonList(careCoordinationResidentService.get(idfResidentId));
            organizationId =  residents.get(0).getDatabaseId();

            logger.info("Resident by idfFormId [{}] found, resident id = [{}]", eventCSV.getIdFormId(), idfResidentId);
        }

        final List<Event> resultEvents = new ArrayList<>();
        for (CareCoordinationResident resident : residents) {
            if (organizationId.equals(resident.getDatabase().getId())) {
                Event eventEntity = therapCsvEventConverter.convertToEvent(eventCSV, resident);
                eventEntity = eventDao.create(eventEntity);
                resultEvents.add(eventEntity);
                logger.info("Event with id=[{}] was created for therap event with GERFORMID=[{}]", eventEntity.getId(), eventCSV.getGerFormId());
                eventNotificationService.createNotifications(eventEntity, eventService.getEventDetails(eventEntity.getId()));
                eventCreatedQueueProducer.putToEventCreatedQueue(eventEntity.getId());
            } else {
                //this can happen because careCoordinationResidentService.getOrCreateResident also returns merged patients
                //and they can reside in any organization
                logger.warn("Attempt to create event for resident in different organization than specified. Provided [{}], but resident [{}] has [{}]",
                        organizationId, resident.getId(), resident.getDatabaseId());
            }
        }

        return resultEvents;
    }

    private void validateInput(TherapEventCSV eventCSV) {
        if (StringUtils.isEmpty(eventCSV.getOrganizationName())) {
            throw new TherapBusinessException("ORGANISATIONNAME is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getOrganizationId())) {
            throw new TherapBusinessException("ORGANISATIONID is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getCommunityName())) {
            throw new TherapBusinessException("COMMUNITYNAME is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getCommunityId())) {
            throw new TherapBusinessException("COMMUNITYID is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getIndividualFirstname())) {
            throw new TherapBusinessException("INDIVIDUALFIRSTNAME is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getIndividualLastname())) {
            throw new TherapBusinessException("INDIVIDUALLASTNAME is missing");
        }
        if (eventCSV.getIndividualDateOfBirth() == null) {
            throw new TherapBusinessException("INDIVIDUALDATEOFBIRTH is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getIndividualGenger())) {
            throw new TherapBusinessException("INDIVIDUALGENDER is missing");
        }
        if (StringUtils.isEmpty(eventCSV.getIndividualSsn())) {
            throw new TherapBusinessException("INDIVIDUALSSN is missing");
        }
        if (eventCSV.getEventType() == null) {
            throw new TherapBusinessException("EVENTTYPE is missing");
        }
        if (eventCSV.getErVisit() == null) {
            throw new TherapBusinessException("ERVISIT is missing");
        }
        if (eventCSV.getOvernightInPatient() == null) {
            throw new TherapBusinessException("OVERNIGHTINPATIENT is missing");
        }
    }
    
}
