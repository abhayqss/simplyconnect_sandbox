package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.constants.ArizonaMatrixConstants;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ecmtracking.EcmTrackingReport;
import com.scnsoft.eldermark.beans.reports.model.ecmtracking.EcmTrackingReportClientRow;
import com.scnsoft.eldermark.beans.reports.model.ecmtracking.EcmTrackingReportCommunityRow;
import com.scnsoft.eldermark.beans.reports.model.ecmtracking.EcmTrackingReportInsuranceAuthorizationDto;
import com.scnsoft.eldermark.beans.reports.model.ecmtracking.EcmTrackingReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.*;
import com.scnsoft.eldermark.dto.EcmTrackingReportDto;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsItem;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class EcmTrackingReportGenerator extends DefaultReportGenerator<EcmTrackingReport> {

    private static final Logger logger = LoggerFactory.getLogger(EcmTrackingReportGenerator.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;
    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;
    @Autowired
    private NoteSpecificationGenerator noteSpecificationGenerator;
    @Autowired
    private EncounterNoteSpecificationGenerator encounterNoteSpecificationGenerator;
    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;
    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;
    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecificationGenerator;

    @Autowired
    private ClientInsuranceAuthorizationSpecificationGenerator clientInsuranceAuthorizationSpecificationGenerator;

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private NoteDao noteDao;
    @Autowired
    private EncounterNoteDao encounterNoteDao;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private ClientAssessmentDao clientAssessmentDao;
    @Autowired
    private ClientHistoryDao clientHistoryDao;
    @Autowired
    private ServicePlanDao servicePlanDao;
    @Autowired
    private ClientInsuranceAuthorizationDao clientInsuranceAuthorizationDao;
    @Autowired
    private CareTeamMemberService careTeamMemberService;
    @Autowired
    private Converter<ClientDetailsAware, ClientDetailsItem> clientDetailsConverter;
    @Autowired
    private Converter<ClientHistoryDetailsAware, ClientDetailsItem> clientHistoryDetailsConverter;

    @Override
    public EcmTrackingReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        logger.debug("Start generating EcmTracking report");
        var report = new EcmTrackingReport();
        populateReportingCriteriaFields(filter, report);
        report.setRows(createEcmTrackingRows(filter, permissionFilter));
        return report;
    }

    private List<EcmTrackingReportRow> createEcmTrackingRows(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var clientSpecification = clientSpecificationGenerator.hasDetailsAccess(permissionFilter)
                .and(clientSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
                .and(Specification.not(clientSpecificationGenerator.isInactiveAndDeactivationDateBefore(filter.getInstantFrom())));

        var clientDetails = clientDao.findAll(clientSpecification, ClientDetailsAware.class, orderBy());
        var clientDetailsItems = clientDetails.stream()
                .map(clientDetailsConverter::convert)
                .collect(Collectors.toList());

        var clientIds = clientDetails.stream()
                .map(ClientDetailsAware::getId)
                .collect(Collectors.toList());

        var clientHistoryData = getClientHistoryData(filter, permissionFilter, clientIds);
        var clientHistoryDetails = clientHistoryData.stream()
                .map(clientHistoryDetailsConverter::convert)
                .collect(Collectors.toList());

        clientDetailsItems.addAll(clientHistoryDetails);

        var careTeamMembers = getCareTeamMembers(clientIds);

        var assessments = getLastAssessments(filter, permissionFilter, clientIds);

        var notes = getNotes(filter, permissionFilter);

        var encounterNotes = getEncounterNotes(filter, permissionFilter);

        var events = getEvents(filter, permissionFilter, clientIds);

        var servicePlans = getServicePlans(filter, permissionFilter, clientIds);

        var clientInsuranceAuthorizations = getClientInsuranceAuthorizations(filter, clientIds);

        var ecmTrackingReportDto = new EcmTrackingReportDto();
        ecmTrackingReportDto.setClientHistoryData(clientDetailsItems);
        ecmTrackingReportDto.setCareTeamMembers(careTeamMembers);
        ecmTrackingReportDto.setAssessments(assessments);
        ecmTrackingReportDto.setNotes(notes);
        ecmTrackingReportDto.setEncounterNotes(encounterNotes);
        ecmTrackingReportDto.setEvents(events);
        ecmTrackingReportDto.setServicePlans(servicePlans);
        ecmTrackingReportDto.setClientInsuranceAuthorizations(clientInsuranceAuthorizations);

        var clientsByOrganization = new LinkedHashMap<Long, List<ClientDetailsItem>>();
        clientDetailsItems.forEach(client -> clientsByOrganization.computeIfAbsent(
                        client.getOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));

        return clientsByOrganization.values().stream()
                .map(clients -> createOrganizationRow(clients, ecmTrackingReportDto))
                .collect(Collectors.toList());
    }

    private List<ClientInsuranceAuthorizationDetailsAware> getClientInsuranceAuthorizations(InternalReportFilter filter, List<Long> clientIds) {
        var byClientIdIn = clientInsuranceAuthorizationSpecificationGenerator.byClientIdIn(clientIds);
        var authorizationInPeriod =
                clientInsuranceAuthorizationSpecificationGenerator.intersectsWithPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var specification = authorizationInPeriod.and(byClientIdIn);
        return clientInsuranceAuthorizationDao.findAll(specification, ClientInsuranceAuthorizationDetailsAware.class);
    }

    private List<ClientCareTeamMemberIdNameAware> getCareTeamMembers(List<Long> clientIds) {
        return careTeamMemberService.findByClientIdInAndRoleCodeIn(clientIds, List.of(CareTeamRoleCode.ROLE_CARE_COORDINATOR), ClientCareTeamMemberIdNameAware.class);
    }

    private EcmTrackingReportRow createOrganizationRow(
            List<ClientDetailsItem> organizationClients,
            EcmTrackingReportDto ecmTrackingReportDto
    ) {
        var row = new EcmTrackingReportRow();

        var clientsByCommunity = new LinkedHashMap<Long, List<ClientDetailsItem>>();
        organizationClients.forEach(client -> clientsByCommunity.computeIfAbsent(
                        client.getCommunityId(),
                        communityId -> new ArrayList<>()
                )
                .add(client));

        var communityRows = clientsByCommunity.values().stream()
                .map(clients -> createCommunityRow(clients, ecmTrackingReportDto))
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);
        return row;
    }

    private EcmTrackingReportCommunityRow createCommunityRow(
            List<ClientDetailsItem> communityClients,
            EcmTrackingReportDto ecmTrackingReportDto
    ) {
        var row = new EcmTrackingReportCommunityRow();
        row.setCommunityName(communityClients.get(0).getCommunityName());

        var clients = new LinkedHashMap<Long, List<ClientDetailsItem>>();
        communityClients.forEach(client -> clients.computeIfAbsent(
                        client.getId(),
                        communityId -> new ArrayList<>()
                )
                .add(client));

        var clientRows = clients.values().stream()
                .map(client -> createClientRow(client, ecmTrackingReportDto))
                .collect(Collectors.toList());

        row.setClientRows(clientRows);
        return row;
    }

    private EcmTrackingReportClientRow createClientRow(List<ClientDetailsItem> clients, EcmTrackingReportDto ecmTrackingReportDto) {
        var row = new EcmTrackingReportClientRow();
        var client = clients.get(0);
        var clientId = client.getId();

        row.setInsuranceName(client.getInNetworkInsuranceDisplayName());
        row.setClientCareCoordinatorName(ecmTrackingReportDto.getCareTeamMembers()
                .stream()
                .filter(Objects::nonNull)
                .filter(ctm -> ctm.getClientId().equals(clientId))
                .map(CareTeamMemberIdNameAware::getEmployeeFullName)
                .collect(Collectors.joining(", ")));

        row.setClientId(clientId);
        row.setClientStatus(resolveClientStatus(client.getActive()));
        row.setClientFirstName(client.getFirstName());
        row.setClientLastName(client.getLastName());
        row.setDateOfBirth(client.getBirthDate());

        var insuranceAuthorizations = Optional.ofNullable(ecmTrackingReportDto.getClientInsuranceAuthorizations()).stream()
                .flatMap(Collection::stream)
                .filter(clientInsuranceAuthorization -> clientId.equals(clientInsuranceAuthorization.getClientId()))
                .map(insuranceAuthorization -> {
                    var insuranceAuthorizationDto = new EcmTrackingReportInsuranceAuthorizationDto();
                    insuranceAuthorizationDto.setStartDate(insuranceAuthorization.getStartDate());
                    insuranceAuthorizationDto.setEndDate(insuranceAuthorization.getEndDate());
                    insuranceAuthorizationDto.setNumber(insuranceAuthorization.getNumber());
                    return insuranceAuthorizationDto;
                })
                .collect(Collectors.toList());

        row.setInsuranceAuthorizations(insuranceAuthorizations);

        var phoneNumbers = Stream.of(
                        PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.MC).orElse(null),
                        PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.HP).orElse(null)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        row.setPhoneNumber(phoneNumbers);
        row.setMedicaid(client.getMedicaidNumber());

        fillClientHistoryData(row, clientId, ecmTrackingReportDto.getClientHistoryData());
        createAssessmentData(row, clientId, ecmTrackingReportDto.getAssessments());
        fillNotesData(row, clientId, ecmTrackingReportDto.getNotes());
        fillEncounterNotesData(row, clientId, ecmTrackingReportDto.getEncounterNotes());
        fillEventsData(row, clientId, ecmTrackingReportDto.getEvents(), ecmTrackingReportDto.getNotes());
        fillServicePlansData(row, clientId, ecmTrackingReportDto.getServicePlans());

        return row;
    }

    private void fillEventsData(
            EcmTrackingReportClientRow row,
            Long clientId,
            List<ClientEventDateAware> events,
            Map<Long, List<ClientNoteAware>> notes
    ) {
        var eventDetailsAware = events.stream()
                .filter(event -> event.getClientId() != null)
                .collect(Collectors.groupingBy(ClientEventDateAware::getClientId));

        var eventDateAwares = eventDetailsAware.get(clientId);
        var clientNoteDateAwares = notes.get(clientId);

        if (eventDateAwares != null && clientNoteDateAwares != null) {
            row.setLastEventDate(eventDateAwares.stream()
                    .filter(Objects::nonNull)
                    .filter(ClientEventDateAware::getIsManual)
                    .map(ClientEventDateAware::getEventDateTime)
                    .max(Instant::compareTo)
                    .orElse(null));
            row.setTotalNumberEventNotesCompleted(clientNoteDateAwares.stream()
                    .filter(clientNote -> clientNote.getType().equals(NoteType.EVENT_NOTE))
                    .count());
        }
    }

    private void fillEncounterNotesData(EcmTrackingReportClientRow row, Long clientId, Map<Long, List<ClientEncounterNoteAware>> encounterNotes) {
        var clientEncounterNoteAwares = encounterNotes.get(clientId);
        if (clientEncounterNoteAwares != null) {
            row.setIntakeSpecialistFirstEncounterDate(clientEncounterNoteAwares.stream()
                    .filter(encounterNote -> !FACE_TO_FACE_VISIT_NOTE_TYPE_CODE.equals(encounterNote.getEncounterNoteType().getCode()))
                    .map(ClientEncounterNoteAware::getEncounterDate)
                    .min(Instant::compareTo)
                    .orElse(null));
            row.setEcmFaceToFaceFirstEncounterDate(clientEncounterNoteAwares.stream()
                    .filter(encounterNote -> FACE_TO_FACE_VISIT_NOTE_TYPE_CODE.equals(encounterNote.getEncounterNoteType().getCode()))
                    .map(ClientEncounterNoteAware::getEncounterDate)
                    .min(Instant::compareTo)
                    .orElse(null));
        }
    }

    private void fillNotesData(EcmTrackingReportClientRow row, Long clientId, Map<Long, List<ClientNoteAware>> notes) {
        var clientNoteAwares = notes.get(clientId);

        if (clientNoteAwares != null) {
            row.setLastCaseNoteDate(clientNoteAwares.stream()
                    .filter(Objects::nonNull)
                    .map(note -> {
                        if (note.getNoteDate() == null) {
                            return note.getLastModifiedDate();
                        }
                        return note.getNoteDate();
                    })
                    .max(Instant::compareTo)
                    .orElse(null));
            row.setTotalNumberCaseNotes((long) clientNoteAwares.size());
        }
    }

    private void createAssessmentData(EcmTrackingReportClientRow row, Long clientId, List<AssessmentDataAware> assessmentsData) {
        var clientAssessments = assessmentsData.stream()
                .filter(assessment -> assessment.getClientId() != null)
                .collect(Collectors.groupingBy(AssessmentDataAware::getClientId));

        var assessmentDataAwares = clientAssessments.get(clientId);

        if (assessmentDataAwares != null) {
            var assessmentsByName = assessmentDataAwares.stream()
                    .collect(Collectors.groupingBy(AssessmentDataAware::getAssessmentShortName));
            var arizonaAssessments = assessmentsByName.get(Assessment.ARIZONA_SSM);
            var norCalAssessments = assessmentsByName.get(Assessment.NOR_CAL_COMPREHENSIVE);
            var hmisIntakeAssessments = assessmentsByName.get(Assessment.HMIS_ADULT_CHILD_INTAKE);
            var hmisIntakeExitAssessments = assessmentsByName.get(Assessment.HMIS_ADULT_CHILD_INTAKE_EXIT);
            var hmisIntakeReassessmentAssessments = assessmentsByName.get(Assessment.HMIS_ADULT_CHILD_INTAKE_REASESSMENT);

            if (arizonaAssessments != null) {
                fillAssessmentData(row, Assessment.ARIZONA_SSM, arizonaAssessments);
            }
            if (norCalAssessments != null) {
                fillAssessmentData(row, Assessment.NOR_CAL_COMPREHENSIVE, norCalAssessments);
            }
            if (hmisIntakeAssessments != null) {
                Optional.ofNullable(hmisIntakeExitAssessments).ifPresent(hmisIntakeAssessments::addAll);
                Optional.ofNullable(hmisIntakeReassessmentAssessments).ifPresent(hmisIntakeAssessments::addAll);
                fillAssessmentData(row, Assessment.HMIS_ADULT_CHILD_INTAKE, hmisIntakeAssessments);
            }
        }
    }

    private void fillAssessmentData(EcmTrackingReportClientRow row, String assessmentShortName, List<AssessmentDataAware> assessmentDataAwares) {
        switch (assessmentShortName) {
            case Assessment.ARIZONA_SSM:
                row.setTotalNumberCompletedArizonaAssessment((long) assessmentDataAwares.size());
                row.setLastArizonaAssessmentCompleteDate(assessmentDataAwares.stream()
                        .filter(Objects::nonNull)
                        .map(assessment -> {
                            if (assessment.getDateCompleted() == null) {
                                return assessment.getLastModifiedDate();
                            }
                            return assessment.getDateCompleted();
                        })
                        .max(Instant::compareTo)
                        .orElse(null));
                var lastArizonaAssessment = assessmentDataAwares.stream()
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(assessment -> {
                            if (assessment.getDateCompleted() == null) {
                                return assessment.getLastModifiedDate();
                            }
                            return assessment.getDateCompleted();
                        }))
                        .orElse(null);

                if (lastArizonaAssessment != null) {
                    try {
                        var result = objectMapper.readValue(lastArizonaAssessment.getResult(), new TypeReference<HashMap<String, Object>>() {
                        });
                        row.setHousingStatus(extractStringField(result, ArizonaMatrixConstants.SHELTER));

                    } catch (JsonProcessingException e) {
                        throw new InternalServerException(InternalServerExceptionType.ASSESSMENT_EXPORT_FAILURE);
                    }
                }
                break;
            case Assessment.NOR_CAL_COMPREHENSIVE:
                row.setLastNorCalAssessmentCompleteDate(assessmentDataAwares.stream()
                        .filter(Objects::nonNull)
                        .map(assessment -> {
                            if (assessment.getDateCompleted() == null) {
                                return assessment.getLastModifiedDate();
                            }
                            return assessment.getDateCompleted();
                        })
                        .max(Instant::compareTo)
                        .orElse(null));
                row.setTotalNumberCompletedNorCalAssessment((long) assessmentDataAwares.size());
                break;
            case Assessment.HMIS_ADULT_CHILD_INTAKE:
            case Assessment.HMIS_ADULT_CHILD_INTAKE_EXIT:
            case Assessment.HMIS_ADULT_CHILD_INTAKE_REASESSMENT:
                row.setLastHmisAssessmentCompleteDate(assessmentDataAwares.stream()
                        .filter(Objects::nonNull)
                        .map(assessment -> {
                            if (assessment.getDateCompleted() == null) {
                                return assessment.getLastModifiedDate();
                            }
                            return assessment.getDateCompleted();
                        })
                        .max(Instant::compareTo)
                        .orElse(null));
                row.setTotalNumberCompletedHmisAssessment((long) assessmentDataAwares.size());
                break;
        }
    }

    private void fillClientHistoryData(EcmTrackingReportClientRow row, Long clientId, List<ClientDetailsItem> clientHistoryData) {
        var historyDataByClientId = clientHistoryData.stream()
                .collect(Collectors.groupingBy(ClientDetailsItem::getId));

        var clientHistoryDetails = historyDataByClientId.get(clientId);
        if (clientHistoryDetails != null) {
            var map = new HashMap<Instant, ClientDeactivationReason>();
            clientHistoryDetails.forEach(item -> map.putIfAbsent(item.getDeactivationDate(), item.getDeactivationReason()));
            row.setDisenrollmentDates(new ArrayList<>(map.keySet()));
            row.setDisenrollmentReasons(new ArrayList<>(map.values()));
        }
    }

    private void fillServicePlansData(EcmTrackingReportClientRow row, Long clientId, List<ServicePlanDetailsAware> servicePlans) {
        var servicePlansByClientIdMap = servicePlans.stream()
                .filter(servicePlan -> servicePlan.getClientId() != null)
                .collect(Collectors.groupingBy(ServicePlanDetailsAware::getClientId));

        var servicePlanDetailsAwares = servicePlansByClientIdMap.get(clientId);

        if (servicePlanDetailsAwares != null) {
            row.setLastServicePlanDate(servicePlanDetailsAwares.stream()
                    .filter(Objects::nonNull)
                    .map(servicePlan -> {
                        if (servicePlan.getDateCompleted() == null) {
                            return servicePlan.getLastModifiedDate();
                        }
                        return servicePlan.getDateCompleted();
                    })
                    .max(Instant::compareTo)
                    .orElse(null));
            row.setTotalNumberServicePlansCompleted(servicePlanDetailsAwares.stream()
                    .filter(servicePlan -> servicePlan.getServicePlanStatus().equals(ServicePlanStatus.SHARED_WITH_CLIENT))
                    .count());
        }
    }

    private List<ClientHistoryDetailsAware> getClientHistoryData(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var byUpdatedDateTimeIn =
                clientHistorySpecificationGenerator.byUpdatedDateTimeIn(filter.getInstantFrom(), filter.getInstantTo());
        var byClientIdIn = clientHistorySpecificationGenerator.byClientIdIn(clientIds);

        var specification = byUpdatedDateTimeIn.and(byClientIdIn);

        return clientHistoryDao.findAll(specification, ClientHistoryDetailsAware.class);
    }

    private List<ClientEventDateAware> getEvents(InternalReportFilter filter, PermissionFilter permissionFilter, Collection<Long> clientIds) {
        var hasAccess = eventSpecificationGenerator.hasAccess(permissionFilter);
        var byClients = eventSpecificationGenerator.byClients(clientIds);
        var serviceEventType = eventSpecificationGenerator.isServiceEventType(false);
        var betweenDates = eventSpecificationGenerator.betweenDates(filter.getInstantFrom(), filter.getInstantTo());

        var specification = hasAccess.and(byClients.and(serviceEventType.and(betweenDates)));

        return eventDao.findAll(specification, ClientEventDateAware.class);
    }

    private Map<Long, List<ClientNoteAware>> getNotes(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccessAndDistinct = noteSpecificationGenerator.hasAccessAndDistinct(permissionFilter);
        var byClientCommunityIn = noteSpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames());
        var withinPeriod = noteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var archived = noteSpecificationGenerator.isArchived(false);

        var specification = hasAccessAndDistinct.and(byClientCommunityIn.and(withinPeriod.and(archived)));

        var noteClientIdsMap = noteDao.findGroupNoteClientIds(specification);

        var notes = noteDao.findAll(specification, ClientNoteAware.class);

        var clientNotesMap = new HashMap<Long, List<ClientNoteAware>>();

        notes.forEach(note -> {
            var clientIds = noteClientIdsMap.get(note.getId());
            if (note.getType().equals(NoteType.GROUP_NOTE)) {
                clientIds.forEach(
                        clientId ->
                                clientNotesMap.computeIfAbsent(clientId, value -> new ArrayList<>())
                                        .add(note)
                );
            } else {
                clientNotesMap.computeIfAbsent(clientIds.get(0), value -> new ArrayList<>())
                        .add(note);
            }
        });
        return clientNotesMap;
    }

    private Map<Long, List<ClientEncounterNoteAware>> getEncounterNotes(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccessAndDistinct = encounterNoteSpecificationGenerator.hasAccessAndDistinct(permissionFilter);
        var byClientCommunityIn = encounterNoteSpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames());
        var withinPeriod = encounterNoteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo());

        var specification = hasAccessAndDistinct.and(byClientCommunityIn.and(withinPeriod));

        var encounterNoteClientIdsMap = encounterNoteDao.findGroupNoteClientIds(specification);

        var encounterNotes = encounterNoteDao.findAll(specification, ClientEncounterNoteAware.class);

        var clientEncounterNotesMap = new HashMap<Long, List<ClientEncounterNoteAware>>();

        encounterNotes.forEach(note -> {
            var clientIds = encounterNoteClientIdsMap.get(note.getId());
            clientIds.forEach(
                    clientId ->
                            clientEncounterNotesMap.computeIfAbsent(clientId, value -> new ArrayList<>())
                                    .add(note)
            );
        });
        return clientEncounterNotesMap;
    }

    private List<AssessmentDataAware> getLastAssessments(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var hasAccess = clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter);
        var byClientIdIn = clientAssessmentResultSpecificationGenerator.byClientIdIn(clientIds);
        var byTypes =
                clientAssessmentResultSpecificationGenerator.byTypeIn(
                        List.of(Assessment.ARIZONA_SSM,
                                Assessment.HMIS_ADULT_CHILD_INTAKE,
                                Assessment.HMIS_ADULT_CHILD_INTAKE_EXIT,
                                Assessment.HMIS_ADULT_CHILD_INTAKE_REASESSMENT,
                                Assessment.NOR_CAL_COMPREHENSIVE)
                );

        var completedWithinPeriod = clientAssessmentResultSpecificationGenerator.completedWithinPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var unarchived = clientAssessmentResultSpecificationGenerator.isUnarchived();

        var specification = hasAccess.and(byClientIdIn.and(byTypes.and(unarchived.and(completedWithinPeriod))));

        return clientAssessmentDao.findAll(specification, AssessmentDataAware.class, Sort.by(ClientAssessmentResult_.DATE_COMPLETED));
    }

    private List<ServicePlanDetailsAware> getServicePlans(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var byCompletedStatus = servicePlanSpecificationGenerator.byStatus(ServicePlanStatus.SHARED_WITH_CLIENT);
        var byInDevelopmentStatus = servicePlanSpecificationGenerator.byStatus(ServicePlanStatus.IN_DEVELOPMENT);
        var hasAccess = servicePlanSpecificationGenerator.hasAccess(permissionFilter);
        var withinReportPeriod = servicePlanSpecificationGenerator.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var byClientIdIn = servicePlanSpecificationGenerator.byClientIdIn(clientIds);

        var specification = hasAccess.and(withinReportPeriod.and(byClientIdIn.and(byCompletedStatus.or(byInDevelopmentStatus))));

        return servicePlanDao.findAll(specification, ServicePlanDetailsAware.class);
    }

    private String extractStringField(Map<String, Object> json, String fieldName) {
        var field = json.get(fieldName);
        return field != null ? field.toString() : null;
    }

    private Sort orderBy() {
        return Sort.by(CareCoordinationUtils.concat(".", Client_.COMMUNITY, Community_.NAME))
                .and(Sort.by(Client_.FIRST_NAME));
    }

    @Override
    public ReportType getReportType() {
        return ReportType.ECM_TRACKING;
    }
}
