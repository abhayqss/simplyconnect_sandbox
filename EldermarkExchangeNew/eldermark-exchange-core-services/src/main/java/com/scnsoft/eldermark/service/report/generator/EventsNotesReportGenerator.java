package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.ClientDetailsAware;
import com.scnsoft.eldermark.beans.projection.EncounterNoteDetailsAware;
import com.scnsoft.eldermark.beans.projection.EventDetailsAware;
import com.scnsoft.eldermark.beans.projection.NoteDetailsAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.EventsNotesReport;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.events.EventsReportClientRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.events.EventsReportCommunityRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.events.EventsReportRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.events.EventsReportSingleEventRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes.NotesReportClientRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes.NotesReportCommunityRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes.NotesReportRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes.NotesReportSingleNoteRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.EncounterNoteDao;
import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dao.specification.EncounterNoteSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.NoteSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@Transactional(readOnly = true)
public class EventsNotesReportGenerator extends DefaultReportGenerator<EventsNotesReport> {

    private static final Logger logger = LoggerFactory.getLogger(EventsNotesReportGenerator.class);

    private static final Sort NOTES_SORT = Sort.by(
            Sort.Order.asc(
                    String.join(
                            ".",
                            Note_.CLIENT,
                            Client_.COMMUNITY,
                            Community_.NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            Note_.CLIENT,
                            Client_.FIRST_NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            Note_.CLIENT,
                            Client_.LAST_NAME
                    )
            ),
            Sort.Order.asc(Note_.NOTE_DATE)
    );

    private static final Sort EVENTS_SORT = Sort.by(
            Sort.Order.asc(
                    String.join(
                            ".",
                            Event_.CLIENT,
                            Client_.COMMUNITY,
                            Community_.NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            Event_.CLIENT,
                            Client_.FIRST_NAME
                    )
            ),
            Sort.Order.asc(
                    String.join(
                            ".",
                            Event_.CLIENT,
                            Client_.LAST_NAME
                    )
            ),
            Sort.Order.asc(Event_.EVENT_DATE_TIME)
    );

    @Autowired
    private NoteSpecificationGenerator noteSpecificationGenerator;

    @Autowired
    private EncounterNoteSpecificationGenerator encounterNoteSpecificationGenerator;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private EncounterNoteDao encounterNoteDao;

    @Autowired
    private EventDao eventDao;

    @Override
    public ReportType getReportType() {
        return ReportType.EVENTS_NOTES;
    }

