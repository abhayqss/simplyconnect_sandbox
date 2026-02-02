package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0009AmbulatoryStatus;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0136YesNoIndicator;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.message.*;
import com.scnsoft.eldermark.entity.xds.segment.*;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.schema.Address;
import com.scnsoft.eldermark.schema.*;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.IncidentReportService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.adt.ProcessAdtService;
import com.scnsoft.eldermark.services.consana.EventCreatedQueueProducer;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.populator.Populator;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLNDriverSLicenseNumberDto;
import com.scnsoft.eldermark.shared.carecoordination.careteam.*;
import com.scnsoft.eldermark.shared.carecoordination.events.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.events.*;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.NHINIoException;
import com.scnsoft.eldermark.xds.XdsRegistryConnectorService;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.util.*;

/**
 * @author averazub
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 25-Sep-15.
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    private EnumMap<AdtTypeEnum, EventType> EVENT_TRIGGERING_ADT_TYPES;

    @Autowired
    private EventDao eventDao;
    @Autowired
    private AdtMessageDao adtMessageDao;
    @Autowired
    private AdtMessageCustomDao adtMessageCustomDao;
    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private CareTeamRoleDao careTeamRoleDao;
    @Autowired
    private EventNotificationService eventNotificationService;
    @Autowired
    private CareCoordinationResidentService careCoordinationResidentService;
    @Autowired
    private CareCoordinationResidentDao careCoordinationResidentDao;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityCrudService communityService;

    private final Marshaller eventsMarshaller;

    @Autowired
    XdsRegistryConnectorService xdsRegistryConnectorService;

    @Autowired
    private StateService stateService;

    @Autowired
    private PatientFacade patientFacade;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    private ResidentMatcherService residentMatcherService;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ResidentDeviceService residentDeviceService;

    @Autowired
    private EmployeeService employeeService;


    @Autowired
    public EventServiceImpl(Marshaller eventsMarshaller) {
        this.eventsMarshaller = eventsMarshaller;
    }

    @Autowired
    private ProcessAdtService processAdtService;

    @Autowired
    private Populator<AdtMessage, EventDto> adtMessagePopulator;

    @Autowired
    private Converter<CECodedElement, String> ceCodedElementStringConverter;

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Autowired
    private Converter<IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator>, Boolean> idCodedValueForHL7TablesBooleanConverter;

    @Autowired
    private ListAndItemTransformer<XPNPersonName, String> xpnPersonNameStringTransformer;

    @Autowired
    private Converter<DLNDriverSLicenseNumber, DLNDriverSLicenseNumberDto> dlnDriverSLicenseNumberTransformer;

    @Autowired
    private ResidentCareTeamMemberJpaDao residentCareTeamMemberJpaDao;

    @Autowired
    private OrganizationCareTeamMemberJpaDao organizationCareTeamMemberJpaDao;

    @Autowired
    private CareCoordinationResidentJpaDao careCoordinationResidentJpaDao;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private EventCreatedQueueProducer eventCreatedQueueProducer;

    @PostConstruct
    public void postConstruct() {
        EVENT_TRIGGERING_ADT_TYPES = new EnumMap<>(AdtTypeEnum.class);

        final EventType eadt = eventTypeService.getByCode("EADT");
        EVENT_TRIGGERING_ADT_TYPES.put(AdtTypeEnum.A01, eadt);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtTypeEnum.A03, eadt);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtTypeEnum.A04, eadt);

        final EventType pru = eventTypeService.getByCode("PRU");
        EVENT_TRIGGERING_ADT_TYPES.put(AdtTypeEnum.A08, pru);
    }

    @Override
    public Date getEventsMinimumDate(List<Long> residentIds) {
        return eventDao.getEventsMinimumDate(residentIds);
    }


    @Override
    public void processEvents(Events events) {
        for (com.scnsoft.eldermark.schema.Event event : events.getEvent()) {
            processEvent(event);
        }
    }

    @Override
    public Event processManualEvent(EventDto eventDto) {
        Event event = createEvent(eventDto, true);
        eventNotificationService.createNotifications(event, getEventDetails(event.getId()));
        return event;
    }

    @Override
    public Event processAutomaticEvent(EventDto eventDto) {
        Event event = createEvent(eventDto, false);
        eventNotificationService.createNotifications(event, getEventDetails(event.getId()));
        return event;
    }

    @Override
    public void processAdtEvent(AdtDto adtDto, Long adtType) {
        final AdtTypeEnum supportedAdtType = AdtTypeEnum.byCode(adtType);
        if (!EVENT_TRIGGERING_ADT_TYPES.containsKey(supportedAdtType)) {
            logger.warn("Unsupported ADT message type: " + adtType);
            return;
        }
        final Optional<Event> event = createAdtEvent(adtDto, supportedAdtType);
        if (event.isPresent()) {
            eventNotificationService.createNotifications(event.get(), getEventDetails(event.get().getId()));
        } else {
            logger.warn("Event wasn't created for AdtDto {}", adtDto);
        }
    }

    @Override
    public Page<EventListItemDto> list(Set<Long> employeeIds, EventFilterDto eventFilter, Pageable pageRequest) {
        final Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Set<Long> communityIds = new HashSet<>(userDetails.getCurrentCommunityIds());
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = careCoordinationResidentService.getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();

        fixFilterDates(eventFilter);
        long startTime = System.currentTimeMillis();
        long stopTime0 = System.currentTimeMillis();
        //System.out.println("EventServiceImpl.getResidentIdsAndMergedIds:" + (stopTime0 - startTime) + "ms");
        Map<Long, List<Long>> notViewableResidentIdsWithEventTypes = new HashMap<>();
        List<Long> viewableResidentIds = new ArrayList<>();
        Map<Long, List<Long>> communityNotViewables = new HashMap<>();
        if (eventFilter.getPatientId() != null) {
            //single patient
            List<Long> mainResidentIds = new ArrayList<>();
            mainResidentIds.add(eventFilter.getPatientId());
            List<Long> mergedResidentIds = mpiService.listMergedResidents(eventFilter.getPatientId());
            CareCoordinationResident resident = careCoordinationResidentDao.get(eventFilter.getPatientId());
            Set<Long> residentCommunityIds = new HashSet<>();
            residentCommunityIds.add(resident.getFacility().getId());
            if (!isAdmin) {
                communityNotViewables = getCommunityNotViewables(employeeIds, residentCommunityIds);
                Map<Long, List<Long>> mainAndMergedResidentIds = new HashMap<>();
                mainAndMergedResidentIds.put(eventFilter.getPatientId(), mergedResidentIds);
                ResidentCareTeamAccessSettingsVO viewableAccessSettings = getViewableAndNotViewableResidentIds(employeeIds, mainAndMergedResidentIds, communityNotViewables);
                viewableResidentIds = viewableAccessSettings.getViewableResidentIds();
                notViewableResidentIdsWithEventTypes = viewableAccessSettings.getNotViewableResidentIdsWithEventTypes();
            } else {
                viewableResidentIds.addAll(mainResidentIds);
                viewableResidentIds.addAll(mergedResidentIds);
            }
        } else {
            //patient list
            List<Long> mainResidentIds = getResidentIds(employeeIds, isAdmin, databaseId);
            Map<Long, List<Long>> mainAndMergedResidentIds = getMergedIdsGroupedByResidentId(new HashSet<>(mainResidentIds));
            if (!isAdmin) {
                communityNotViewables = getCommunityNotViewablesForDatabase(employeeIds, databaseId);
                ResidentCareTeamAccessSettingsVO viewableAccessSettings = getViewableAndNotViewableResidentIds(employeeIds, mainAndMergedResidentIds, communityNotViewables);
                viewableResidentIds = viewableAccessSettings.getViewableResidentIds();
                notViewableResidentIdsWithEventTypes = viewableAccessSettings.getNotViewableResidentIdsWithEventTypes();
            } else {
                for (Long mainResidentId : mainAndMergedResidentIds.keySet()) {
                    viewableResidentIds.add(mainResidentId);
                    viewableResidentIds.addAll(mainAndMergedResidentIds.get(mainResidentId));
                }
            }
        }
        final List<EventListItemDto> result = new ArrayList<>();
        startTime = System.currentTimeMillis();
        List<EventListItemDbo> events = eventDao.getEventsForEmployee(eventFilter, databaseId, communityIds, pageRequest, isAdmin, viewableResidentIds, notViewableResidentIdsWithEventTypes, employeeCommunityIds);
        stopTime0 = System.currentTimeMillis();
        System.out.println("EventServiceImpl.getEventsForEmployee:" + (stopTime0 - startTime) + "ms");
        for (EventListItemDbo event : events) {

            final EventListItemDto item = new EventListItemDto();
            item.setEventDate(event.getEventDate() != null ? event.getEventDate().getTime() : null);
            logger.info("List Event DateTime:" + event.getEventDate());
            item.setEventId(event.getEventId());
            item.setEventType(event.getEventType());
            item.setResidentName(event.getResidentFirstName() + " " + event.getResidentLastName());
            result.add(item);
        }
        return new PageImpl<>(result, pageRequest, eventDao.getEventsCountForEmployee(eventFilter, databaseId, communityIds, isAdmin, viewableResidentIds, notViewableResidentIdsWithEventTypes, employeeCommunityIds));
    }

    @Override
    public Map<Long, Long> countEventsForEachResidentIdForNonAdminEmployees(Set<Long> employeeIds, Long databaseId, Set<Long> communityIds, Set<Long> employeeCommunityIds) {
        List<Long> mainResidentIds = getResidentIds(employeeIds, Boolean.FALSE, databaseId);
        Map<Long, List<Long>> groupedMergedResidentIds = getMergedIdsGroupedByResidentId(new HashSet<>(mainResidentIds));

        Map<Long, List<Long>> communityNotViewables = getCommunityNotViewablesForDatabase(employeeIds, databaseId);
        ResidentCareTeamAccessSettingsVO viewableAccessSettings = getViewableAndNotViewableResidentIds(employeeIds, groupedMergedResidentIds, communityNotViewables);
        List<Long> viewableResidentIds = viewableAccessSettings.getViewableResidentIds();
        Map<Long, List<Long>> notViewableResidentIdsWithEventTypes = viewableAccessSettings.getNotViewableResidentIdsWithEventTypes();

        Map<Long, Long> allResidentsWithEventCounts = eventDao.getEventsCountGroupedByResidentId(viewableResidentIds, notViewableResidentIdsWithEventTypes, communityIds, employeeIds);
        Map<Long, Long> result = new HashMap<>();
        if (MapUtils.isNotEmpty(groupedMergedResidentIds)) {
            for (Long mainResidentId : groupedMergedResidentIds.keySet()) {
                if (allResidentsWithEventCounts.containsKey(mainResidentId)) {
                    Long totalEventsCount = allResidentsWithEventCounts.get(mainResidentId);
                    for (Long mergedResidentId : groupedMergedResidentIds.get(mainResidentId)) {
                        if (allResidentsWithEventCounts.containsKey(mergedResidentId)) {
                            totalEventsCount += allResidentsWithEventCounts.get(mergedResidentId);
                        }
                    }
                    result.put(mainResidentId, totalEventsCount);
                } else {
                    result.put(mainResidentId, 0l);
                }
            }
        }

        return result;
    }

    private ResidentCareTeamAccessSettingsVO getViewableAndNotViewableResidentIds(Set<Long> employeeIds, Map<Long, List<Long>> mainAndMergedResidentIds, Map<Long, List<Long>> communityNotViewables) {
        Set<Long> allResidentIds = new HashSet<>();
        List<MergedResidentsGroupVO> mergedResidentGroups = new ArrayList<>();
        List<MergedResidentsGroupVO> accessibleGroups = new ArrayList<>();
        for (Long mainResidentId : mainAndMergedResidentIds.keySet()) {
            List<Long> mergedIds = mainAndMergedResidentIds.get(mainResidentId);
            Set<Long> mergedResidentsGroup = new HashSet<>();
            mergedResidentsGroup.add(mainResidentId);
            mergedResidentsGroup.addAll(mergedIds);
            mergedResidentGroups.add(new MergedResidentsGroupVO(mergedResidentsGroup));
            allResidentIds.addAll(mergedResidentsGroup);
        }
        Map<Long, List<Long>> notViewableResidentIdsWithEventTypes = new HashMap<>();
        List<Long> viewableResidentIds = new ArrayList<>();
        List<Long> accessibleResidentIds = new ArrayList<>();
        List<ResidentNotificationEventTypeVO> potentialResidentNotViewables = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allResidentIds)) {
            potentialResidentNotViewables = residentCareTeamMemberJpaDao.getNotViewableByEmployeesEventTypesForResidents(employeeIds, allResidentIds);
        }
        List<ResidentNotificationEventTypeVO> notViewables = new ArrayList<>();
        List<ResidentNotificationEventTypeVO> viewableVo = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allResidentIds)) {
            viewableVo = residentCareTeamMemberJpaDao.getViewableByEmployeesEventTypesForResidents(employeeIds, allResidentIds);
        }
        Map<Long, List<Long>> viewablesGroup = fillGroupedByEntityIdMap(viewableVo);
        Map<Long, List<Long>> notViewablesGroup = fillGroupedByEntityIdMap(potentialResidentNotViewables);

        for (MergedResidentsGroupVO mergedResidentsGroup : mergedResidentGroups) {
            //1. find all viewable and not viewable types for resident and merges
            Set<Long> allViewableEventTypes = new HashSet<>();
            Set<Long> allNotViewableEventTypes = new HashSet<>();
            for (Long residentId : mergedResidentsGroup.getMergedResidentIds()) {
                if (viewablesGroup.containsKey(residentId)) {
                    allViewableEventTypes.addAll(viewablesGroup.get(residentId));
                }
                if (notViewablesGroup.containsKey(residentId)) {
                    allNotViewableEventTypes.addAll(notViewablesGroup.get(residentId));
                }
            }
            if (CollectionUtils.isEmpty(allViewableEventTypes) && CollectionUtils.isEmpty(allNotViewableEventTypes)) {
                //in case there are no specific viewable settings, then viewable settings for this group
                //can be inherited from community care team settings. It is accessible residents
                accessibleResidentIds.addAll(mergedResidentsGroup.getMergedResidentIds());
                accessibleGroups.add(mergedResidentsGroup);
            }
            //2. remove viewable types from not viewable list
            Set<Long> notViewableEventTypes = new HashSet<>(allNotViewableEventTypes);
            notViewableEventTypes.removeAll(allViewableEventTypes);
            //3. apply rest of not viewable types for all merged records in group
            if (CollectionUtils.isNotEmpty(notViewableEventTypes)) {
                for (Long notViewableEventTypeId : notViewableEventTypes) {
                    for (Long residentId : mergedResidentsGroup.getMergedResidentIds()) {
                        notViewables.add(new ResidentNotificationEventTypeVO(residentId, notViewableEventTypeId));
                    }
                }
            }
        }


        //apply community care team notification settings for resident ids that doesn't have explicit settings in resident care team
        if (CollectionUtils.isNotEmpty(accessibleResidentIds) && MapUtils.isNotEmpty(communityNotViewables)) {
            List<CommunityResidentVO> communityResidentsVos = careCoordinationResidentJpaDao.getCommunityIdsByResidentsIds(accessibleResidentIds);
            Map<Long, Long> residentCommunities = fillResidentCommunitiesMap(communityResidentsVos);
            for (MergedResidentsGroupVO accessibleGroup : accessibleGroups) {
                //1. find not viewable for all communities for residents in group
                Set<Long> allNotViewableCommunityEventTypes = new HashSet<>();
                for (Long residentId : accessibleGroup.getMergedResidentIds()) {
                    Long residentCommunity = residentCommunities.get(residentId);
                    if (communityNotViewables.containsKey(residentCommunity)) {
                        allNotViewableCommunityEventTypes.addAll(communityNotViewables.get(residentCommunity));
                    }
                }
                //2. apply not viewable types for all merged records in group
                if (CollectionUtils.isNotEmpty(allNotViewableCommunityEventTypes)) {
                    for (Long notViewableEventTypeId : allNotViewableCommunityEventTypes) {
                        for (Long residentId : accessibleGroup.getMergedResidentIds()) {
                            notViewables.add(new ResidentNotificationEventTypeVO(residentId, notViewableEventTypeId));
                        }
                    }
                }
            }
        }

        notViewableResidentIdsWithEventTypes = fillGroupedByEntityIdMap(notViewables);
        for (Long id : allResidentIds) {
            if (!notViewableResidentIdsWithEventTypes.containsKey(id)) {
                viewableResidentIds.add(id);
            }
        }
        return new ResidentCareTeamAccessSettingsVO(viewableResidentIds, notViewableResidentIdsWithEventTypes);
    }

    private Map<Long, Long> fillResidentCommunitiesMap(List<CommunityResidentVO> communityResidentsVos) {
        Map<Long, Long> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(communityResidentsVos)) {
            for (CommunityResidentVO communityResidentVO : communityResidentsVos) {
                if (!result.containsKey(communityResidentVO.getResidentId())) {
                    result.put(communityResidentVO.getResidentId(), communityResidentVO.getCommunityId());
                }
            }
        }
        return result;
    }

    private Map<Long, List<Long>> getCommunityNotViewables(Set<Long> employeeIds, Set<Long> communityIds) {
        List<CommunityNotificationTypeVO> result = new ArrayList<>();
        List<CommunityNotificationTypeVO> potentialCommunityNotViewables = organizationCareTeamMemberJpaDao.getNotViewableByEmployeesEventTypesForCommunities(employeeIds, communityIds);
        if (CollectionUtils.isNotEmpty(potentialCommunityNotViewables) && employeeIds.size() > 1) {
            List<CommunityNotificationTypeVO> communityViewables = organizationCareTeamMemberJpaDao.getViewableByEmployeesEventTypesForCommunities(employeeIds, communityIds);
            result.addAll(removeViewablesFromNotViewables(potentialCommunityNotViewables, communityViewables));
        } else if (CollectionUtils.isNotEmpty(potentialCommunityNotViewables)) {
            result.addAll(potentialCommunityNotViewables);
        }
        return fillGroupedByEntityIdMap(result);
    }

    private Map<Long, List<Long>> getCommunityNotViewablesForDatabase(Set<Long> employeeIds, Long databaseId) {
        List<CommunityNotificationTypeVO> result = new ArrayList<>();
        List<CommunityNotificationTypeVO> potentialCommunityNotViewables = organizationCareTeamMemberJpaDao.getNotViewableByEmployeesEventTypesForAllCommunitiesInDatabase(employeeIds, databaseId);
        if (CollectionUtils.isNotEmpty(potentialCommunityNotViewables) && employeeIds.size() > 1) {
            List<CommunityNotificationTypeVO> communityViewables = organizationCareTeamMemberJpaDao.getViewableByEmployeesEventTypesForAllCommunitiesInDatabase(employeeIds, databaseId);
            result.addAll(removeViewablesFromNotViewables(potentialCommunityNotViewables, communityViewables));
        } else if (CollectionUtils.isNotEmpty(potentialCommunityNotViewables)) {
            result.addAll(potentialCommunityNotViewables);
        }
        return fillGroupedByEntityIdMap(result);
    }

    private List<CommunityNotificationTypeVO> removeViewablesFromNotViewables(List<CommunityNotificationTypeVO> potentialCommunityNotViewables, List<CommunityNotificationTypeVO> communityViewables) {
        List<CommunityNotificationTypeVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(potentialCommunityNotViewables)) {
            for (CommunityNotificationTypeVO potentialNotViewable : potentialCommunityNotViewables) {
                if (!communityViewables.contains(potentialNotViewable)) {
                    result.add(potentialNotViewable);
                }
            }
        }
        return result;
    }

    private <ENTITY extends EntityEventTypeVO> Map<Long, List<Long>> fillGroupedByEntityIdMap(List<ENTITY> notViewables) {
        Map<Long, List<Long>> result = new HashMap<>();
        if (CollectionUtils.isNotEmpty(notViewables)) {
            for (ENTITY notViewableEventTypesVO : notViewables) {
                if (!result.containsKey(notViewableEventTypesVO.getEntityId())) {
                    result.put(notViewableEventTypesVO.getEntityId(), new ArrayList<Long>());
                }
                result.get(notViewableEventTypesVO.getEntityId()).add(notViewableEventTypesVO.getEventTypeId());
            }
        }
        return result;
    }

    @Override
    public Integer getPageNumber(Long eventId, Set<Long> employeeIds) {
        return getPageNumber(eventId, employeeIds, null);
    }

    @Override
    public Integer getPageNumber(Long eventId, Set<Long> employeeIds, Long residentId) {
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Set<Long> communityIds = new HashSet<Long>(userDetails.getCurrentCommunityIds());
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = careCoordinationResidentService.getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();

        Set<Long> mergedFilterResidentsIds = new HashSet<Long>();
        List<Long> resIds;
        if (residentId == null) {
            resIds = getResidentIdsAndMergedIds(employeeIds, isAdmin, databaseId);
        } else {
            resIds = new ArrayList<Long>();
            resIds.add(residentId);
            mergedFilterResidentsIds.addAll(mpiService.listMergedResidents(residentId));
        }
        return eventDao.getPageNumber(eventId, databaseId, communityIds, isAdmin, resIds, employeeCommunityIds);
    }

    @Override
    public DeviceEventProcessingResultDto processDeviceEvents(DeviceEvents events) {
        DeviceEventProcessingResultDto result = new DeviceEventProcessingResultDto();
        for (DeviceEvent event : events.getEvent()) {
            boolean processed = processDeviceEvent(event);
            if (processed) {
                result.addProcessed(event.getPatient().getDeviceId());
            } else {
                result.addFailed(event.getPatient().getDeviceId());
            }
        }
        return result;
    }

    private List<Long> getResidentIdsAndMergedIds(Set<Long> employeeIds, Boolean isAdmin, Long databaseId) {
        List<Long> resIds = new ArrayList<Long>();
        if (isAdmin) {
            resIds = mpiService.listResidentsAndMergedResidents(databaseId);
        } else {
            List<Long> residentsWithoutMergedIds = residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(employeeIds, databaseId);
            residentsWithoutMergedIds.addAll(organizationCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(employeeIds, databaseId));
            residentsWithoutMergedIds.addAll(careCoordinationResidentDao.getResidentIdsCreatedByEmployeeId(employeeIds, databaseId));
            if (!CollectionUtils.isEmpty(residentsWithoutMergedIds)) {
                for (Long resId : residentsWithoutMergedIds) {
                    resIds.add(resId);
                    resIds.addAll(mpiService.listMergedResidents(resId));
                }
            }
        }

        return resIds;
    }

    private List<Long> getResidentIds(Set<Long> employeeIds, Boolean isAdmin, Long databaseId) {
        List<Long> resIds = new ArrayList<Long>();
        if (isAdmin) {
            resIds = mpiService.listResidentsAndMergedResidents(databaseId);
        } else {
            resIds.addAll(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(employeeIds, databaseId));
            resIds.addAll(organizationCareTeamMemberDao.getCareTeamResidentIdsByEmployeeId(employeeIds, databaseId));
            resIds.addAll(careCoordinationResidentDao.getResidentIdsCreatedByEmployeeId(employeeIds, databaseId));
        }
        return resIds;
    }

    private Map<Long, List<Long>> getMergedIdsGroupedByResidentId(Set<Long> residentsWithoutMergedIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(residentsWithoutMergedIds)) {
            for (Long resId : residentsWithoutMergedIds) {
                result.put(resId, mpiService.listMergedResidents(resId));
            }
        }
        return result;
    }

    @Override
    public void checkAccess(Long eventId) {
        Long databaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Set<Long> employeeIds = userDetails.getEmployeeAndLinkedEmployeeIds();
        Set<Long> communityIds = new HashSet<Long>(userDetails.getCurrentCommunityIds());
        boolean isAdmin = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
        Pair<Boolean, Set<Long>> adminAndEmployeeIds = careCoordinationResidentService.getCommunityAdminEmployeeIds(employeeIds);
        Set<Long> employeeCommunityIds = adminAndEmployeeIds.getSecond();
        isAdmin = isAdmin || adminAndEmployeeIds.getFirst();

        List<Long> resIds = getResidentIdsAndMergedIds(employeeIds, isAdmin, databaseId);

        List<Long> eventIds = eventDao.getEventsIdsForEmployee(databaseId, communityIds, isAdmin, resIds, employeeCommunityIds);
        if (!eventIds.contains(eventId)) {
            throw new BusinessAccessDeniedException("You have no access to specified event description, please contact your Administrator for more details.");
        }
    }

    @Override
    public Events createEvents(final Event entity) {
        final Events result = new Events();
        com.scnsoft.eldermark.schema.Event event = new com.scnsoft.eldermark.schema.Event();

        final Patient patient = new Patient();
        final CareCoordinationResident resident = entity.getResident();

        patient.setName(createPersonName(resident.getFirstName(), resident.getLastName()));
        if (resident.getGender() != null) {
            patient.setGender(resident.getGender().getCode());
        }
        if (resident.getMaritalStatus() != null) {
            patient.setMaritalStatus(resident.getMaritalStatus().getCode());
        }
        patient.setSSN(resident.getSocialSecurity());

        if (resident.getBirthDate() != null) {
            patient.setDateOfBirth(getXmlGregorianCalendar(resident.getBirthDate()));
        }

        event.setPatient(patient);


        final EventDetails eventDetails = new EventDetails();
        eventDetails.setAssessmentNarrative(entity.getAssessment());
        eventDetails.setBackgroundNarrative(entity.getBackground());


        XMLGregorianCalendar eventDatetime = getXmlGregorianCalendar(entity.getEventDatetime());
        eventDetails.setDate(eventDatetime);
        eventDetails.setTime(eventDatetime);

        eventDetails.setType(entity.getEventType().getCode());
        eventDetails.setERVisit(entity.isErVisit());

        final FollowUp followUp = new FollowUp();
        followUp.setIsExpected(entity.isFollowup());
        if (entity.isFollowup()) {
            followUp.setDetails(entity.getFollowup());
        }

        eventDetails.setFollowUp(followUp);
        eventDetails.setLocation(entity.getLocation());
        eventDetails.setOvernightInPatient(entity.isOvernightIn());
        eventDetails.setResultedInInjury(entity.isInjury());
        eventDetails.setSituationNarrative(entity.getSituation());

        if (entity.getEventTreatingHospital() != null) {
            final EventTreatingHospital eventTreatingHospital = entity.getEventTreatingHospital();
            final TreatingHospital treatingHospital = new TreatingHospital();
            if (eventTreatingHospital.getEventAddress() != null) {
                treatingHospital.setAddress(createAddress(eventTreatingHospital.getEventAddress()));
            }
            treatingHospital.setName(eventTreatingHospital.getName());
            treatingHospital.setPhone(eventTreatingHospital.getPhone());

            eventDetails.setTreatingHospital(treatingHospital);
        }

        if (entity.getEventTreatingPhysician() != null) {
            final EventTreatingPhysician eventTreatingPhysician = entity.getEventTreatingPhysician();
            final TreatingPhysician treatingPhysician = new TreatingPhysician();

            treatingPhysician.setName(createPersonName(eventTreatingPhysician.getFirstName(), eventTreatingPhysician.getLastName()));
            if (eventTreatingPhysician.getEventAddress() != null) {
                treatingPhysician.setAddress(createAddress(eventTreatingPhysician.getEventAddress()));
            }
            treatingPhysician.setPhone(eventTreatingPhysician.getPhone());

            eventDetails.setTreatingPhysician(treatingPhysician);
        }

        event.setEventDetails(eventDetails);

        if (entity.getEventAuthor() != null) {
            final FormAuthor formAuthor = new FormAuthor();
            final EventAuthor eventAuthor = entity.getEventAuthor();

            formAuthor.setName(createPersonName(eventAuthor.getFirstName(), eventAuthor.getLastName()));
            formAuthor.setOrganization(eventAuthor.getOrganization());
            formAuthor.setRole(eventAuthor.getRole());

            event.setFormAuthor(formAuthor);
        }

        if (entity.getEventManager() != null) {
            final Manager manager = new Manager();
            final EventManager eventManager = entity.getEventManager();
            manager.setName(createPersonName(eventManager.getFirstName(), eventManager.getLastName()));
            manager.setPhone(eventManager.getPhone());
            manager.setEmail(eventManager.getEmail());

            event.setManager(manager);
        }
        if (entity.getEventRn() != null) {
            final EventRN eventRN = entity.getEventRn();
            final RN rn = new RN();
            rn.setName(createPersonName(eventRN.getFirstName(), eventRN.getLastName()));

            if (eventRN.getEventAddress() != null) {
                rn.getAddress().add(createAddress(eventRN.getEventAddress()));
            }
            event.setRN(rn);
        }

        result.getEvent().add(event);

        return result;
    }

    private XMLGregorianCalendar getXmlGregorianCalendar(Date date) {

        final GregorianCalendar birthDate = new GregorianCalendar();
        birthDate.setTime(date);
        XMLGregorianCalendar birthDateXmlGregorianCalendar = new XMLGregorianCalendarImpl(birthDate);
        birthDateXmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        birthDateXmlGregorianCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        return birthDateXmlGregorianCalendar;
    }

    private PersonName createPersonName(String firstName, String lastName) {
        final PersonName personName = new PersonName();
        personName.setFirstName(firstName);
        personName.setLastName(lastName);
        return personName;
    }

    private Address createAddress(EventAddress eventAddress) {
        Address address = new Address();
        address.setCity(eventAddress.getCity());
        address.setState(eventAddress.getState().getAbbr());
        address.setStreet(eventAddress.getStreet());
        address.setZip(eventAddress.getZip());
        return address;
    }

    @Override
    public EventDto getEventDetailsWithoutNotes(Long eventId) {
        final Event event = eventDao.get(eventId);
        AdtMessage adtMessage = null;
        if (event.getAdtMsgId() != null) {
            adtMessage = adtMessageDao.findOne(event.getAdtMsgId());
        }
        return createEventDto(event, patientFacade.getPatientDto(event.getResident().getId(), false, false),
                adtMessage);
    }

    @Override
    public EventDto getEventDetails(Long eventId) {
        return addNotes(getEventDetailsWithoutNotes(eventId), eventId);
    }

    private EventDto addNotes(EventDto eventDto, Long eventId) {
        final List<RelatedNoteItemDto> notes = noteService.getRelatedEventNotes(eventId);
        if (!CollectionUtils.isEmpty(notes)) {
            eventDto.setRelatedNotes(notes);
        }
        return eventDto;
    }

    public EventDto createEventDto(Event event, PatientDto patient, AdtMessage adtMessage) {
        EventDto result = new EventDto();
        result.setPatient(patient);
        //        result.getPatient().setSsn(event.getResident().getSocialSecurity());

        if (StringUtils.isNotBlank(event.getOrganization())) {
            result.getPatient().setOrganization(event.getOrganization());
        }
        if (StringUtils.isNotBlank(event.getCommunity())) {
            result.getPatient().setCommunity(event.getCommunity());
        }

        final EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName(event.getEventAuthor().getFirstName());
        employeeDto.setLastName(event.getEventAuthor().getLastName());
        employeeDto.setRole(event.getEventAuthor().getRole());

        result.setEmployee(employeeDto);

        final EventDetailsDto details = new EventDetailsDto();
        details.setEventDatetime(event.getEventDatetime());
        details.setEventTypeId(event.getEventType().getId());
        details.setEventType(event.getEventType().getDescription());
        details.setEmergencyVisit(event.isErVisit());
        details.setOvernightPatient(event.isOvernightIn());

        details.setLocation(event.getLocation());
        details.setInjury(event.isInjury());
        details.setSituation(event.getSituation());
        details.setBackground(event.getBackground());
        details.setAssessment(event.getAssessment());
        details.setFollowUpExpected(event.isFollowup());
        details.setFollowUpDetails(event.getFollowup());
        details.setDeviceId(event.getDeviceId());

        populateDeathDetailsFields(details, event, adtMessage);

        result.setEventDetails(details);
        if (event.getEventTreatingPhysician() != null) {
            result.setIncludeTreatingPhysician(true);
            final NameWithAddressDto treatingPhysician = new NameWithAddressDto();
            treatingPhysician.setFirstName(event.getEventTreatingPhysician().getFirstName());
            treatingPhysician.setLastName(event.getEventTreatingPhysician().getLastName());
            treatingPhysician.setPhone(event.getEventTreatingPhysician().getPhone());

            if (event.getEventTreatingPhysician().getEventAddress() != null) {
                treatingPhysician.setIncludeAddress(true);
                AddressDto addressDto = createAddressDto(event.getEventTreatingPhysician().getEventAddress());
                treatingPhysician.setAddress(addressDto);

            } else {
                treatingPhysician.setIncludeAddress(false);
            }
            result.setTreatingPhysician(treatingPhysician);
        } else {
            result.setIncludeTreatingPhysician(false);
        }

        if (event.getEventTreatingHospital() != null) {
            result.setIncludeHospital(true);
            final HospitalDto hospitalDto = new HospitalDto();

            hospitalDto.setPhone(StringUtils.trimToNull(event.getEventTreatingHospital().getPhone()));
            hospitalDto.setName(event.getEventTreatingHospital().getName());
            if (event.getEventTreatingHospital().getEventAddress() != null) {
                hospitalDto.setIncludeAddress(true);
                hospitalDto.setAddress(createAddressDto(event.getEventTreatingHospital().getEventAddress()));
            } else {
                hospitalDto.setIncludeAddress(false);
            }

            result.setTreatingHospital(hospitalDto);
        } else {
            result.setIncludeHospital(false);
        }
        //
        if (event.getEventRn() != null) {
            result.setIncludeResponsible(true);
            NameWithAddressDto responsible = new NameWithAddressDto();

            responsible.setFirstName(event.getEventRn().getFirstName());
            responsible.setLastName(event.getEventRn().getLastName());
            // NameWithAddressDto may contain a phone, but EventRN doesn't have any

            if (event.getEventRn().getEventAddress() != null) {
                responsible.setIncludeAddress(true);
                responsible.setAddress(createAddressDto(event.getEventRn().getEventAddress()));
            } else {
                responsible.setIncludeAddress(false);
            }
            result.setResponsible(responsible);
        } else {
            result.setIncludeResponsible(false);
        }
        //
        if (event.getEventManager() != null) {
            result.setIncludeManager(true);

            final ManagerDto manager = new ManagerDto();
            manager.setFirstName(event.getEventManager().getFirstName());
            manager.setLastName(event.getEventManager().getLastName());
            manager.setPhone(StringUtils.trimToNull(event.getEventManager().getPhone()));
            manager.setEmail(event.getEventManager().getEmail());

            result.setManager(manager);
        } else {
            result.setIncludeManager(false);
        }

        if (adtMessage != null) {
            adtMessagePopulator.populate(adtMessage, result);
        }

        // TODO Deprecated part - should be removed
        if (adtMessage != null) {
            AdtEventDto adtEventDto = new AdtEventDto();
            result.setAdtEvent(adtEventDto);
            fillEVNData(adtEventDto, adtMessage);
            fillPIDData(adtEventDto, adtMessage);
            fillPR1Data(result, adtMessage);
            fillPV1Data(result, adtMessage);
            fillIN1Data(result, adtMessage);
        }

        result.setIsIrRequired(event.getEventType().isRequireIr() && BooleanUtils.isTrue(event.getResident().getFacility().getIrEnabled()));
        final IncidentReport incidentReport = incidentReportService.getIncidentReportForEvent(event.getId());
        if (incidentReport != null) {
            result.setIrId(incidentReport.getId());
            result.setIrDate(incidentReport.getReportDate() != null ? incidentReport.getReportDate().getTime() : null);
        }
        return result;
    }

    private void populateDeathDetailsFields(EventDetailsDto details, Event event, AdtMessage adtMessage) {
        details.setDeathDateTime(event.getDeathDate());
        Boolean deathIndicator = event.getDeathIndicator();

        if (adtMessage instanceof PIDSegmentContainingMessage) {
            final PIDSegmentContainingMessage msg = (PIDSegmentContainingMessage) adtMessage;
            if (msg.getPid() != null) {
                final PIDPatientIdentificationSegment pid = msg.getPid();

                if (details.getDeathDateTime() == null) {
                    details.setDeathDateTime(pid.getPatientDeathDateAndTime());
                }

                // this is just null-safe 'OR' operation between two Booleans which will result in null if both inputs are nulls
                // null is the least prioritized values, so null || true = true and null || false = false
                if (pid.getPatientDeathIndicator() != null) {
                    final Boolean pidDeathIndicator = idCodedValueForHL7TablesBooleanConverter.convert(pid.getPatientDeathIndicator());
                    if (deathIndicator == null) {
                        deathIndicator = pidDeathIndicator;
                    } else {
                        if (pidDeathIndicator != null) {
                            deathIndicator = deathIndicator || pidDeathIndicator;
                        }
                    }
                }

            }
        }
        details.setDeathIndicator(deathIndicator);
    }

    @Deprecated
    private static void fillIN1Data(EventDto result, AdtMessage adtMessage) {
        if (adtMessage instanceof IN1ListSegmentContainingMessage) {
            final List<IN1InsuranceSegment> in1List = ((IN1ListSegmentContainingMessage) adtMessage).getIn1List();
            if (!CollectionUtils.isEmpty(in1List)) {
                final IN1InsuranceSegment in1 = in1List.get(0);
                if (in1 != null) {
                    final InsuranceDto insuranceDto = new InsuranceDto();
                    insuranceDto.setPlanId(in1.getInsurancePlanId().getIdentifier());
                    insuranceDto.setCompanyId(in1.getInsuranceCompanyId().getpId());
                    if (in1.getInsuranceCompanyName() != null) {
                        insuranceDto.setCompanyName(in1.getInsuranceCompanyName().getOrganizationName());
                    }
                    insuranceDto.setPlanEffectiveDate(in1.getPlanEffectiveDate());
                    insuranceDto.setPlanExpirationDate(in1.getPlanExpirationDate());
                    insuranceDto.setPlanType(in1.getPlanType());

                    result.setInsurance(insuranceDto);
                }
            }
        }
    }

    @Deprecated
    private void fillEVNData(AdtEventDto adtEventDto, AdtMessage adtMessage) {
        if (adtMessage instanceof EVNSegmentContainingMessage) {
            final EVNEventTypeSegment evn = ((EVNSegmentContainingMessage) adtMessage).getEvn();
            if (evn != null) {
                adtEventDto.setEventTypeCode(evn.getEventTypeCode());
                adtEventDto.setRecordedDateTime(evn.getRecordedDatetime());
                adtEventDto.setEventReasonCode(isCodedValueForUserDefinedTablesStringConverter.convert(evn.getEventReasonCode()));
                adtEventDto.setEventOccured(evn.getEventOccurred());
            }
        }
    }

    @Deprecated
    private void fillPIDData(AdtEventDto adtEventDto, AdtMessage adtMessage) {
        if (adtMessage instanceof PIDSegmentContainingMessage) {
            final PIDPatientIdentificationSegment pid = ((PIDSegmentContainingMessage) adtMessage).getPid();
//            AdtEventDto adtEventDto = new AdtEventDto(patient);
//            result.setPatient(adtEventDto);
            if (pid != null) {
                if (CollectionUtils.isNotEmpty(pid.getPatientIdentifiers())) {
                    adtEventDto.setPatientIdentifier(pid.getPatientIdentifiers().get(0).getpId());
                }
                if (CollectionUtils.isNotEmpty(pid.getMothersMaidenNames())) {
                    adtEventDto.setMothersMaidenName(xpnPersonNameStringTransformer.convert(pid.getMothersMaidenNames().get(0)));
                }
                if (CollectionUtils.isNotEmpty(pid.getPatientAliases())) {
                    adtEventDto.setPatientAlias(xpnPersonNameStringTransformer.convert(pid.getPatientAliases().get(0)));
                }
                if (CollectionUtils.isNotEmpty(pid.getPhoneNumbersHome())) {
                    adtEventDto.setPhoneNumberHome(pid.getPhoneNumbersHome().get(0).getPhoneNumber());
                }
                if (CollectionUtils.isNotEmpty(pid.getPhoneNumbersBusiness())) {
                    adtEventDto.setPhoneNumberBusiness(pid.getPhoneNumbersBusiness().get(0).getPhoneNumber());
                }
                if (CollectionUtils.isNotEmpty(pid.getRaces())) {
                    adtEventDto.setRace(ceCodedElementStringConverter.convert(pid.getRaces().get(0)));
                }
                adtEventDto.setPrimaryLanguage(ceCodedElementStringConverter.convert(pid.getPrimaryLanguage()));
                adtEventDto.setReligion(ceCodedElementStringConverter.convert(pid.getReligion()));
                if (pid.getPatientAccountNumber() != null) {
                    adtEventDto.setPatientAccountNumber(pid.getPatientAccountNumber().getpId());
                }
                if (pid.getDriversLicenseNumber() != null) {
                    adtEventDto.setDriverLicenseNumber(pid.getDriversLicenseNumber().getLicenseNumber());
                }
                if (CollectionUtils.isNotEmpty(pid.getMothersIdentifiers())) {
                    adtEventDto.setMotherIdentifier(pid.getMothersIdentifiers().get(0).getpId());
                }
                if (CollectionUtils.isNotEmpty(pid.getEthnicGroups())) {
                    adtEventDto.setEtnicGroup(ceCodedElementStringConverter.convert(pid.getEthnicGroups().get(0)));
                }
                adtEventDto.setBirthPlace(pid.getBirthPlace());
                if (pid.getBirthOrder() != null && pid.getBirthOrder() != 0)
                    adtEventDto.setBirthOrder(pid.getBirthOrder());
                if (CollectionUtils.isNotEmpty(pid.getCitizenships())) {
                    adtEventDto.setCitizenship(ceCodedElementStringConverter.convert(pid.getCitizenships().get(0)));
                }
                adtEventDto.setVeteransMilitaryStatus(ceCodedElementStringConverter.convert(pid.getVeteransMilitaryStatus()));
                adtEventDto.setNationality(ceCodedElementStringConverter.convert(pid.getNationality()));
                adtEventDto.setDeathDateTime(pid.getPatientDeathDateAndTime());
                adtEventDto.setDeathIndicator(idCodedValueForHL7TablesBooleanConverter.convert(pid.getPatientDeathIndicator()));
            }
        }
    }

    @Deprecated
    private static void fillPR1Data(EventDto eventDto, AdtMessage adtMessage) {
        if (adtMessage instanceof PR1ListSegmentContaingMessage) {
            final List<PR1ProceduresSegment> pr1List = ((PR1ListSegmentContaingMessage) adtMessage).getPr1List();
            if (!CollectionUtils.isEmpty(pr1List)) {
                final PR1ProceduresSegment pr1 = pr1List.get(0);
                if (pr1 != null) {
                    ProcedureDto procedureDto = new ProcedureDto();
                    procedureDto.setDescription(pr1.getProcedureDescription());
                    procedureDto.setDateTime(pr1.getProcedureDatetime());
                    procedureDto.setCode(createCE(pr1.getProcedureCode()));
                    procedureDto.setAssociatedDiagnosisCode(createCE(pr1.getAssociatedDiagnosisCode()));
                    eventDto.setProcedure(procedureDto);
                }
            }
        }
    }

    @Deprecated
    private void fillPV1Data(EventDto eventDto, AdtMessage adtMessage) {
        if (adtMessage instanceof PV1SegmentContainingMessage) {
            final PV1PatientVisitSegment pv1 = ((PV1SegmentContainingMessage) adtMessage).getPv1();
            if (pv1 != null) {
                PatientVisitDto patientVisitDto = new PatientVisitDto();
                patientVisitDto.setPatientClass(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getPatientClass()));
                if (pv1.getAttendingDoctor() != null) {
                    patientVisitDto.setAttendingDoctor(CareCoordinationUtils.getFullName(pv1.getAttendingDoctor().getFirstName(),
                            pv1.getAttendingDoctor().getLastName()));
                }
                if (pv1.getRefferingDoctor() != null) {
                    patientVisitDto.setReferringDoctor(CareCoordinationUtils.getFullName(pv1.getRefferingDoctor().getFirstName(),
                            pv1.getRefferingDoctor().getLastName()));
                }
                if (pv1.getConsultingDoctor() != null) {
                    patientVisitDto.setConsultingDoctor(CareCoordinationUtils.getFullName(pv1.getConsultingDoctor().getFirstName(),
                            pv1.getConsultingDoctor().getLastName()));
                }
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(pv1.getAmbulatoryStatuses())) {
                    final StringBuilder builder = new StringBuilder();
                    for (ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus> is : pv1.getAmbulatoryStatuses()) {
                        final String isConverted = isCodedValueForUserDefinedTablesStringConverter.convert(is);
                        if (StringUtils.isNotBlank(isConverted)) {
                            builder.append(isConverted).append(", ");
                        }
                    }
                    if (builder.length() >= 2) {
                        patientVisitDto.setAmbulatoryStatus(builder.substring(0, builder.length() - 2));
                    }
                }

                patientVisitDto.setAdmissionType(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getAdmissionType()));
                patientVisitDto.setPreadmitTestIndicator(pv1.getPreadmitTestIndicator());
                patientVisitDto.setReadmissionIndicator(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getReadmissionIndicator()));
                patientVisitDto.setAdmitSource(isCodedValueForUserDefinedTablesStringConverter.convert(pv1.getAdmitSource()));
                patientVisitDto.setDischargeDisposition(pv1.getDischargeDisposition());
                if (pv1.getDischargedToLocation() != null)
                    patientVisitDto.setDischargedToLocation(pv1.getDischargedToLocation().getDischargeLocation());
                patientVisitDto.setAdmitDateTime(pv1.getAdmitDatetime());
                patientVisitDto.setDischargeDateTime(pv1.getDischargeDatetime());
                eventDto.setPatientVisit(patientVisitDto);
            }
        }
    }

    private static СECodeDto createCE(CECodedElement procedureCode) {
        if (procedureCode == null) {
            return null;
        }
        СECodeDto codeDto = new СECodeDto();
        codeDto.setIdentifier(procedureCode.getIdentifier());
        codeDto.setText(procedureCode.getText());
        codeDto.setNameOfCodingSystem(procedureCode.getNameOfCodingSystem());
        codeDto.setAlternateIdentifier(procedureCode.getAlternateIdentifier());
        codeDto.setAlternateText(procedureCode.getAlternateText());
        codeDto.setNameOfAlternateCodingSystem(procedureCode.getNameOfAlternateCodingSystem());
        return codeDto;
    }

    public static AddressDto createAddressDto(EventAddress eventAddress) {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(eventAddress.getCity());
        if (eventAddress.getState() != null) {
            addressDto.setState(CareCoordinationUtils.createKeyValueDto(eventAddress.getState()));
        }
        addressDto.setStreet(eventAddress.getStreet());
        addressDto.setZip(eventAddress.getZip());
        return addressDto;
    }

    public void createNotifyEvent(NotifyEventDto eventDto) {

        Event event = new Event();
        event.setResident(careCoordinationResidentDao.get(eventDto.getPatientId()));
        final Long eventTypeId = 31l;
        event.setEventType(eventTypeService.get(eventTypeId));
        event.setIsFollowup(false);
        event.setIsInjury(false);
        event.setEventDatetime(eventDto.getEventDateTime());

        Long employeeId = eventDto.getSubmitterId();
        Employee employee = employeeService.getEmployee(employeeId);

        final EventAuthor author = new EventAuthor();
        author.setFirstName(employee.getFirstName());
        author.setLastName(employee.getLastName());
        author.setOrganization(CareCoordinationConstants.RBA_DEFAULT_ORGANIZATION);
        author.setRole(employee.getCareTeamRole().getName());
        event.setEventAuthor(author);
        eventDao.create(event);

    }

    private com.scnsoft.eldermark.entity.Event createEvent(EventDto eventDto, boolean isManual) {
        com.scnsoft.eldermark.entity.Event eventEntity = new com.scnsoft.eldermark.entity.Event();
        eventEntity.setResident(careCoordinationResidentDao.get(eventDto.getPatient().getId()));
        eventEntity.setEventType(eventTypeService.get(eventDto.getEventDetails().getEventTypeId()));
        eventEntity.setAssessment(eventDto.getEventDetails().getAssessment());
        eventEntity.setBackground(eventDto.getEventDetails().getBackground());

        if (eventDto.getEventDetails().isFollowUpExpected()) {
            eventEntity.setIsFollowup(true);
            eventEntity.setFollowup(eventDto.getEventDetails().getFollowUpDetails());
        } else {
            eventEntity.setIsFollowup(false);
        }
        eventEntity.setIsInjury(eventDto.getEventDetails().isInjury());
        eventEntity.setIsManual(isManual);
        eventEntity.setLocation(eventDto.getEventDetails().getLocation());
        eventEntity.setSituation(eventDto.getEventDetails().getSituation());
        eventEntity.setIsErVisit(eventDto.getEventDetails().isEmergencyVisit());
        eventEntity.setIsOvernightIn(eventDto.getEventDetails().isOvernightPatient());
        eventEntity.setNotes(Arrays.asList(eventDto.getNote()));
        eventEntity.setEventDatetime(eventDto.getEventDetails().getEventDatetime());
        eventEntity.setAuxiliaryInfo(eventDto.getEventDetails().getAuxiliaryInfo());

        if (eventDto.isIncludeManager()) {
            final EventManager eventManager = new EventManager();
            eventManager.setFirstName(eventDto.getManager().getFirstName());
            eventManager.setLastName(eventDto.getManager().getLastName());
            eventManager.setEmail(eventDto.getManager().getEmail());
            eventManager.setPhone(eventDto.getManager().getPhone());
            eventEntity.setEventManager(eventManager);
        }

        final EventAuthor author = new EventAuthor();

        author.setFirstName(eventDto.getEmployee().getFirstName());
        author.setLastName(eventDto.getEmployee().getLastName());
        author.setOrganization(CareCoordinationConstants.RBA_DEFAULT_ORGANIZATION);
        author.setRole(careTeamRoleDao.findOne(eventDto.getEmployee().getRoleId()).getName());
        eventEntity.setEventAuthor(author);

        if (eventDto.isIncludeResponsible()) {
            final EventRN eventRN = new EventRN();
            eventRN.setFirstName(eventDto.getResponsible().getFirstName());
            eventRN.setLastName(eventDto.getResponsible().getLastName());
            if (eventDto.getResponsible().isIncludeAddress()) {
                eventRN.setEventAddress(createEventAddress(eventDto.getResponsible()));
            }
            eventEntity.setEventRn(eventRN);
        }
        if (eventDto.isIncludeTreatingPhysician()) {
            final EventTreatingPhysician eventTreatingPhysician = new EventTreatingPhysician();

            eventTreatingPhysician.setFirstName(eventDto.getTreatingPhysician().getFirstName());
            eventTreatingPhysician.setLastName(eventDto.getTreatingPhysician().getLastName());
            eventTreatingPhysician.setPhone(eventDto.getTreatingPhysician().getPhone());

            if (eventDto.getTreatingPhysician().isIncludeAddress()) {

                eventTreatingPhysician.setEventAddress(createEventAddress(eventDto.getTreatingPhysician()));
            }
            eventEntity.setEventTreatingPhysician(eventTreatingPhysician);
        }

        if (eventDto.isIncludeHospital()) {
            final EventTreatingHospital eventTreatingHospital = new EventTreatingHospital();

            eventTreatingHospital.setName(eventDto.getTreatingHospital().getName());
            eventTreatingHospital.setPhone(eventDto.getTreatingHospital().getPhone());

            if (eventDto.getTreatingHospital().isIncludeAddress()) {
                eventTreatingHospital.setEventAddress(createEventAddress(eventDto.getTreatingHospital()));
            }
            eventEntity.setEventTreatingHospital(eventTreatingHospital);
        }
        pushContent(createEvents(eventEntity), eventEntity);
        eventEntity = eventDao.create(eventEntity);
        logger.info("Create Event DateTime:" + eventEntity.getEventDatetime());
        eventDao.flush();
        eventCreatedQueueProducer.putToEventCreatedQueue(eventEntity.getId());

        return eventEntity;
    }

    private Optional<Event> createAdtEvent(AdtDto adtDto, AdtTypeEnum supportedAdtType) {
        if ((adtDto.getResidentId() == null)) {
            logger.warn("Resident id is not present in adtDto");
            return Optional.absent();
        }

        final CareCoordinationResident adtResident = careCoordinationResidentDao.get(adtDto.getResidentId());

        if (adtResident == null) {
            logger.warn("ADT Resident id=[{}] not found!", adtDto.getResidentId());
            return Optional.absent();
        }
        if (adtDto.getNewPatient() != null && adtDto.getNewPatient()) {
            try {
                xdsRegistryConnectorService.saveCcdInRegistry(adtResident.getId());
            } catch (NHINIoException e) {
                logger.error(e.getMessage(), e);
            }
        }
        Event eventEntity = new Event();
        eventEntity.setAdtMsgId(adtDto.getMsgId());
        if (adtDto.getMsgId() != null) {
            final AdtMessage adtMessage = adtMessageCustomDao.getMessageById(adtDto.getMsgId(), supportedAdtType);
            if (adtMessage instanceof PV1SegmentContainingMessage) {
                final PV1SegmentContainingMessage pvMes = (PV1SegmentContainingMessage) adtMessage;
                if (pvMes.getPv1() != null && "E".equals(pvMes.getPv1().getPatientClass().getRawCode())) {
                    eventEntity.setIsErVisit(true);
                }
            } else {
                logger.info("Incoming adt message [{}] is not of type PV1SegmentContainingMessage", adtMessage.getId());
            }
        } else {
            logger.warn("Adt message id is not present");
        }
        eventEntity.setResident(adtResident);
        eventEntity.setEventType(EVENT_TRIGGERING_ADT_TYPES.get(supportedAdtType));
        eventEntity.setIsFollowup(false);

        eventEntity.setEventDatetime(adtDto.getEventDate());

        final EventAuthor author = new EventAuthor();
        author.setFirstName("ADT");
        author.setLastName("Repository");
        author.setOrganization("XDS.b");
        author.setRole("");
        eventEntity.setEventAuthor(author);
        eventEntity.setSituation(supportedAdtType.getDescription());

        getProcessAdtService().processAdmitDischargeDates(adtDto, adtResident);
        getProcessAdtService().processDeathDate(adtDto, adtResident);
        careCoordinationResidentDao.merge(adtResident);

        pushContent(createEvents(eventEntity), eventEntity);
        eventEntity = eventDao.create(eventEntity);
        eventDao.flush();
        logger.info("Created event with id [{}]", eventEntity.getId());
        eventCreatedQueueProducer.putToEventCreatedQueue(eventEntity.getId());
        return Optional.of(eventEntity);
    }

//    private void registerCcd(CareCoordinationResident id) {
//        DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
//                .setDocumentTitle("CCD.XML")
//                .setFileName("CCD.XML")
//                .setMimeType("text/xml")
//                .build();
//
//    }


    private EventAddress createEventAddress(final WithAddressDto withAddressDto) {
        final EventAddress eventAddress = new EventAddress();
        eventAddress.setCity(withAddressDto.getAddress().getCity());
        eventAddress.setStreet(withAddressDto.getAddress().getStreet());
        eventAddress.setZip(withAddressDto.getAddress().getZip());
        eventAddress.setState(stateService.get(withAddressDto.getAddress().getState().getId()));

        return eventAddress;
    }


    private void processEvent(com.scnsoft.eldermark.schema.Event event) {
        Long organizationId = organizationService.getOrCreateOrganizationFromSchema(event.getOrganization());
        Long communityId = communityService.getOrCreateCommunityFromSchema(organizationId, event.getCommunity());

        // 1. lookup Patient
        List<CareCoordinationResident> residents = careCoordinationResidentService.getOrCreateResident(communityId, event.getPatient());

        for (CareCoordinationResident resident : residents) {
            com.scnsoft.eldermark.entity.Event eventEntity = createEventEntity(event, resident);
            eventNotificationService.createNotifications(eventEntity, getEventDetails(eventEntity.getId()));
        }
    }

    private boolean processDeviceEvent(DeviceEvent event) {
        Long organizationId = organizationService.getOrCreateOrganizationFromSchema(event.getOrganization());
        Long communityId = communityService.getOrCreateCommunityFromSchema(organizationId, event.getCommunity());

        ResidentDevice residentDevice = residentDeviceService.findByDeviceIdAndFacilityId(event.getPatient().getDeviceId(), communityId);
        if (residentDevice != null) {
            com.scnsoft.eldermark.entity.Event eventEntity = createEventEntity(event, residentDevice.getResident());
            eventNotificationService.createNotifications(eventEntity, getEventDetails(eventEntity.getId()));
            return true;
        } else {
            return false;
        }
    }

    private <T extends BasicEvent> com.scnsoft.eldermark.entity.Event createEventEntity(T event, CareCoordinationResident resident) {
        com.scnsoft.eldermark.entity.Event eventEntity = new com.scnsoft.eldermark.entity.Event();
        eventEntity.setResident(resident);
        eventEntity.setEventType(eventTypeService.getByCode(event.getEventDetails().getType()));
        eventEntity.setAssessment(event.getEventDetails().getAssessmentNarrative());
        eventEntity.setBackground(event.getEventDetails().getBackgroundNarrative());

        if (event.getOrganization() != null) {
            eventEntity.setOrganization(event.getOrganization().getName());
        }
        if (event.getCommunity() != null) {
            eventEntity.setCommunity(event.getCommunity().getName());
        }

        FollowUp followUp = event.getEventDetails().getFollowUp();

        if ((followUp != null) && (followUp.isIsExpected())) {
            eventEntity.setIsFollowup(true);
            eventEntity.setFollowup(followUp.getDetails());
        } else {
            eventEntity.setIsFollowup(false);
        }
        eventEntity.setIsInjury(event.getEventDetails().isResultedInInjury() == null ? false : event.getEventDetails().isResultedInInjury());
        eventEntity.setIsManual(false);
        eventEntity.setLocation(event.getEventDetails().getLocation());
        eventEntity.setSituation(event.getEventDetails().getSituationNarrative());
        eventEntity.setIsErVisit(event.getEventDetails().isERVisit());
        eventEntity.setIsOvernightIn(event.getEventDetails().isOvernightInPatient());
        eventEntity.setAuxiliaryInfo(event.getEventDetails().getAuxiliaryInfo());

        eventEntity.setEventDatetime(calculateDatetime(event.getEventDetails().getDate(), event.getEventDetails().getTime()));

        if (event.getManager() != null) {
            final EventManager eventManager = new EventManager();
            eventManager.setFirstName(event.getManager().getName().getFirstName());
            eventManager.setLastName(event.getManager().getName().getLastName());
            eventManager.setEmail(event.getManager().getEmail());
            eventManager.setPhone(event.getManager().getPhone());
            eventEntity.setEventManager(eventManager);
        }

        if (event.getFormAuthor() != null) {
            final EventAuthor author = new EventAuthor();
            author.setFirstName(event.getFormAuthor().getName().getFirstName());
            author.setLastName(event.getFormAuthor().getName().getLastName());
            author.setOrganization(event.getFormAuthor().getOrganization());
            author.setRole(event.getFormAuthor().getRole());
            eventEntity.setEventAuthor(author);
        }
        if (event.getRN() != null) {
            final EventRN eventRN = new EventRN();
            eventRN.setFirstName(event.getRN().getName().getFirstName());
            eventRN.setLastName(event.getRN().getName().getLastName());
            if (!CollectionUtils.isEmpty(event.getRN().getAddress())) {
                com.scnsoft.eldermark.schema.Address address = event.getRN().getAddress().get(0);
                EventAddress eventAddress = new EventAddress();
                eventAddress.setCity(address.getCity());
                eventAddress.setStreet(address.getStreet());
                eventAddress.setZip(address.getZip());
                eventAddress.setState(stateService.findByAbbrOrFullName(address.getState()));
                eventRN.setEventAddress(eventAddress);
            }
            eventEntity.setEventRn(eventRN);
        }
        if (event.getEventDetails().getTreatingPhysician() != null) {
            final EventTreatingPhysician eventTreatingPhysician = new EventTreatingPhysician();
            final TreatingPhysician treatingPhysician = event.getEventDetails().getTreatingPhysician();

            eventTreatingPhysician.setFirstName(treatingPhysician.getName().getFirstName());
            eventTreatingPhysician.setLastName(treatingPhysician.getName().getLastName());
            eventTreatingPhysician.setPhone(treatingPhysician.getPhone());

            if (treatingPhysician.getAddress() != null) {
                EventAddress eventAddress = new EventAddress();
                eventAddress.setCity(treatingPhysician.getAddress().getCity());
                eventAddress.setStreet(treatingPhysician.getAddress().getStreet());
                eventAddress.setZip(treatingPhysician.getAddress().getZip());
                eventAddress.setState(stateService.findByAbbrOrFullName(treatingPhysician.getAddress().getState()));
                eventTreatingPhysician.setEventAddress(eventAddress);
            }
            eventEntity.setEventTreatingPhysician(eventTreatingPhysician);
        }

        if (event.getEventDetails().getTreatingHospital() != null) {
            final EventTreatingHospital eventTreatingHospital = new EventTreatingHospital();
            final TreatingHospital treatingHospital = event.getEventDetails().getTreatingHospital();

            eventTreatingHospital.setName(treatingHospital.getName());
            eventTreatingHospital.setPhone(treatingHospital.getPhone());

            if (treatingHospital.getAddress() != null) {
                EventAddress eventAddress = new EventAddress();
                eventAddress.setCity(treatingHospital.getAddress().getCity());
                eventAddress.setStreet(treatingHospital.getAddress().getStreet());
                eventAddress.setZip(treatingHospital.getAddress().getZip());
                eventAddress.setState(stateService.findByAbbrOrFullName(treatingHospital.getAddress().getState()));
                eventTreatingHospital.setEventAddress(eventAddress);
            }
            eventEntity.setEventTreatingHospital(eventTreatingHospital);
        }

        if (event instanceof DeviceEvent) {
            String deviceId = ((DeviceEvent) event).getPatient().getDeviceId();
            eventEntity.setDeviceId(deviceId);
        }

        pushContent(event, eventEntity);
        eventDao.create(eventEntity);
        eventCreatedQueueProducer.putToEventCreatedQueue(eventEntity.getId());
        return eventEntity;
    }

    private void pushContent(Object event, Event eventEntity) {
        try {
            String xml = marshal(event);
            eventEntity.setEventContent(xml);
        } catch (JAXBException e) {
            eventEntity.setEventContent("undefined");
            logger.error("Error marshaling event", e);
        }
    }

    private String marshal(Object event) throws JAXBException {
        StringWriter sw = new StringWriter();
        synchronized (eventsMarshaller) {
            eventsMarshaller.marshal(event, sw);
            sw.flush();
        }
        return sw.toString();
    }


    private Date calculateDatetime(final XMLGregorianCalendar date, final XMLGregorianCalendar time) {
        final Calendar calendar = time == null ? new GregorianCalendar() : time.toGregorianCalendar();
//        calendar.set(date.getYear(), date.getMonth() - 1, date.getDay());
        if (date != null) {
            calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
            calendar.set(Calendar.MONTH, date.getMonth() - 1);
            calendar.set(Calendar.YEAR, date.getYear());
        }

        return calendar.getTime();
    }

    private void fixFilterDates(EventFilterDto eventFilter) {
        final Calendar calendar = new GregorianCalendar();
        if (eventFilter.getDateFrom() != null) {
            calendar.setTime(eventFilter.getDateFrom());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            eventFilter.setDateFrom(calendar.getTime());
        }

        if (eventFilter.getDateTo() != null) {
            calendar.setTime(eventFilter.getDateTo());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            eventFilter.setDateTo(calendar.getTime());
        }
    }

    /*
    private void processEvent2(com.scnsoft.eldermark.schema.Event event) {

        CareCoordinationResident resident = searchResidentByCommunity(event.getPatient(), event.getCommunity().getID());
        if (resident==null) {
            resident = searchResidentByOrganization(event.getPatient(), event.getOrganization().getID());
            if (resident==null) {
                Database database = searchOrganizationByExtId(event.getOrganization());
                if (database ==null) {
                    database = createOrganization();
                    sendNotificatyions();
                }
                createPatient(event.getPatient(), event.getOrganization().getID());
            }
            Organization organization = searchCommunityInOrganization(event.getCommunity().getID(), event.getOrganization().getID());
            if (organization==null) {

            }
        }

                // 1. lookup Patient
        CareCoordinationResident resident = careCoordinationResidentService.getOrCreateResident(event.getPatient());
        com.scnsoft.eldermark.entity.Event eventEntity = createEventEntity(event, resident);

        eventNotificationService.createNotifications(eventEntity);
    }*/

    public ProcessAdtService getProcessAdtService() {
        return processAdtService;
    }

    public void setProcessAdtService(final ProcessAdtService processAdtService) {
        this.processAdtService = processAdtService;
    }

    @Override
    public Event getById(Long eventId) {
        return eventDao.get(eventId);
    }
}
