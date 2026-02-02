package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.EventNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface EventNotificationDao extends BaseDao<EventNotification> {
    List<EventNotification> listNotSendByEvent(Long eventId);

    List<EventNotification> listByEventId(long eventId, Pageable pageRequest, Boolean isSend);
    Long countByEventId(long eventId, Boolean isSend);

    void updateDelivered(Long eventNotificationId);

    List<EventNotification> getEventNotificationsByEmployeeId(Long employeeId);

    List<Long> getAdminEventNotificationsEventIdsByEmployeeId(Long employeeId, Set<Long> eventIds);
}
