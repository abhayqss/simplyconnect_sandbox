package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.EventListItemDbo;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.*;

import static com.scnsoft.eldermark.dao.dialect.SqlServerCustomDialect.MSSQL_WHERE_IN_PARAM_LIMIT;

/**
 * @author averazub
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 *
 * Created on 24-Sep-15.
 */
@Repository
public class EventDaoImpl extends BaseDaoImpl<Event> implements EventDao {

    public EventDaoImpl() {
        super(Event.class);
    }

    @Override
    public Date getEventsMinimumDate(List<Long> residentIds) {
        boolean sendMultipleQueries = false;

        String strQuery = "Select min(o.eventDatetime) From Event o LEFT JOIN o.eventType as t Where t.service=false";
        if (CollectionUtils.isNotEmpty(residentIds)) {
            strQuery += " And o.resident.id In :residentIds";
        }
        TypedQuery<Date> query = entityManager.createQuery(strQuery, Date.class);
        if (CollectionUtils.isNotEmpty(residentIds)) {
            if (residentIds.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
                sendMultipleQueries = true;
            }
            query.setParameter("residentIds", residentIds);
        }

        // search minimum
        Date minDate = new Date();
        if (sendMultipleQueries) {
            int fromIndex = 0;
            while (fromIndex < residentIds.size()) {
                int toIndex = Math.min(residentIds.size(), fromIndex + MSSQL_WHERE_IN_PARAM_LIMIT - 1);
                query.setParameter("residentIds", residentIds.subList(fromIndex, toIndex));
                try {
                    Date currentMinDate = query.getSingleResult();
                    if (currentMinDate != null && currentMinDate.before(minDate)) {
                        minDate = currentMinDate;
                    }
                } catch (NoResultException ignored) {}
                fromIndex = toIndex;
            }
        } else {
            try {
                minDate = query.getSingleResult();
            } catch (NoResultException ignored) {}
        }

        if (minDate == null) {
            return new Date();
        } else {
            return minDate;
        }
    }

    @Override
    public List<Long> getEventsIdsForEmployee(Long databaseId, Set<Long> communityIds, boolean isAdmin,
                                              List<Long> accessibleResidentAndMergedResidentIds, Set<Long> employeeCommunityIds) {
        if (!isAdmin && CollectionUtils.isEmpty(accessibleResidentAndMergedResidentIds)) {
            return new ArrayList<Long>();
        }
        final StringBuilder stringBuilder = new StringBuilder("Select DISTINCT event.id ");
        eventListQueryApplyFromAndWhereClause(stringBuilder, null, databaseId, communityIds, accessibleResidentAndMergedResidentIds, employeeCommunityIds);

        final Query query = entityManager.createQuery(stringBuilder.toString());

        eventListQueryApplyQueryParams(query, null, databaseId, communityIds, accessibleResidentAndMergedResidentIds, employeeCommunityIds);

        return query.getResultList();
    }

