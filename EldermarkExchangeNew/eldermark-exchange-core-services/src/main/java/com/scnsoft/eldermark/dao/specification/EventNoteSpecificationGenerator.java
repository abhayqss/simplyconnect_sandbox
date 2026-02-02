package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.EventNoteFilter.EventNoteEnum;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.EventPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.NotePredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.EventNote;
import com.scnsoft.eldermark.entity.EventNote_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.note.Note_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.EventNoteFilter.EventNoteEnum.*;

@Component
public class EventNoteSpecificationGenerator {

    private static final List<EventNoteFilter.EventNoteEnum> includeNotesTypes = Arrays.asList(ALL, NOTE);
    private static final List<EventNoteFilter.EventNoteEnum> includeEventsTypes = Arrays.asList(ALL, EVENT);

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private NotePredicateGenerator notePredicateGenerator;

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    public Specification<EventNote> byFilterAndHasAccess(EventNoteFilter eventNoteFilter, PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            From<?, Client> clientFrom = null;

            //resolve best join to clients
            if (includeNotesTypes.contains(eventNoteFilter.getItemType())) {
                clientFrom = root.join(EventNote_.clients);
                criteriaQuery.distinct(true);
            } else if (includeEventsTypes.contains(eventNoteFilter.getItemType())){
                clientFrom = root.join(EventNote_.client);
            }

            Predicate filterPredicate = getByFilterPredicate(eventNoteFilter, root, criteriaQuery, criteriaBuilder, clientFrom);
            Predicate accessPredicate = getAccessPredicate(permissionFilter, eventNoteFilter.getItemType(), root, criteriaQuery, criteriaBuilder, clientFrom);


            return criteriaBuilder.and(filterPredicate, accessPredicate);
        };
    }

    private Predicate getByFilterPredicate(EventNoteFilter eventNoteFilter, Root<EventNote> root, CriteriaQuery<?> criteriaQuery,
                                           CriteriaBuilder criteriaBuilder, From<?, Client> clientJoin) {
        List<Predicate> predicates = new ArrayList<>();

        //populate root's joins cache. Filter and security predicate will fetch them via JpaUtils.getJoin
        //this also filters out unnecessary item types by use of inner joins
        switch (eventNoteFilter.getItemType()) {
            case ALL:
                root.join(EventNote_.event, JoinType.LEFT);
                root.join(EventNote_.note, JoinType.LEFT);
                break;
            case EVENT:
                root.join(EventNote_.event, JoinType.LEFT);
                predicates.add(root.get(EventNote_.eventId).isNotNull());
                break;
            case NOTE:
                root.join(EventNote_.note, JoinType.INNER);
                break;
            case NONE:
                return criteriaBuilder.or();
        }

        if (eventNoteFilter.getOrganizationId() != null)
            predicates.add(criteriaBuilder.equal(clientJoin.get(Client_.organizationId),
                    eventNoteFilter.getOrganizationId()));

        if (CollectionUtils.isNotEmpty(eventNoteFilter.getCommunityIds()))
            predicates.add(
                    clientJoin.get(Client_.communityId).in(eventNoteFilter.getCommunityIds()));

        if (eventNoteFilter.getClientId() != null) {
            Predicate mergedClients = clientPredicateGenerator.clientAndMergedClients(criteriaBuilder,
                    clientJoin, criteriaQuery, Collections.singletonList(eventNoteFilter.getClientId()));
            predicates.add(mergedClients);
        }
        if (eventNoteFilter.getNoteTypeId() != null)
            predicates.add(criteriaBuilder.equal(root.get(EventNote_.subTypeId), eventNoteFilter.getNoteTypeId()));

        if (eventNoteFilter.getEventTypeId() != null)
            predicates.add(criteriaBuilder.equal(root.get(EventNote_.typeId), eventNoteFilter.getEventTypeId()));

        if (eventNoteFilter.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(EventNote_.DATE), Instant.ofEpochMilli(eventNoteFilter.getToDate())));
        }
        if (eventNoteFilter.getFromDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(EventNote_.DATE), Instant.ofEpochMilli(eventNoteFilter.getFromDate())));
        }

        if (BooleanUtils.isTrue(eventNoteFilter.getOnlyEventsWithIR())) {
            var eventJoin = JpaUtils.getJoin(root, EventNote_.event).orElseThrow();
            predicates.add(criteriaBuilder.isNotEmpty(eventJoin.get(Event_.incidentReport)));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    //eventNoteEnum added as second parameter for better performance
    private Predicate getAccessPredicate(PermissionFilter permissionFilter, EventNoteEnum eventNoteEnum, Root<EventNote> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder, From<?, Client> clientJoin) {
        if (ALL.equals(eventNoteEnum) && employeesHaveSameLevelViewPermissions(permissionFilter)) {
            return allSameLevelPermissionsPredicate(permissionFilter, criteriaBuilder, root, criteriaQuery, clientJoin);
        }

        var predicates = new ArrayList<Predicate>();

        if (includeEventsTypes.contains(eventNoteEnum)) {
            predicates.add(hasAccessToEvents(permissionFilter, criteriaBuilder, root, clientJoin, criteriaQuery));
        }

        if (includeNotesTypes.contains(eventNoteEnum)) {
            predicates.add(hasAccessToNotes(permissionFilter, criteriaBuilder, root, clientJoin, criteriaQuery));
        }

        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    /**
     * Checks if permissions on the same level allowed for the same (linked)
     * employees. For example view events and notes inside associated organization
     * or view events and notes if part of client care team. This check allows to
     * run unified optimized security check across EventOrNote view.
     *
     * @param permissionFilter
     * @return
     */
    private boolean employeesHaveSameLevelViewPermissions(PermissionFilter permissionFilter) {
        return Stream.of(
                new Pair<>(Permission.EVENT_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT_CLIENT, Permission.NOTE_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION, Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY, Permission.NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY),
                new Pair<>(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION, Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION),
                new Pair<>(Permission.EVENT_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY, Permission.NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM, Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM, Permission.NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF, Permission.NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF),
                new Pair<>(Permission.EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH, Permission.NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH))
                .allMatch(pair -> checkPermissionsOnLevel(permissionFilter, pair));
    }

    private boolean checkPermissionsOnLevel(PermissionFilter permissionFilter,
                                            Pair<Permission, Permission> permissions) {

        if (permissionFilter.hasPermission(permissions.getFirst())) {
            return permissionFilter.hasPermission(permissions.getSecond())
                    && CollectionUtils.isEqualCollection(permissionFilter.getEmployees(permissions.getFirst()),
                    permissionFilter.getEmployees(permissions.getSecond()));
        }
        return !permissionFilter.hasPermission(permissions.getSecond());
    }

    private Predicate allSameLevelPermissionsPredicate(PermissionFilter permissionFilter,
                                                       CriteriaBuilder criteriaBuilder, Root<EventNote> root,
                                                       CriteriaQuery<?> criteriaQuery, From<?, Client> clientJoin) {
        Predicate permissionPredicate = notePredicateGenerator.permissionPredicateOnlyByClient(permissionFilter,
                clientJoin, criteriaQuery, criteriaBuilder);

        return criteriaBuilder.and(
                viewableEvent(permissionFilter, root, criteriaQuery, criteriaBuilder),
                permissionPredicate
        );
    }

    private Predicate hasAccessToEvents(PermissionFilter permissionFilter,
                                        CriteriaBuilder criteriaBuilder, Root<EventNote> root,
                                        From<?, Client> clientJoin,
                                        CriteriaQuery<?> criteriaQuery) {
        return eventPredicateGenerator.hasAccess(permissionFilter,
                JpaUtils.getJoin(root, EventNote_.event).orElseThrow(),
                clientJoin,
                criteriaQuery, criteriaBuilder);
    }

    private Predicate hasAccessToNotes(PermissionFilter permissionFilter,
                                       CriteriaBuilder criteriaBuilder, Root<EventNote> root,
                                       From<?, Client> clientJoin,
                                       CriteriaQuery<?> criteriaQuery) {
        return notePredicateGenerator.hasAccess(permissionFilter,
                JpaUtils.getJoin(root, EventNote_.note).orElseThrow(),
                clientJoin, criteriaQuery, criteriaBuilder);
    }

    private Predicate viewableEvent(PermissionFilter permissionFilter, Root<EventNote> root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        var eventJoin = JpaUtils.getJoin(root, EventNote_.event).orElseThrow();

        var noteJoin = JpaUtils.getJoin(root, EventNote_.note).orElseThrow();
        var eventFromNoteJoin = JpaUtils.getOrCreateJoin(noteJoin, Note_.event, JoinType.LEFT, false);

        return eventPredicateGenerator.viewableEvents(List.of(eventJoin, eventFromNoteJoin), permissionFilter.getAllEmployeeIds(), criteriaBuilder, criteriaQuery);
    }
}
