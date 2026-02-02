package com.scnsoft.eldermark.services.task;

import com.scnsoft.eldermark.dao.carecoordination.EmployeeRequestDao;
import com.scnsoft.eldermark.dao.carecoordination.EventNotificationDao;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.carecoordination.EventNotificationProcessService;
import com.scnsoft.eldermark.services.carecoordination.EventNotificationService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author mradzivonenka
 * @author phomal
 * @author pzhurba
 *
 * Created by pzhurba on 04-Nov-15.
 */
@Conditional(value = EmployeeRequestCleanerRunCondition.class)
@Service
public class EmployeeRequestCleanerImpl implements EmployeeRequestCleaner {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRequestCleanerImpl.class);
    private static final Long ONE_DAY = 1000L * 60 * 60 * 24;
    private static final Long THREE_DAYS = ONE_DAY * 3L;

    @Autowired
    EmployeeRequestDao employeeRequestDao;
    @Autowired
    EmployeeRequestService employeeRequestService;
    @Autowired
    EventNotificationDao eventNotificationDao;
    @Autowired
    EventNotificationService eventNotificationService;
    @Autowired
    EventNotificationProcessService eventNotificationProcessService;
    @Autowired
    EventService eventService;

    private long msInvitationExpiration;

    public long getMsInvitationExpiration() {
        return msInvitationExpiration;
    }

    @Value("${invitation.expiration.time.ms:0}")
    public void setMsInvitationExpiration(long msInvitationExpiration) {
        if (msInvitationExpiration < 0) {
            throw new IllegalArgumentException("Invalid \"invitation.expiration.time.ms\" property: expected a positive number or 0, but got " +
                    msInvitationExpiration + ".");
        }
        this.msInvitationExpiration = msInvitationExpiration;
    }

    @Scheduled(cron = "${invitation.expiration.cron.expression}")
    public void cleanExpiredInvitation() {
        logger.info("Run Requests Cleaner ... ");
        cleanInvitations();
        clearResetPasswordRequests();

        logger.info("Requests Cleaner finished. ");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void cleanInvitations() {
        final Date expiredDate = new Date(System.currentTimeMillis() - msInvitationExpiration);
        final List<EmployeeRequest> requests = employeeRequestDao.getExpiredRequests(expiredDate, EmployeeRequestType.INVITE);

        for (EmployeeRequest employeeRequest : requests) {
            logger.info("Deleting request and target employee : " + employeeRequest.toString());

//            assignEventNotificationsToAdmins(employeeRequest);

            employeeRequestService.expireInvitation(employeeRequest);
        }
    }

    private void assignEventNotificationsToAdmins(EmployeeRequest employeeRequest) {
        Long employeeId = employeeRequest.getTargetEmployee().getId();
        List<EventNotification> employeeNotifications = eventNotificationDao.getEventNotificationsByEmployeeId(employeeId);
        if (CollectionUtils.isNotEmpty(employeeNotifications)) {
            Set<Long> employeeEventIds = new HashSet<Long>();
            for (EventNotification employeeNotification : employeeNotifications) {
                employeeEventIds.add(employeeNotification.getEvent().getId());
            }
            List<Long> adminNotificationsEventIds = eventNotificationDao.getAdminEventNotificationsEventIdsByEmployeeId(employeeId, employeeEventIds);

            for (EventNotification employeeNotification : employeeNotifications) {
                Event event = employeeNotification.getEvent();
                if (!adminNotificationsEventIds.contains(event.getId())) {
                    eventNotificationService.createNotificationsForAdmin(employeeNotification);
                }
                eventNotificationDao.delete(employeeNotification);
                for (EventNotification eventNotification : eventNotificationDao.listNotSendByEvent(event.getId())) {
                    EventDto eventDto = eventService.getEventDetails(event.getId());
                    eventNotificationProcessService.processNotification(eventNotification, eventDto);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void clearResetPasswordRequests() {
        final Date expiredDate = new Date(System.currentTimeMillis() - ONE_DAY);
        final List<EmployeeRequest> requests = employeeRequestDao.getExpiredRequests(expiredDate, EmployeeRequestType.RESET_PASSWORD);

        for (EmployeeRequest employeeRequest : requests) {
            logger.info("Deleting reset password request : " + employeeRequest.toString());
            employeeRequestDao.delete(employeeRequest);
        }
        employeeRequestDao.flush();
    }
}
