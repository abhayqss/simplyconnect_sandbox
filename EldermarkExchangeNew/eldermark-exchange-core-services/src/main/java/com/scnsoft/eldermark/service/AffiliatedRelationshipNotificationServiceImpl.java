package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AffiliatedRelationshipNotificationDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dto.AffiliatedNotificationDto;
import com.scnsoft.eldermark.entity.AffiliatedRelationshipNotification;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.service.notification.sender.AffiliatedRelationshipNotificationSender;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffiliatedRelationshipNotificationServiceImpl implements AffiliatedRelationshipNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AffiliatedRelationshipNotificationServiceImpl.class);

    @Value("${affiliated.relationship.notification.enabled}")
    private boolean affiliatedRelationshipNotificationsEnabled;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private AffiliatedRelationshipNotificationDao affiliatedRelationshipNotificationDao;

    @Autowired
    private AffiliatedRelationshipNotificationSender affiliatedRelationshipNotificationSender;

    @Override
    public void sentNotification(AffiliatedNotificationDto dto, Collection<Long> recipientIds) {
        if (CollectionUtils.isEmpty(recipientIds)) {
            logger.info("Recipients are empty for affiliated relationship notifications");
        }
        var notifications = prepareNotifications(dto, recipientIds);
        if (affiliatedRelationshipNotificationsEnabled) {
            notifications.stream()
                    .filter(n -> Strings.isNotEmpty(n.getDestination()))
                    .map(AffiliatedRelationshipNotification::getId)
                    .forEach(affiliatedRelationshipNotificationSender::send);
        } else {
            logger.info("Sending of affiliated relationship notifications is disabled");
        }
    }

    private List<AffiliatedRelationshipNotification> prepareNotifications(
        AffiliatedNotificationDto dto,
        Collection<Long> recipientIds
    ) {
        var notifications = recipientIds.stream()
                .map(employeeDao::getOne)
                .map(recipient -> {
                    var notification = new AffiliatedRelationshipNotification();
                    notification.setPrimaryOrganizationId(dto.getPrimaryOrganizationId());
                    notification.setAffiliatedOrganizationId(dto.getAffiliatedOrganizationId());
                    notification.setCreatedDatetime(Instant.now());
                    notification.setAuthor(loggedUserService.getCurrentEmployee());
                    notification.setReceiver(recipient);
                    notification.setDestination(PersonTelecomUtils.findValue(recipient.getPerson(), PersonTelecomCode.EMAIL).orElse(null));
                    notification.setTerminated(dto.isTerminated());
                    return notification;
                })
                .collect(Collectors.toList());
        return affiliatedRelationshipNotificationDao.saveAll(notifications);
    }
}
