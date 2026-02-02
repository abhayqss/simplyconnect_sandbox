package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.projection.AssessmentDataAware;
import com.scnsoft.eldermark.beans.projection.ClientHistoryOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.projection.ClientOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.projection.EncounterNoteDetailsAware;
import com.scnsoft.eldermark.beans.projection.EventDetailsAware;
import com.scnsoft.eldermark.beans.projection.NoteOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.projection.OutreachReportAssessmentDataAware;
import com.scnsoft.eldermark.beans.projection.ServicePlanDetailsAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutReachReturnTrackerRtfRow;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerOtfClientRow;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerOtfCommunityRow;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerOtfRow;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerReport;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerRtfClientRow;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerRtfCommunityRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.CareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.security.projection.ClientCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.security.projection.CommunityCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.dao.PersonAddressDao;
import com.scnsoft.eldermark.dao.PersonTelecomDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.CommunityCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EncounterNoteSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.NoteSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ServicePlanSpecificationGenerator;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.OutreachReturnTrackerReportDto;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.client.report.CareTeamMemberOutreachReportItem;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsOutreachReportItem;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.EncounterNoteService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class OutreachReturnTrackerReportGenerator extends DefaultReportGenerator<OutreachReturnTrackerReport> {

    private static final Logger logger = LoggerFactory.getLogger(OutreachReturnTrackerReportGenerator.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientHistoryDao clientHistoryDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private EncounterNoteService encounterNoteService;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private NoteSpecificationGenerator noteSpecificationGenerator;

    @Autowired
    private EncounterNoteSpecificationGenerator encounterNoteSpecificationGenerator;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecificationGenerator;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCareTeamMemberSpecificationGenerator;

    @Autowired
    private CommunityCareTeamMemberSpecificationGenerator communityCareTeamMemberSpecificationGenerator;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;

    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;

    @Autowired
    private PersonAddressDao personAddressDao;

    @Autowired
    private PersonTelecomDao personTelecomDao;

    @Autowired
    private Converter<Address, AddressDto> addressDtoConverter;

    @Autowired
    private Converter<ClientOutreachReportDetailsAware, ClientDetailsOutreachReportItem> clientOutreachReportDetailsConverter;

    @Autowired
    private Converter<ClientHistoryOutreachReportDetailsAware, ClientDetailsOutreachReportItem> clientHistoryOutreachReportDetailsConverter;

    @Autowired
    private Converter<CommunityCareTeamMemberOutreachReportDetailsAware, CareTeamMemberOutreachReportItem> careTeamMemberOutreachReportItemConverter;

    @Autowired
    private Converter<ClientCareTeamMemberOutreachReportDetailsAware, CareTeamMemberOutreachReportItem> clientCareTeamMemberOutreachReportItemConverter;

    @Override
    public OutreachReturnTrackerReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        logger.debug("Start generating OutreachReturnTracking report");
        var report = new OutreachReturnTrackerReport();
        populateReportingCriteriaFields(filter, report);

        var clients = getClientsData(filter, permissionFilter);

        var clientIds = clients.stream()
                .map(ClientOutreachReportDetailsAware::getId)
                .collect(Collectors.toList());

        var clientOutreachDetails = clients.stream()
                .map(clientOutreachReportDetailsConverter::convert)
                .collect(Collectors.toList());

        var clientHistories = getClientHistoryData(filter, clientIds);

        var clientHistoryOutreachDetails = clientHistories.stream()
                .map(clientHistoryOutreachReportDetailsConverter::convert)
                .collect(Collectors.toList());

        clientOutreachDetails.addAll(clientHistoryOutreachDetails);

        var clientPersonAddresses = getClientPersonAddresses(clientIds);
        var clientPersonTelecoms = getClientPersonTelecoms(clientIds);

        var clientNotes = getClientNotesData(filter, permissionFilter, clientIds);

        var clientEncounterNotes = getClientEncounterNotesData(filter, permissionFilter);

        var clientEvents = getClientEventsData(filter, permissionFilter, clientIds);

        var clientServicePlans = getServicePlansData(filter, permissionFilter, clientIds);

        var careTeamMembers = getCareTeamMembers(filter, permissionFilter);
        var clientCareTeamMembers = getClientCareTeamMembers(permissionFilter, clientIds);

        var ctmEmployeeIds = careTeamMembers.stream()
                .map(CommunityCareTeamMemberOutreachReportDetailsAware::getEmployeeId)
                .collect(Collectors.toList());

        var clientCtmEmployeeIds = clientCareTeamMembers.stream()
                .map(CareTeamMemberOutreachReportDetailsAware::getEmployeeId)
                .collect(Collectors.toList());

        var communityCtmTelecoms = getCommunityCareTeamMemberPersonTelecoms(ctmEmployeeIds);
        var clientCtmTelecoms = getClientCareTeamMemberPersonTelecoms(clientCtmEmployeeIds);

        var norCalAssessments = getNorCalAssessments(filter, permissionFilter, clientIds);

        var reportDto = new OutreachReturnTrackerReportDto();
        reportDto.setAssessments(norCalAssessments);
        reportDto.setClientData(clientOutreachDetails);
        reportDto.setEvents(clientEvents);
        reportDto.setNotes(clientNotes);
        reportDto.setServicePlans(clientServicePlans);
        reportDto.setCareTeamMembers(careTeamMembers);
        reportDto.setClientCareTeamMembers(clientCareTeamMembers);
        reportDto.setEncounterNotes(clientEncounterNotes);
        reportDto.setClientPersonAddresses(clientPersonAddresses);
        reportDto.setClientPersonTelecoms(clientPersonTelecoms);
        reportDto.setCtmTelecoms(communityCtmTelecoms);
        reportDto.setClientCtmTelecoms(clientCtmTelecoms);

        report.setOutreachReturnTrackerOtfRows(createOutreachReturnTrackerOtfRows(reportDto));
        report.setOutReachReturnTrackerRtfRows(createOutreachReturnTrackerRtfRows(reportDto));
        return report;
    }

    private Map<Long, List<PersonTelecom>> getCommunityCareTeamMemberPersonTelecoms(List<Long> ctmEmployeeIds) {
        return personTelecomDao.findCommunityIdPersonTelecomsByCtmEmployeeIdIn(ctmEmployeeIds);
    }

    private Map<Long, List<PersonTelecom>> getClientCareTeamMemberPersonTelecoms(List<Long> employeeIds) {
        return personTelecomDao.findClientIdPersonTelecomsByCtmEmployeeIdIn(employeeIds);
    }

    private Map<Long, List<PersonAddress>> getClientPersonAddresses(List<Long> clientIds) {
        return personAddressDao.findAllByClientIdIn(clientIds);
    }

    private Map<Long, List<PersonTelecom>> getClientPersonTelecoms(List<Long> clientIds) {
        return personTelecomDao.findAllByClientIdIn(clientIds);
    }

    private List<OutreachReturnTrackerOtfRow> createOutreachReturnTrackerOtfRows(OutreachReturnTrackerReportDto reportDto) {
        var clientsByOrganization = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        reportDto.getClientData().forEach(client -> clientsByOrganization.computeIfAbsent(
                        client.getOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));

        return clientsByOrganization.values().stream()
                .map(clients -> createOrganizationOtfRow(clients, reportDto))
                .collect(Collectors.toList());
    }

    private OutreachReturnTrackerOtfRow createOrganizationOtfRow(List<ClientDetailsOutreachReportItem> organizationClients, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutreachReturnTrackerOtfRow();

        var clientsByCommunity = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        organizationClients.forEach(client -> clientsByCommunity.computeIfAbsent(
                        client.getCommunityId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));


        var communityRows = clientsByCommunity.values().stream()
                .map(clients -> createCommunityOtfRow(clients, reportDto))
                .collect(Collectors.toList());
        row.setCommunityRows(communityRows);
        return row;
    }

    private List<OutReachReturnTrackerRtfRow> createOutreachReturnTrackerRtfRows(OutreachReturnTrackerReportDto reportDto) {
        var clientsByOrganization = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        reportDto.getClientData().forEach(client -> clientsByOrganization.computeIfAbsent(
                        client.getOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));

        return clientsByOrganization.values().stream()
                .map(clients -> createOrganizationRtfRow(clients, reportDto))
                .collect(Collectors.toList());
    }

    private OutReachReturnTrackerRtfRow createOrganizationRtfRow(List<ClientDetailsOutreachReportItem> organizationClients, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutReachReturnTrackerRtfRow();

        var clientsByCommunity = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        organizationClients.forEach(client -> clientsByCommunity.computeIfAbsent(
                        client.getCommunityId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));


        var communityRows = clientsByCommunity.values().stream()
                .map(clients -> createCommunityRtfRow(clients, reportDto))
                .collect(Collectors.toList());
        row.setCommunityRows(communityRows);
        return row;
    }

    private OutreachReturnTrackerRtfCommunityRow createCommunityRtfRow(List<ClientDetailsOutreachReportItem> communityClients, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutreachReturnTrackerRtfCommunityRow();

        var clientsByCommunity = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        communityClients.forEach(client -> clientsByCommunity.computeIfAbsent(
                        client.getId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));


        var clientRows = clientsByCommunity.values().stream()
                .map(clients -> createClientRtfRow(clients, reportDto))
                .collect(Collectors.toList());
        row.setClientRows(clientRows);
        return row;
    }

    private OutreachReturnTrackerOtfCommunityRow createCommunityOtfRow(List<ClientDetailsOutreachReportItem> communityClients, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutreachReturnTrackerOtfCommunityRow();

        var clientsByCommunity = new LinkedHashMap<Long, List<ClientDetailsOutreachReportItem>>();
        communityClients.forEach(client -> clientsByCommunity.computeIfAbsent(
                        client.getId(),
                        organizationId -> new ArrayList<>()
                )
                .add(client));


        var clientRows = clientsByCommunity.values().stream()
                .map(clients -> createClientOtfRow(clients, reportDto))
                .collect(Collectors.toList());
        row.setClientRows(clientRows);
        return row;
    }

    private OutreachReturnTrackerOtfClientRow createClientOtfRow(List<ClientDetailsOutreachReportItem> clientDetails, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutreachReturnTrackerOtfClientRow();
        var client = clientDetails.get(0);
        var clientId = client.getId();

        row.setClientName(client.getFullName());
        row.setMemberClientIndexNumber(client.getMedicareNumber());
        var clientEncounterNotes = reportDto.getEncounterNotes().get(clientId);
        if (clientEncounterNotes != null) {
            var encounterNotes = clientEncounterNotes.stream()
                    .sorted(Comparator.nullsLast(Comparator.comparing(EncounterNoteDetailsAware::getEncounterDate)))
                    .filter(encNote -> ATTEMPT_TO_CONTACT_NOTE_TYPE.equals(encNote.getEncounterNoteType().getCode()))
                    .collect(Collectors.toList());
            row.setDateOfOutreachAttempts(encounterNotes.stream()
                    .map(encNote -> Pair.of(encNote.getEncounterFromTime(), encNote.getEncounterToTime()))
                    .collect(Collectors.toList()));
            row.setOutreachAttemptMethods(clientEncounterNotes.stream()
                    .map(encNote -> reportDto.getNotes().stream()
                            .filter(note -> note.getId().equals(encNote.getId()))
                            .findFirst()
                            .map(NoteOutreachReportDetailsAware::getSubType)
                            .map(NoteSubType::getDescription)
                            .orElse(null))
                    .collect(Collectors.toList()));
            row.setTimeSpentPerformingOutreach(clientEncounterNotes.stream()
                    .map(this::countTotalTimeSpent)
                    .collect(Collectors.toList()));
        }

        return row;
    }

    private OutreachReturnTrackerRtfClientRow createClientRtfRow(List<ClientDetailsOutreachReportItem> clientDetails, OutreachReturnTrackerReportDto reportDto) {
        var row = new OutreachReturnTrackerRtfClientRow();

        var client = clientDetails.get(0);
        var clientId = client.getId();
        var clientCommunityId = client.getCommunityId();

        var servicePlans = reportDto.getServicePlans();
        var assessments = reportDto.getAssessments();
        var careTeamMembers = Optional.ofNullable(reportDto.getCareTeamMembers()).stream()
                .flatMap(Collection::stream)
                .filter(ctm -> clientCommunityId.equals(ctm.getCommunityId()))
                .collect(Collectors.toList());
        var clientCareTeamMembers = Optional.ofNullable(reportDto.getClientCareTeamMembers()).stream()
                .flatMap(Collection::stream)
                .filter(ctm -> clientId.equals(ctm.getClientId()))
                .collect(Collectors.toList());

        var communityCtmTelecoms = reportDto.getCtmTelecoms().get(clientCommunityId);
        var clientCtmTelecoms = reportDto.getClientCtmTelecoms().get(clientId);

        var clientEncounterNotes = reportDto.getEncounterNotes().get(clientId);

        row.setMemberClientIndexNumber(client.getMedicareNumber());
        row.setMemberLastName(client.getLastName());
        row.setMemberFirstName(client.getFirstName());
        var telecoms = reportDto.getClientPersonTelecoms().get(clientId);
        var addresses = reportDto.getClientPersonAddresses().get(clientId);
        if (!CollectionUtils.isEmpty(addresses)) {
            var address = addressDtoConverter.convert(addresses.get(0));
            row.setMemberResidentialAddress(address.getStreet());
            row.setMemberResidentialCity(address.getCity());
            row.setMemberResidentialState(address.getStateName());
            row.setMemberResidentialZip(address.getZip());
        }
        var mobilePhone = PersonTelecomUtils.findValue(telecoms, PersonTelecomCode.MC).orElse(null);
        var homePhone = PersonTelecomUtils.findValue(telecoms, PersonTelecomCode.HP).orElse(null);
        row.setMemberPhoneNumber(
                Stream.of(mobilePhone, homePhone)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "))
        );
        row.setEcmEnrollmentStartDate(client.getIntakeDate());
        row.setEcmEnrollmentEndDate(client.getExitDate());
        row.setLatestCarePlanRevisionDate(servicePlans.stream()
                .filter(servicePlan -> clientId.equals(servicePlan.getClientId()))
                .max(Comparator.nullsFirst(Comparator.comparing(ServicePlanDetailsAware::getLastModifiedDate)))
                .map(ServicePlanDetailsAware::getLastModifiedDate)
                .orElse(null));
        var assessmentDataAware = getAssessmentStartedDate(assessments, clientId);
        row.setAssessmentStartedDate(
                Optional.ofNullable(assessmentDataAware)
                        .map(OutreachReportAssessmentDataAware::getDateStarted)
                        .orElse(null)
        );
        row.setMostRecentCompletedAssessmentDate(
                Optional.ofNullable(assessmentDataAware)
                        .map(OutreachReportAssessmentDataAware::getDateCompleted)
                        .orElse(null)
        );
        if (clientEncounterNotes != null) {
            var encounterDate = clientEncounterNotes.stream()
                    .filter(encNote -> FACE_TO_FACE_VISIT_NOTE_TYPE_CODE.equals(encNote.getEncounterNoteType().getCode()))
                    .max(Comparator.nullsFirst(Comparator.comparing(EncounterNoteDetailsAware::getEncounterDate)))
                    .map(EncounterNoteDetailsAware::getEncounterFromTime)
                    .orElse(null);

            row.setInPerson(
                    clientEncounterNotes.stream()
                            .filter(encNote ->  FACE_TO_FACE_VISIT_NOTE_TYPE_CODE.equals(encNote.getEncounterNoteType().getCode()))
                            .count()
            );
            row.setTelephonicVideo(clientEncounterNotes.stream()
                    .filter(encNote -> FACE_TO_FACE_VISIT_NOTE_TYPE_CODE.equals(encNote.getEncounterNoteType().getCode())
                            || PHONE_CALL_EMAIL_NOTE_TYPE.equals(encNote.getEncounterNoteType().getCode()))
                    .count());
            row.setMostRecentEncounterWithMemberDate(encounterDate);
        }

        if (!CollectionUtils.isEmpty(careTeamMembers)) {
            var ctms = careTeamMembers.stream()
                    .map(ctm -> {
                        var employee = careTeamMemberOutreachReportItemConverter.convert(ctm);
                        employee.setEmail(PersonTelecomUtils.findValue(communityCtmTelecoms, PersonTelecomCode.EMAIL).orElse(null));
                        employee.setPhoneNumber(PersonTelecomUtils.findValue(communityCtmTelecoms, PersonTelecomCode.MC).orElse(null));
                        return employee;
                    })
                    .collect(Collectors.toList());
            row.getCareTeamMemberOutreachReportItems().addAll(ctms);
        }
        if (!CollectionUtils.isEmpty(clientCareTeamMembers)) {
            var clientCtms = clientCareTeamMembers.stream()
                    .map(ctm -> {
                        var employee = clientCareTeamMemberOutreachReportItemConverter.convert(ctm);
                        employee.setEmail(PersonTelecomUtils.findValue(clientCtmTelecoms, PersonTelecomCode.EMAIL).orElse(null));
                        employee.setPhoneNumber(PersonTelecomUtils.findValue(clientCtmTelecoms, PersonTelecomCode.MC).orElse(null));
                        return employee;
                    })
                    .collect(Collectors.toList());
            row.getCareTeamMemberOutreachReportItems().addAll(clientCtms);
        }

        if (!CollectionUtils.isEmpty(row.getCareTeamMemberOutreachReportItems())) {
            row.getCareTeamMemberOutreachReportItems()
                    .sort(Comparator.comparing(CareTeamMemberOutreachReportItem::getFirstName, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        var historyDataByClientId = reportDto.getClientData().stream()
                .collect(Collectors.groupingBy(ClientDetailsOutreachReportItem::getId));

        var clientHistoryDetails = historyDataByClientId.get(clientId);
        if (clientHistoryDetails != null) {
            var map = new HashMap<Instant, ClientDeactivationReason>();
            clientHistoryDetails.forEach(item -> map.putIfAbsent(item.getExitDate(), item.getDeactivationReason()));
            row.setDiscontinuationDates(new ArrayList<>(map.keySet()));
            row.setDiscontinuationReasons(new ArrayList<>(map.values()));
        }


        row.setEcmProviderName(client.getCommunityName());
        row.setEcmProviderPhoneNumbers(clientCareTeamMembers.stream()
                .filter(ctm -> CareTeamRoleCode.ROLE_CARE_COORDINATOR.getCode().equals(ctm.getCareTeamRoleCode()))
                .map(ctm -> PersonTelecomUtils.findValue(clientCtmTelecoms, PersonTelecomCode.MC).orElse(null))
                .collect(Collectors.toList())
        );

        return row;
    }

    private OutreachReportAssessmentDataAware getAssessmentStartedDate(List<OutreachReportAssessmentDataAware> assessments, Long clientId) {
        return assessments.stream()
                .filter(assessment -> clientId.equals(assessment.getClientId()))
                .max(Comparator.nullsFirst(Comparator.comparing(AssessmentDataAware::getDateCompleted)))
                .orElse(null);
    }

    private List<CommunityCareTeamMemberOutreachReportDetailsAware> getCareTeamMembers(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var communityCareTeamMemberSpecification = communityCareTeamMemberSpecificationGenerator.hasAccess(permissionFilter)
                .and(communityCareTeamMemberSpecificationGenerator.byCommunityIdNameIn(filter.getAccessibleCommunityIdsAndNames()))
                .and(communityCareTeamMemberSpecificationGenerator.isEmployeeActive())
                .and(communityCareTeamMemberSpecificationGenerator.byCareTeamRoleCodeIn(List.of(CareTeamRoleCode.ROLE_CASE_MANAGER, CareTeamRoleCode.ROLE_CARE_COORDINATOR)));
        return communityCareTeamMemberService.find(communityCareTeamMemberSpecification, CommunityCareTeamMemberOutreachReportDetailsAware.class);
    }

    private List<ClientCareTeamMemberOutreachReportDetailsAware> getClientCareTeamMembers(PermissionFilter permissionFilter, List<Long> clientIds) {
        var clientCareTeamMemberSpecification = clientCareTeamMemberSpecificationGenerator.hasAccess(permissionFilter)
                .and(clientCareTeamMemberSpecificationGenerator.byClientIdIn(clientIds))
                .and(clientCareTeamMemberSpecificationGenerator.isEmployeeActive())
                .and(clientCareTeamMemberSpecificationGenerator.byCareTeamRoleCodeIn(List.of(CareTeamRoleCode.ROLE_CASE_MANAGER, CareTeamRoleCode.ROLE_CARE_COORDINATOR)));

        return clientCareTeamMemberService.find(clientCareTeamMemberSpecification, ClientCareTeamMemberOutreachReportDetailsAware.class);
    }

    private List<ServicePlanDetailsAware> getServicePlansData(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var servicePlanSpecification = servicePlanSpecificationGenerator.hasAccess(permissionFilter)
                .and(servicePlanSpecificationGenerator.byClientIdIn(clientIds))
                .and(servicePlanSpecificationGenerator.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo()));
        return servicePlanService.find(servicePlanSpecification, ServicePlanDetailsAware.class);
    }

    private List<EventDetailsAware> getClientEventsData(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var eventSpecification = eventSpecificationGenerator.hasAccess(permissionFilter)
                .and(eventSpecificationGenerator.byClients(clientIds))
                .and(eventSpecificationGenerator.betweenDates(filter.getInstantFrom(), filter.getInstantTo()));

        return eventService.find(eventSpecification, EventDetailsAware.class);
    }

    private List<NoteOutreachReportDetailsAware> getClientNotesData(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var noteSpecification = noteSpecificationGenerator.byClientIdIn(clientIds)
                .and(noteSpecificationGenerator.hasAccessAndDistinct(permissionFilter))
                .and(noteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo()));

        return noteService.find(noteSpecification, NoteOutreachReportDetailsAware.class);
    }

    private Map<Long, List<EncounterNoteDetailsAware>> getClientEncounterNotesData(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var specification = encounterNoteSpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames())
                .and(encounterNoteSpecificationGenerator.hasAccessAndDistinct(permissionFilter))
                .and(encounterNoteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo()));

        var encounterNotes = encounterNoteService.find(specification, EncounterNoteDetailsAware.class);

        var encounterNoteClientIdsMap = encounterNoteService.findGroupNoteClientIds(specification);

        var clientEncounterNotesMap = new HashMap<Long, List<EncounterNoteDetailsAware>>();

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

    private List<ClientOutreachReportDetailsAware> getClientsData(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var clientSpecification =
                clientSpecificationGenerator.hasDetailsAccess(permissionFilter)
                        .and(clientSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
                        .and(clientSpecificationGenerator.isActiveInPeriod(filter.getInstantFrom(), filter.getInstantTo()));

        return clientService.find(clientSpecification, ClientOutreachReportDetailsAware.class);
    }

    private List<ClientHistoryOutreachReportDetailsAware> getClientHistoryData(InternalReportFilter filter, List<Long> clientIds) {
        var specification = clientHistorySpecificationGenerator.byUpdatedDateTimeIn(filter.getInstantFrom(), filter.getInstantTo())
                .and(clientHistorySpecificationGenerator.byClientIdIn(clientIds));

        return clientHistoryDao.findAll(specification, ClientHistoryOutreachReportDetailsAware.class);
    }

    private List<OutreachReportAssessmentDataAware> getNorCalAssessments(InternalReportFilter filter, PermissionFilter permissionFilter, List<Long> clientIds) {
        var clientAssessmentResultSpecification = clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter)
                .and(clientAssessmentResultSpecificationGenerator.byType(Assessment.NOR_CAL_COMPREHENSIVE))
                .and(clientAssessmentResultSpecificationGenerator.byClientIdIn(clientIds))
                .and(clientAssessmentResultSpecificationGenerator.completedWithinPeriod(filter.getInstantFrom(), filter.getInstantTo()))
                .and(clientAssessmentResultSpecificationGenerator.latestCompletedBeforeDate(filter.getInstantTo()))
                .and(clientAssessmentResultSpecificationGenerator.isUnarchived());

        return clientAssessmentResultService.find(clientAssessmentResultSpecification, OutreachReportAssessmentDataAware.class);
    }

    public Long countTotalTimeSpent(EncounterNoteDetailsAware encNote) {
        if (encNote.getEncounterFromTime() == null || encNote.getEncounterToTime() == null) {
            return 0L;
        }
        return Duration.between(encNote.getEncounterFromTime(), encNote.getEncounterToTime()).toMinutes();
    }

    @Override
    public ReportType getReportType() {
        return ReportType.OUTREACH_RETURN_TRACKER;
    }
}
