package com.scnsoft.eldermark.dao.specification;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.EventPredicateGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventType_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.Predicate;

@Component
public class EventSpecificationGenerator {

    @Autowired
    private EventPredicateGenerator eventPredicateGenerator;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    public Specification<Event> byId(Long eventId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Event_.id), eventId);
    }

    public Specification<Event> byEventType(String eventTypeCode) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.join(Event_.EVENT_TYPE).get(EventType_.CODE), eventTypeCode);
    }

    public Specification<Event> byClients(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isNotEmpty(clientIds)) {
                return criteriaBuilder.in(root.join(Event_.CLIENT).get(Client_.ID)).value(clientIds);
            }
            return criteriaBuilder.or();
        };
    }

    public Specification<Event> byClientIdAndMerged(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (clientId != null) {
                var clientJoin = JpaUtils.getOrCreateJoin(root, Event_.client);
                Predicate mergedClients = clientPredicateGenerator.clientAndMergedClients(criteriaBuilder,
                        clientJoin, criteriaQuery, Collections.singletonList(clientId));
                return criteriaBuilder.and(mergedClients);
            }
            return criteriaBuilder.or();
        };
    }


    public Specification<Event> excludeService() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var eventTypeJoin = JpaUtils.getOrCreateJoin(root, Event_.eventType);
            return criteriaBuilder.equal(eventTypeJoin.get(EventType_.SERVICE), false);
        };
    }

    public Specification<Event> byEventDateTimeIn(Instant dateStart, Instant dateEnd) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.between(root.get(Event_.eventDateTime), dateStart, dateEnd);
    }

    public Specification<Event> byEventTypeCodeIn(List<String> eventTypeCodes) {
        return (root, query, criteriaBuilder) -> {
            if (isNotEmpty(eventTypeCodes)) {
                return JpaUtils.getOrCreateJoin(root, Event_.eventType).get(EventType_.code).in(eventTypeCodes);
            } else {
                return criteriaBuilder.or();
            }
        };
    }

    public Specification<Event> byEventTypesIn(List<String> eventTypeDescriptions) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isNotEmpty(eventTypeDescriptions)) {
                return criteriaBuilder.in(root.join(Event_.EVENT_TYPE).get(EventType_.DESCRIPTION)).value(eventTypeDescriptions);
            }
            return criteriaBuilder.or();
        };
    }

    public Specification<Event> byErVisit(boolean value) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Event_.IS_ER_VISIT), value);
    }

    public <T extends IdAware> Specification<Event> byClientCommunities(List<T> communities) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var communityIds = CareCoordinationUtils.toIdsSet(communities);
            if (isNotEmpty(communities)) {
                return JpaUtils.getOrCreateJoin(root, Event_.client).get(Client_.communityId).in(communityIds);
            }
            return criteriaBuilder.or();
        };
    }

    public Specification<Event> betweenDates(Instant dateFrom, Instant dateTo) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        eventPredicateGenerator.from(dateFrom, criteriaBuilder, root),
                        eventPredicateGenerator.to(dateTo, criteriaBuilder, root)
                );
    }

    public Specification<Event> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                eventPredicateGenerator.hasAccess(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Event> hasAccessIgnoringNotViewable(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                eventPredicateGenerator.hasAccessIgnoringNotViewable(permissionFilter, root, criteriaQuery, criteriaBuilder);
    }

    public Specification<Event> viewableForAnyEmployee(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> eventPredicateGenerator.viewableEvents(root,
                employeeIds,
                criteriaBuilder,
                criteriaQuery);
    }

    public Specification<Event> byClientIds(List<Long> clientIds) {
        return (root, query, criteriaBuilder) -> CollectionUtils.isEmpty(clientIds) ?
                criteriaBuilder.or():
                criteriaBuilder.in(root.get(Event_.CLIENT_ID)).value(clientIds);
    }

    public Specification<Event> byEventTypeCodes(List<String> eventTypeCodes) {
        return (root, query, criteriaBuilder) -> CollectionUtils.isEmpty(eventTypeCodes) ?
                criteriaBuilder.or():
                criteriaBuilder.in(root.get(Event_.eventType).get(EventType_.CODE)).value(eventTypeCodes);
    }

    public <T extends IdNameAware> Specification<Event> byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
            (PermissionFilter permissionFilter, Collection<T> communities, Instant createdDate, Instant activeDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var accessibleClients = SpecificationUtils.subquery(Client.class,
                    criteriaQuery,
                    clientRoot ->
                            clientSpecificationGenerator.accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, communities, createdDate, activeDate)
                                    .toPredicate(clientRoot, criteriaQuery, criteriaBuilder));
            return root.get(Event_.clientId).in(accessibleClients);
        };
    }

    public Specification<Event> isServiceEventType(boolean isServiceEvent) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var isService = JpaUtils.getOrCreateJoin(root, Event_.eventType).get(EventType_.service);
            return criteriaBuilder.equal(isService, isServiceEvent);
        };

    }

    public Specification<Event> byAppointmentChainId(Long appointmentChainId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Event_.appointmentChainId), appointmentChainId);
    }
}
