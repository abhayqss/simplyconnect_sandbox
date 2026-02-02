package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mradzivonenka
 * @author Netkachev
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EventNotificationDaoImpl extends BaseDaoImpl<EventNotification> implements EventNotificationDao {
    public EventNotificationDaoImpl() {
        super(EventNotification.class);
    }

    @Override
    public Long countByEventId(long eventId, Boolean isSend) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select count(o) from EventNotification o where o.event.id = :eventId ");
        if (isSend != null) {
            if (isSend) {
                stringBuilder.append("AND o.sentDatetime IS NOT NULL");
            } else {
                stringBuilder.append("AND o.sentDatetime IS  NULL");
            }
        }
        Query query = entityManager.createQuery(stringBuilder.toString());
        query.setParameter("eventId", eventId);

        return (Long) query.getSingleResult();

    }

    @Override
    public void updateDelivered(final Long eventNotificationId) {
        final Query query = entityManager.createQuery("UPDATE EventNotification o SET o.sentDatetime = :dateTime WHERE o.id = :eventNotificationId");
        query.setParameter("dateTime", new Date());
        query.setParameter("eventNotificationId", eventNotificationId);

        query.executeUpdate();
    }

    @Override
    public List<EventNotification> getEventNotificationsByEmployeeId(Long employeeId) {
        final TypedQuery<EventNotification> query = entityManager.createQuery("select o from EventNotification o where o.employee.id = :employeeId", EventNotification.class);
        query.setParameter("employeeId", employeeId);
        return query.getResultList();
    }

    @Override
    public List<Long> getAdminEventNotificationsEventIdsByEmployeeId(Long employeeId, Set<Long> eventIds) {
        final TypedQuery<Long> query = entityManager.createQuery("select o.event.id from EventNotification o where o.employee.id <> :employeeId and o.event.id in :eventIds", Long.class);
        query.setParameter("employeeId", employeeId);
        query.setParameter("eventIds", eventIds);
        return query.getResultList();
    }

    @Override
    public List<EventNotification> listNotSendByEvent(Long eventId) {
        final TypedQuery<EventNotification> query = entityManager.createQuery("select o from EventNotification o where o.event.id = :eventId and o.sentDatetime is NULL", EventNotification.class);
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    @Override
    public List<EventNotification> listByEventId(long eventId, Pageable pageRequest, Boolean isSend) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select o from EventNotification o where o.event.id = :eventId ");
        if (isSend != null) {
            if (isSend) {
                stringBuilder.append("AND o.sentDatetime IS NOT NULL");
            } else {
                stringBuilder.append("AND o.sentDatetime IS  NULL");
            }
        }
        if (pageRequest.getSort() != null) {
            if (pageRequest.getSort().getOrderFor("dateTime") != null) {
                stringBuilder.append(" ORDER BY o.createdDatetime ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("dateTime").getDirection());
            } else if (pageRequest.getSort().getOrderFor("notificationType") != null) {
                stringBuilder.append(" ORDER BY o.notificationType ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("notificationType").getDirection());
            } else if (pageRequest.getSort().getOrderFor("contactName") != null) {
                String direction = pageRequest.getSort().getOrderFor("contactName").getDirection().name();
                stringBuilder.append(" ORDER BY o.personName ").append(direction);
            } else if (pageRequest.getSort().getOrderFor("careTeamRole") != null) {
                stringBuilder.append(" ORDER BY o.careTeamRole ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("careTeamRole").getDirection());
            } else if (pageRequest.getSort().getOrderFor("description") != null) {
                stringBuilder.append(" ORDER BY o.description ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("description").getDirection());
            } else if (pageRequest.getSort().getOrderFor("responsibility") != null) {
                stringBuilder.append(" ORDER BY o.responsibility ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("responsibility").getDirection());
            } else if (pageRequest.getSort().getOrderFor("organization") != null) {
                stringBuilder.append(" order by o.employee.database.name ");
                stringBuilder.append(pageRequest.getSort().getOrderFor("organization").getDirection());
            }
        }

        final TypedQuery<EventNotification> query = entityManager.createQuery(stringBuilder.toString(), EventNotification.class);
        query.setParameter("eventId", eventId);
        applyPageable(query, pageRequest);

        return query.getResultList();
    }
}