    @Override
    public EventsNotesReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        logger.debug("Start generating EventsNotesReport report");
        var report = new EventsNotesReport();
        populateReportingCriteriaFields(filter, report);
        report.setNotesRows(createNotesRows(filter, permissionFilter));
        report.setEventRows(createEventsRows(filter, permissionFilter));
        return report;
    }

    private List<NotesReportRow> createNotesRows(InternalReportFilter filter, PermissionFilter permissionFilter) {

        var specification = noteSpecificationGenerator.hasAccessAndDistinct(permissionFilter)
                .and(noteSpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames()))
                .and(noteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo()))
                .and(noteSpecificationGenerator.isArchived(false));

        logger.debug("Start fetching notes details");

        var notesDetails = noteDao.findAll(specification,
                NoteDetailsAware.class,
                NOTES_SORT);

        logger.debug("Finished fetching notes details");

        var encounterNoteSpecification =
                encounterNoteSpecificationGenerator.hasAccessAndDistinct(permissionFilter)
                        .and(encounterNoteSpecificationGenerator.byClientCommunityIn(filter.getAccessibleCommunityIdsAndNames()))
                        .and(encounterNoteSpecificationGenerator.withinPeriod(filter.getInstantFrom(), filter.getInstantTo()));

        logger.debug("Start fetching encounter details");

        var encounterDetails =
                encounterNoteDao.findAll(encounterNoteSpecification, EncounterNoteDetailsAware.class)
                        .stream().collect(Collectors.toMap(EncounterNoteDetailsAware::getId, Function.identity()));

        logger.debug("Finished fetching encounter details");

        var noteClientIds = notesDetails.stream()
                .map(NoteDetailsAware::getNoteClientIds)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        var clients = clientDao.findByIdIn(noteClientIds, ClientDetailsAware.class);

        var clientCommunitiesMap = clients.stream()
                .collect(Collectors.toMap(ClientDetailsAware::getId, Function.identity()));

        var notesByOrganization = new LinkedHashMap<Long, List<NoteDetailsAware>>();

        notesDetails.forEach(note -> {
            if (note.getClientId() == null) {
                note.getNoteClientIds().forEach(clientId -> notesByOrganization.computeIfAbsent(
                                clientCommunitiesMap.get(clientId).getOrganizationId(), organizationId -> new ArrayList<>()
                        )
                        .add(note));
            } else {
                notesByOrganization.computeIfAbsent(
                                note.getClientOrganizationId(), organizationId -> new ArrayList<>()
                        )
                        .add(note);
            }
        });

        return notesByOrganization.entrySet().stream()
                .map(entry -> createNoteRow(entry.getKey(), entry.getValue(), encounterDetails, clientCommunitiesMap))
                .collect(Collectors.toList());
    }

    private NotesReportRow createNoteRow(
            Long currentOrganizationId,
            List<NoteDetailsAware> organizationNotes,
            Map<Long, EncounterNoteDetailsAware> encounterDetails,
            Map<Long, ClientDetailsAware> clientCommunitiesMap
    ) {
        var row = new NotesReportRow();
        var organizationName = clientCommunitiesMap.values().stream()
                .filter(details -> currentOrganizationId.equals(details.getOrganizationId()))
                .findAny()
                .map(ClientDetailsAware::getOrganizationName)
                .orElse(null);

        row.setOrganizationName(organizationName);

        var notesByCommunity = new LinkedHashMap<Long, Set<NoteDetailsAware>>();

        organizationNotes.forEach(note -> {
            if (note.getClientCommunityId() == null) {
                note.getNoteClientIds().forEach(clientId -> notesByCommunity.computeIfAbsent(
                        clientCommunitiesMap.get(clientId).getCommunityId(), communityId -> new LinkedHashSet<>()
                ).add(note));
            } else {
                notesByCommunity.computeIfAbsent(
                                note.getClientCommunityId(), communityId -> new LinkedHashSet<>()
                        )
                        .add(note);
            }
        });

        var communityRows = notesByCommunity.entrySet().stream()
                .map(entry -> createNoteCommunityRow(
                        entry.getKey(), entry.getValue(), encounterDetails, clientCommunitiesMap)
                )
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);
        return row;
    }

    private NotesReportCommunityRow createNoteCommunityRow(
            Long currentCommunityId,
            Set<NoteDetailsAware> communityNotes,
            Map<Long, EncounterNoteDetailsAware> encounterDetails,
            Map<Long, ClientDetailsAware> clientCommunitiesMap
    ) {

        var row = new NotesReportCommunityRow();

        var communityName = clientCommunitiesMap.values().stream()
                .filter(details -> currentCommunityId.equals(details.getCommunityId()))
                .findAny()
                .map(ClientDetailsAware::getCommunityName)
                .orElse(null);
        row.setCommunityName(communityName);

        var notesByClient = new LinkedHashMap<Long, Set<NoteDetailsAware>>();

        communityNotes.forEach(note -> {
            if (note.getClientId() == null) {
                note.getNoteClientIds().forEach(id -> notesByClient.computeIfAbsent(id, clientId -> new LinkedHashSet<>())
                        .add(note));
            } else {
                notesByClient.computeIfAbsent(note.getClientId(), clientId -> new LinkedHashSet<>())
                        .add(note);
            }
        });

        var clientRows = notesByClient.entrySet().stream()
                .map(entry -> createNoteClientRow(entry.getKey(), entry.getValue(), encounterDetails, clientCommunitiesMap))
                .collect(Collectors.toList());

        row.setClientRows(clientRows);
        return row;
    }

    private NotesReportClientRow createNoteClientRow(
            Long currentClientId,
            Set<NoteDetailsAware> clientNotes,
            Map<Long, EncounterNoteDetailsAware> encounterDetails,
            Map<Long, ClientDetailsAware> clientCommunitiesMap
    ) {
        var client = clientCommunitiesMap.get(currentClientId);

        var row = new NotesReportClientRow();
        row.setClientId(client.getId());
        row.setClientName(client.getFullName());

        var singleNoteRows = clientNotes.stream()
                .map(note -> createSingleNoteRow(note, encounterDetails))
                .sorted(Comparator.comparing(NotesReportSingleNoteRow::getDate))
                .collect(Collectors.toList());

        row.setSingleNoteRows(singleNoteRows);
        return row;
    }

    private NotesReportSingleNoteRow createSingleNoteRow(
            NoteDetailsAware noteDetails, Map<Long, EncounterNoteDetailsAware> encounterDetails
    ) {
        var note = new NotesReportSingleNoteRow();
        note.setAssessment(noteDetails.getAssessment());
        if (note.getDate() == null) {
            note.setDate(noteDetails.getLastModifiedDate());
        } else {
            note.setDate(noteDetails.getNoteDate());
        }
        note.setObjective(noteDetails.getObjective());
        note.setPlan(noteDetails.getPlan());
        note.setSubjective(noteDetails.getSubjective());
        if (ObjectUtils.allNotNull(noteDetails.getEncounterFromTime(), noteDetails.getEncounterToTime())) {
            long totalSpentTime = Duration.between(
                    noteDetails.getEncounterFromTime(), noteDetails.getEncounterToTime()
            ).toMinutes();
            var units = totalSpentTime / 15;
            note.setUnits(units);
        } else {
            note.setUnits(0L);
        }
        note.setEncounterDate(noteDetails.getEncounterDate());
        if (ObjectUtils.anyNotNull(noteDetails.getClinicianCompletingEncounterFirstName(), noteDetails.getClinicianCompletingEncounterLastName())) {
            note.setPersonCompletingEncounter(
                    CareCoordinationUtils.getFullName(
                            noteDetails.getClinicianCompletingEncounterFirstName(),
                            noteDetails.getClinicianCompletingEncounterLastName()
                    )
            );
        }
        var encounterNoteDetailsAware = encounterDetails.get(noteDetails.getId());
        ofNullable(encounterNoteDetailsAware)
                .ifPresent(enc -> {
                    note.setEncounterType(enc.getEncounterNoteType().getDescription());
                });
        note.setNoteType(noteDetails.getSubType().getDescription());
        note.setSubmittedBy(
                CareCoordinationUtils.getFullName(
                        noteDetails.getEmployeeFirstName(),
                        noteDetails.getEmployeeLastName()
                )
        );
        return note;
    }

    private List<EventsReportRow> createEventsRows(InternalReportFilter filter, PermissionFilter permissionFilter) {

        var specification = eventSpecificationGenerator.hasAccess(permissionFilter)
                .and(eventSpecificationGenerator.byClientCommunities(filter.getAccessibleCommunityIdsAndNames()))
                .and(eventSpecificationGenerator.isServiceEventType(false))
                .and(eventSpecificationGenerator.betweenDates(filter.getInstantFrom(), filter.getInstantTo()));

        logger.debug("Start fetching events details");

        var eventDetails = eventDao.findAll(specification,
                EventDetailsAware.class,
                EVENTS_SORT);


        logger.debug("Finished fetching events details");

        var eventsByOrganization = new LinkedHashMap<Long, List<EventDetailsAware>>();
        eventDetails.forEach(event -> eventsByOrganization.computeIfAbsent(
                        event.getClientOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(event));

        return eventsByOrganization.values().stream()
                .map(this::createEventRow)
                .collect(Collectors.toList());
    }

    private EventsReportRow createEventRow(List<EventDetailsAware> organizationEvents) {
        var row = new EventsReportRow();
        row.setOrganizationName(organizationEvents.get(0).getClientOrganizationName());

        var eventsByCommunity = new LinkedHashMap<Long, List<EventDetailsAware>>();
        organizationEvents.forEach(event -> eventsByCommunity.computeIfAbsent(
                        event.getClientCommunityId(),
                        communityId -> new ArrayList<>()
                )
                .add(event));

        var communityRows = eventsByCommunity.values().stream()
                .map(this::createEventCommunityRow)
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);
        return row;
    }

    private EventsReportCommunityRow createEventCommunityRow(List<EventDetailsAware> communityEvents) {

        var row = new EventsReportCommunityRow();
        row.setCommunityName(communityEvents.get(0).getClientCommunityName());

        var eventsByClient = new LinkedHashMap<Long, List<EventDetailsAware>>();
        communityEvents.forEach(event -> eventsByClient.computeIfAbsent(
                        event.getClientId(),
                        communityId -> new ArrayList<>()
                )
                .add(event));

        var clientRows = eventsByClient.values().stream()
                .map(this::createEventClientRow)
                .collect(Collectors.toList());

        row.setClientRows(clientRows);
        return row;
    }

    private EventsReportClientRow createEventClientRow(List<EventDetailsAware> clientEvents) {
        var firstCLientEvent = clientEvents.get(0);
        var row = new EventsReportClientRow();
        row.setClientId(firstCLientEvent.getClientId());
        row.setClientName(firstCLientEvent.getClientFullName());
        var singleEventRow = clientEvents.stream()
                .map(this::createSingleEventRow)
                .collect(Collectors.toList());

        row.setSingleEventRows(singleEventRow);
        return row;
    }

    private EventsReportSingleEventRow createSingleEventRow(EventDetailsAware eventDetails) {
        var event = new EventsReportSingleEventRow();
        event.setAssessment(eventDetails.getAssessment());
        event.setDate(eventDetails.getEventDateTime());
        event.setEventType(eventDetails.getEventTypeDescription());
        event.setBackground(eventDetails.getBackground());
        event.setHasInjury(eventDetails.getIsInjury());
        event.setLocation(eventDetails.getLocation());
        event.setSituation(eventDetails.getSituation());
        event.setFollowUpExpected(eventDetails.getIsFollowup());
        event.setFollowUp(eventDetails.getFollowup());
        event.setEmergencyDepartmentVisit(eventDetails.getIsErVisit());
        event.setOverNightInPatient(eventDetails.getIsOvernightIn());
        event.setSubmittedBy(CareCoordinationUtils.getFullName(
                eventDetails.getEventAuthorFirstName(),
                eventDetails.getEventAuthorLastName()
        ));
        return event;
    }

}