    @Override
    public Integer getPageNumber(Long eventId, Long databaseId, Set<Long> communityIds, boolean isAdmin, List<Long> accessibleResidentAndMergedResidentIds, Set<Long> employeeCommunityIds) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT (Row-1)/25 FROM " +
                            "(SELECT ROW_NUMBER() OVER (ORDER BY event_datetime desc) AS Row, event.id ");

        boolean sendMultipleQueries = false;

        stringBuilder.append(" FROM Event event LEFT JOIN EventType eventType on event.event_type_id = eventType.id ");

        if (CollectionUtils.isNotEmpty(accessibleResidentAndMergedResidentIds)) {
            stringBuilder.append("LEFT JOIN Resident r on event.resident_id = r.id ");
        }
        stringBuilder.append("WHERE 1=1 ");
        stringBuilder.append("AND eventType.is_service=0 ");
        if (databaseId != null && CollectionUtils.isEmpty(accessibleResidentAndMergedResidentIds)) {
            stringBuilder.append(" AND r.database_id = :databaseId ");
        }

        if (CollectionUtils.isNotEmpty(accessibleResidentAndMergedResidentIds)) {
            if (accessibleResidentAndMergedResidentIds.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
                sendMultipleQueries = true;
            }
            if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
                stringBuilder.append(" AND (r.id IN (:residentIds) ");
            } else {
                stringBuilder.append(" AND r.id IN (:residentIds) ");
            }
        }
        if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
            if (CollectionUtils.isNotEmpty(accessibleResidentAndMergedResidentIds)) {
                stringBuilder.append(" OR r.facility_id in (:employeeCommunityIds)) ");
            } else {
                stringBuilder.append(" AND r.facility_id in (:employeeCommunityIds) ");
            }
        }
        if (CollectionUtils.isNotEmpty(communityIds)) {
            stringBuilder.append(" AND r.facility.id IN (:communityIds) ");
        }
        stringBuilder.append(") us WHERE id = :eventId");

        final Query query = entityManager.createNativeQuery(stringBuilder.toString());
        query.setParameter("eventId", eventId);

        Integer count = 0;
        if (sendMultipleQueries) {
            int fromIndex = 0;
            while (fromIndex < accessibleResidentAndMergedResidentIds.size()) {
                int toIndex = Math.min(accessibleResidentAndMergedResidentIds.size(), fromIndex + MSSQL_WHERE_IN_PARAM_LIMIT - 1);
                eventListQueryApplyQueryParams(query, null, databaseId, communityIds,
                        accessibleResidentAndMergedResidentIds.subList(fromIndex, toIndex), employeeCommunityIds);
                count += ((BigInteger) query.getSingleResult()).intValue();
                fromIndex = toIndex;
            }
        } else {
            eventListQueryApplyQueryParams(query, null, databaseId, communityIds, accessibleResidentAndMergedResidentIds, employeeCommunityIds);
            count = ((BigInteger) query.getSingleResult()).intValue();
        }

        return count;
    }

    @Override
    public Long getEventsCountForEmployee(EventFilterDto filter, final Long databaseId, final Set<Long> communityIds,
                                          boolean isAdmin, List<Long> viewableResidentAndMergedResidentIds, Map<Long, List<Long>> residentsWithNotViewableTypes,
                                          Set<Long> employeeCommunityIds) {
        if (!isAdmin && filter.getPatientId() == null && (CollectionUtils.isEmpty(viewableResidentAndMergedResidentIds) && MapUtils.isEmpty(residentsWithNotViewableTypes))) {
            return 0L;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT coalesce (count (DISTINCT event.id), 0) ");
        final boolean sendMultipleQueries =
                eventListQueryApplyFromAndWhereClauseNotViewable(stringBuilder, filter, databaseId, communityIds, viewableResidentAndMergedResidentIds,
                        residentsWithNotViewableTypes, employeeCommunityIds);

        final Query query = entityManager.createQuery(stringBuilder.toString());

        Long count = 0L;
        if (sendMultipleQueries) {
            int fromIndex = 0;
            while (fromIndex < viewableResidentAndMergedResidentIds.size()) {
                int toIndex = Math.min(viewableResidentAndMergedResidentIds.size(), fromIndex + MSSQL_WHERE_IN_PARAM_LIMIT - 1);
                eventListQueryApplyQueryParamsNotViewable(query, filter, databaseId, communityIds,
                        viewableResidentAndMergedResidentIds.subList(fromIndex, toIndex), residentsWithNotViewableTypes, employeeCommunityIds);
                count += (Long) query.getSingleResult();
                fromIndex = toIndex;
            }
        } else {
            eventListQueryApplyQueryParamsNotViewable(query, filter, databaseId, communityIds, viewableResidentAndMergedResidentIds, residentsWithNotViewableTypes, employeeCommunityIds);
            count = (Long) query.getSingleResult();
        }

        return count;
    }

    @Override
    public Map<Long,Long> getEventsCountGroupedByResidentId(List<Long> viewableResidentAndMergedResidentIds,
                                                            Map<Long, List<Long>> residentsWithNotViewableTypes,
                                                            Set<Long> communityIds, Set<Long> employeeCommunityIds) {
        Map<Long,Long> result = new HashMap<>();
        if (CollectionUtils.isEmpty(viewableResidentAndMergedResidentIds) && MapUtils.isEmpty(residentsWithNotViewableTypes)) {
            return result;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT r.id, coalesce (count (DISTINCT event.id), 0) ");
        final boolean sendMultipleQueries =
                eventListQueryApplyFromAndWhereClauseNotViewable(stringBuilder, null, null, communityIds, viewableResidentAndMergedResidentIds,
                        residentsWithNotViewableTypes, employeeCommunityIds);
        stringBuilder.append(" GROUP BY r.id ");

        final Query query = entityManager.createQuery(stringBuilder.toString());

        if (sendMultipleQueries) {
            // The query has too many parameters. The MS SQL Server supports a maximum of 2100 parameters.
            // TODO Break the query into several requests and send them one-by-one. Take a pagination into account.
            eventListQueryApplyQueryParamsNotViewable(query, null, null, communityIds,
                    viewableResidentAndMergedResidentIds.subList(0, MSSQL_WHERE_IN_PARAM_LIMIT - 1), residentsWithNotViewableTypes, employeeCommunityIds);
        } else {
            eventListQueryApplyQueryParamsNotViewable(query, null, null, communityIds, viewableResidentAndMergedResidentIds, residentsWithNotViewableTypes, employeeCommunityIds);
        }

        List<Object[]> resultSets = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultSets)) {
            for (Object[] resultSet : resultSets) {
                result.put((Long) resultSet[0], (Long) resultSet[1]);
            }
        }
        return result;
    }

    @Override
    public Long getEventsCountForEmployee(EventFilterDto eventFilter, List<Long> residentIds) {
        //TODO not viewable
        return getEventsCountForEmployee(eventFilter, null, null, false, residentIds, null, null);
    }

    @Override
    public List<EventListItemDbo> getEventsForEmployee(EventFilterDto eventFilter, final Long databaseId,
                                                       final Set<Long> communityIds, Pageable pageRequest, boolean isAdmin, List<Long> viewableResidentAndMergedResidentIds,
                                                       final Map<Long, List<Long>> residentsWithNotViewableTypes, final Set<Long> employeeCommunityIds) {
        if (!isAdmin && eventFilter.getPatientId() == null && (CollectionUtils.isEmpty(viewableResidentAndMergedResidentIds) && MapUtils.isEmpty(residentsWithNotViewableTypes)) && CollectionUtils.isEmpty(employeeCommunityIds)) {
            return new ArrayList<>();
        }
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SQL_SELECT_EVENTS);
        final boolean sendMultipleQueries =
                eventListQueryApplyFromAndWhereClauseNotViewable(stringBuilder, eventFilter, databaseId, communityIds, viewableResidentAndMergedResidentIds,
                        residentsWithNotViewableTypes, employeeCommunityIds);

        stringBuilder.append(" ORDER BY event.eventDatetime desc");

        final Query query = entityManager.createQuery(stringBuilder.toString());

        if (sendMultipleQueries) {
            // The query has too many parameters. The MS SQL Server supports a maximum of 2100 parameters.
            // TODO Break the query into several requests and send them one-by-one. Take a pagination into account.

            eventListQueryApplyQueryParamsNotViewable(query, eventFilter, databaseId, communityIds,
                    viewableResidentAndMergedResidentIds.subList(0, MSSQL_WHERE_IN_PARAM_LIMIT - 1), residentsWithNotViewableTypes, employeeCommunityIds);
        } else {
            eventListQueryApplyQueryParamsNotViewable(query, eventFilter, databaseId, communityIds, viewableResidentAndMergedResidentIds, residentsWithNotViewableTypes, employeeCommunityIds);
        }

        applyPageable(query, pageRequest);

        List<Object[]> resultSet = query.getResultList();
        List<EventListItemDbo> results = mapToList(resultSet);

        return results;
    }

    @Override
    public List<EventListItemDbo> getEvents(EventFilterDto eventFilter, List<Long> residentIds, Pageable pageable) {
        if (eventFilter.getPatientId() == null && CollectionUtils.isEmpty(residentIds)) {
            return new ArrayList<EventListItemDbo>();
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SQL_SELECT_EVENTS);
        List<Long> residentIdsToSearch = residentIds;
        if (eventFilter.getPatientId() != null) {
            residentIdsToSearch = new ArrayList<>();
            residentIdsToSearch.add(eventFilter.getPatientId());
        }
        final boolean sendMultipleQueries =
                eventListQueryApplyFromAndWhereClause(stringBuilder, eventFilter, null, null,  residentIdsToSearch, null);

        // order by event date (from the newest to the oldest)
        stringBuilder.append(" ORDER BY event.eventDatetime desc");

        final Query query = entityManager.createQuery(stringBuilder.toString());

        if (sendMultipleQueries) {
            // The query has too many parameters. The MS SQL Server supports a maximum of 2100 parameters.
            // TODO Break the query into several requests and send them one-by-one. Take a pagination into account.

            eventListQueryApplyQueryParams(query, eventFilter, null, null,
                    residentIdsToSearch.subList(0, MSSQL_WHERE_IN_PARAM_LIMIT - 1), null);
        } else {
            eventListQueryApplyQueryParams(query, eventFilter, null, null, residentIdsToSearch, null);
        }

        applyPageable(query, pageable);

        List<EventListItemDbo> results = mapToList(query.getResultList());
        return results;
    }

    public static final String SQL_SELECT_EVENTS = "SELECT DISTINCT event.id AS eventId, event.eventDatetime as eventDate, eventType.description as eventType, eventType.eventGroup.id as eventGroupId, r.firstName as residentFirstName, r.lastName as residentLastName";

    /**
     * Map result set
     */
    private List<EventListItemDbo> mapToList(List<Object[]> resultSet) {
        ArrayList<EventListItemDbo> list = new ArrayList<EventListItemDbo>();
        for (Object[] resultRow: resultSet) {
            EventListItemDbo result = new EventListItemDbo();
            result.setEventId((Long)resultRow[0]);
            result.setEventDate((Date)resultRow[1]);
            result.setEventType((String)resultRow[2]);
            result.setEventGroupId((Long)resultRow[3]);
            result.setResidentFirstName((String)resultRow[4]);
            result.setResidentLastName((String)resultRow[5]);
            list.add(result);
        }
        return list;
    }

    /*private TypedQuery<Event> createQuery(final Long employeeId, EventFilterDto filter, final Long databaseId, final List<Long> communityIds, boolean isAdmin) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Select DISTINCT event ");
        eventListQueryApplyFromAndWhereClause(stringBuilder, filter, databaseId, communityIds, isAdmin, careTeamResidentIds);

        stringBuilder.append(" ORDER BY event.eventDatetime desc");

        final TypedQuery<Event> query = entityManager.createQuery(stringBuilder.toString(), Event.class);

        eventListQueryApplyQueryParams(query, employeeId, filter, databaseId, communityIds, isAdmin, careTeamResidentIds);

        return query;

    }*/

    private boolean eventListQueryApplyFromAndWhereClause(StringBuilder stringBuilder, EventFilterDto filter, final Long databaseId,
                                                          final Set<Long> communityIds, List<Long> mergedResidentIds,
                                                          Set<Long> employeeCommunityIds) {
        boolean sendMultipleQueries = false;

        stringBuilder.append(" FROM Event event LEFT JOIN event.eventType as eventType ");
        //stringBuilder.append(" LEFT OUTER JOIN (" + NOT_VIEWABLE_QUERY + ") nv on nv.id = event.id ");

        if (filter != null && filter.getEventGroupId()!=null) {
            stringBuilder.append("LEFT JOIN event.eventType.eventGroup as eventGroup ");
        }
        if (filter != null && BooleanUtils.isTrue(filter.getIrRelatedEvent())) {
            stringBuilder.append("LEFT JOIN event.incidentReport ir ");
        }

        stringBuilder.append("LEFT JOIN event.resident r ");
        stringBuilder.append("WHERE 1=1 ");
        stringBuilder.append("AND eventType.service=false ");
        if (databaseId != null && CollectionUtils.isEmpty(mergedResidentIds)) {
            stringBuilder.append(" AND r.database.id = :databaseId ");
        }

        if (CollectionUtils.isNotEmpty(mergedResidentIds)) {
            if (mergedResidentIds.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
                sendMultipleQueries = true;
            }
            if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
                stringBuilder.append(" AND (r.id IN (:residentIds) ");
            } else {
                stringBuilder.append(" AND r.id IN (:residentIds) ");
            }
        }
        if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
            if (CollectionUtils.isNotEmpty(mergedResidentIds)) {
                stringBuilder.append(" OR r.facility.id in (:employeeCommunityIds)) ");
            } else {
                stringBuilder.append(" AND r.facility.id in (:employeeCommunityIds) ");
            }
        }

        if (filter!=null) {
            if (filter.getEventTypeId() != null) {
                stringBuilder.append(" AND eventType.id = :eventTypeId");
            }
            if (filter.getEventGroupId() != null) {
                stringBuilder.append(" AND eventGroup.id = :eventGroupId");
            }
            /*if (filter.getPatientId() != null) {
                stringBuilder.append(" AND r.id in (:patientIds)");
            }*/
            if (filter.getDateFrom() != null) {
                stringBuilder.append(" AND event.eventDatetime >= :dateFrom");
            }
            if (filter.getDateTo() != null) {
                stringBuilder.append(" AND event.eventDatetime <= :dateTo");
            }
            if (BooleanUtils.isTrue(filter.getIrRelatedEvent())) {
                stringBuilder.append(" AND ir IS NOT NULL");
            }

        }

        if (CollectionUtils.isNotEmpty(communityIds)) {
            stringBuilder.append(" AND r.facility.id IN (:communityIds) ");
        }

        return sendMultipleQueries;
    }

    private boolean eventListQueryApplyFromAndWhereClauseNotViewable(StringBuilder stringBuilder, EventFilterDto filter, final Long databaseId,
                                                          final Set<Long> communityIds, final List<Long> viewableResidentIds,
                                                          final Map<Long, List<Long>> residentsWithNotViewableTypes, final Set<Long> employeeCommunityIds) {
        boolean sendMultipleQueries = false;

        stringBuilder.append(" FROM Event event LEFT JOIN event.eventType as eventType ");

        if (filter != null && filter.getEventGroupId()!=null) {
            stringBuilder.append("LEFT JOIN event.eventType.eventGroup as eventGroup ");
        }
        if (filter != null && BooleanUtils.isTrue(filter.getIrRelatedEvent())) {
            stringBuilder.append("LEFT JOIN event.incidentReport ir ");
        }
        stringBuilder.append("LEFT JOIN event.resident r ");
        stringBuilder.append("WHERE 1=1 ");
        stringBuilder.append("AND eventType.service=false ");
        if (databaseId != null && (CollectionUtils.isEmpty(viewableResidentIds) && MapUtils.isEmpty(residentsWithNotViewableTypes))) {
            stringBuilder.append(" AND r.database.id = :databaseId ");
        }

        if (CollectionUtils.isNotEmpty(viewableResidentIds) || MapUtils.isNotEmpty(residentsWithNotViewableTypes) || CollectionUtils.isNotEmpty(employeeCommunityIds)) {
            stringBuilder.append(" AND ( 1=2 ");
            if (CollectionUtils.isNotEmpty(viewableResidentIds)) {
                if (viewableResidentIds.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
                    sendMultipleQueries = true;
                }
                stringBuilder.append(" OR (r.id IN (:residentIds)) ");
            }
            if (MapUtils.isNotEmpty(residentsWithNotViewableTypes)) {
                for (Long residentId : residentsWithNotViewableTypes.keySet()) {
                    stringBuilder.append(" OR (r.id = (" + residentId + ") AND eventType.id not in (");
                    List<Long> notViewableEventTypeIds = residentsWithNotViewableTypes.get(residentId);
                    stringBuilder.append(StringUtils.join(notViewableEventTypeIds,','));
                    stringBuilder.append(")) ");
                }
            }
            //communities that community admin has access to, shouldn't be added to query in case events list for single patient
            if (CollectionUtils.isNotEmpty(employeeCommunityIds) && !(filter != null && filter.getPatientId() != null)) {
                stringBuilder.append(" OR (r.facility.id in (:employeeCommunityIds)) ");
            }
            stringBuilder.append(" ) ");

        }

        if (filter!=null) {
            if (filter.getEventTypeId() != null) {
                stringBuilder.append(" AND eventType.id = :eventTypeId");
            }
            if (filter.getEventGroupId() != null) {
                stringBuilder.append(" AND eventGroup.id = :eventGroupId");
            }
            if (filter.getDateFrom() != null) {
                stringBuilder.append(" AND event.eventDatetime >= :dateFrom");
            }
            if (filter.getDateTo() != null) {
                stringBuilder.append(" AND event.eventDatetime <= :dateTo");
            }
            if (BooleanUtils.isTrue(filter.getIrRelatedEvent())) {
                stringBuilder.append(" AND ir IS NOT NULL");
            }
        }

        if (CollectionUtils.isNotEmpty(communityIds)) {
            stringBuilder.append(" AND r.facility.id IN (:communityIds) ");
        }

        return sendMultipleQueries;
    }

    private Query eventListQueryApplyQueryParams(Query query, EventFilterDto filter, final Long databaseId,
                                                 final Set<Long> communityIds, List<Long> mergedResidentIds,
                                                 Set<Long> employeeCommunityIds) {
        // employee id is not required here, list of resident ids is enough to identify an event
//            query.setParameter("employeeId", employeeId);
        if (CollectionUtils.isNotEmpty(mergedResidentIds)) {
            query.setParameter("residentIds", mergedResidentIds);
        }
        if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
            query.setParameter("employeeCommunityIds", employeeCommunityIds);
        }
        if (filter != null) {
            if (filter.getEventTypeId() != null) {
                query.setParameter("eventTypeId", filter.getEventTypeId());
            }
            if (filter.getEventGroupId() != null) {
                query.setParameter("eventGroupId", filter.getEventGroupId());
            }
            if (filter.getDateFrom() != null) {
                query.setParameter("dateFrom", filter.getDateFrom());
            }
            if (filter.getDateTo() != null) {
                query.setParameter("dateTo", filter.getDateTo());
            }
        }

        if (databaseId != null && CollectionUtils.isEmpty(mergedResidentIds)) {
            query.setParameter("databaseId", databaseId);
        }

        if (CollectionUtils.isNotEmpty(communityIds)) {
            query.setParameter("communityIds", communityIds);
        }

        return query;
    }

    private Query eventListQueryApplyQueryParamsNotViewable(Query query, EventFilterDto filter, final Long databaseId,
                                                 final Set<Long> communityIds, final List<Long> viewableResidentIds,
                                                 final Map<Long, List<Long>> residentsWithNotViewableTypes,
                                                 Set<Long> employeeCommunityIds) {
        if (CollectionUtils.isNotEmpty(viewableResidentIds)) {
            query.setParameter("residentIds", viewableResidentIds);
        }
        if (CollectionUtils.isNotEmpty(employeeCommunityIds) && !(filter != null && filter.getPatientId() != null)) {
            query.setParameter("employeeCommunityIds", employeeCommunityIds);
        }
        if (filter != null) {
            if (filter.getEventTypeId() != null) {
                query.setParameter("eventTypeId", filter.getEventTypeId());
            }
            if (filter.getEventGroupId() != null) {
                query.setParameter("eventGroupId", filter.getEventGroupId());
            }
            if (filter.getDateFrom() != null) {
                query.setParameter("dateFrom", filter.getDateFrom());
            }
            if (filter.getDateTo() != null) {
                query.setParameter("dateTo", filter.getDateTo());
            }
        }

        if (databaseId != null && (CollectionUtils.isEmpty(viewableResidentIds) && MapUtils.isEmpty(residentsWithNotViewableTypes))) {
            query.setParameter("databaseId", databaseId);
        }

        if (CollectionUtils.isNotEmpty(communityIds)) {
            query.setParameter("communityIds", communityIds);
        }

        return query;
    }

}
