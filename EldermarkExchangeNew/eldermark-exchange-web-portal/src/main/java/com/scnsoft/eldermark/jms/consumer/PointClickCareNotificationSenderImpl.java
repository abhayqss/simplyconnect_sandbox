package com.scnsoft.eldermark.jms.consumer;

import com.scnsoft.eldermark.config.WebJmsReceiveConfig;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdAboutToHitNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdReachedNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdResetNotificationQueueDto;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.service.pointclickcare.PccIntegrationOrPatientMatchEnabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@ConditionalOnBean(WebJmsReceiveConfig.class)
@Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
public class PointClickCareNotificationSenderImpl implements PointClickCareNotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCareNotificationSenderImpl.class);
    private final Set<Integer> thresholdNotificationsSent = new HashSet<>();

    private Instant currentResetAt;

    @Value("#{'${pcc.dailyRate.notification.thresholds}'.split(',')}")
    private List<Integer> sendingThresholds;

    @Value("#{'${pcc.dailyRate.notification.emails}'.split(',')}")
    private List<String> emails;

    @Value("${portal.url}")
    private String profile;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @JmsListener(
            destination = "${jms.queue.pcc.notification.dailyLimitAboutToHit.destination}",
            concurrency = "${jms.queue.pcc.notification.dailyLimitAboutToHit.concurrency}",
            containerFactory = "pccNotificationDailyLimitAboutToHitJmsListenerContainerFactory"
    )
    @Override
    public void sendDailyThresholdAboutToHit(PccDailyThresholdAboutToHitNotificationQueueDto dto) {
        logger.info("Point Click Care Notification: Received sendDailyThresholdAboutToHit notification");
        if (sendingThresholds.contains(dto.getPercentsRemaining()) && thresholdNotificationsSent.add(dto.getPercentsRemaining())) {
            logger.info("Point Click Care Notification: Sending sendDailyThresholdAboutToHit notification");
            var subject = "PointClickCare Daily Rate: " + dto.getPercentsRemaining() + "% API calls remaining";
            var message = "Environment: " + profile + "\n" +
                    "PointClickCare Daily Rate: " + dto.getPercentsRemaining() + "% API calls remaining (" +
                    dto.getRequestsRemaining() + " out of " + dto.getLimit() + "). Reset at " + dto.getResetAt();
            exchangeMailService.sendSimpleEmail(emails, subject, message);
            logger.info("Point Click Care Notification: Sent sendDailyThresholdAboutToHit notification");

        }
    }

    @JmsListener(
            destination = "${jms.queue.pcc.notification.dailyLimitReached.destination}",
            concurrency = "${jms.queue.pcc.notification.dailyLimitReached.concurrency}",
            containerFactory = "pccNotificationDailyLimitReachedJmsListenerContainerFactory"
    )
    @Override
    public void sendDailyThresholdReached(PccDailyThresholdReachedNotificationQueueDto dto) {
        logger.info("Point Click Care Notification: Received sendDailyThresholdReached notification");
        if (thresholdNotificationsSent.add(0)) {
            logger.info("Point Click Care Notification: Sending sendDailyThresholdReached notification");
            var subject = "PointClickCare Daily Rate: Stopped";
            var message = "Environment: " + profile + "\n" +
                    "Stopped PointClickCare API calls because daily threshold is reached. Reset at " + dto.getResetAt();
            exchangeMailService.sendSimpleEmail(emails, subject, message);
            logger.info("Point Click Care Notification: Sent sendDailyThresholdReached notification");

        }
    }

    @JmsListener(
            destination = "${jms.queue.pcc.notification.dailyLimitReset.destination}",
            concurrency = "${jms.queue.pcc.notification.dailyLimitReset.concurrency}",
            containerFactory = "pccNotificationDailyLimitResetJmsListenerContainerFactory"
    )
    @Override
    public void reset(PccDailyThresholdResetNotificationQueueDto dto) {
        logger.info("Point Click Care Notification: Received reset");
        if (currentResetAt == null || currentResetAt.isBefore(dto.getResetAt())) {
            logger.info("Point Click Care Notification: Performing reset");
            thresholdNotificationsSent.clear();
            this.currentResetAt = dto.getResetAt();
        }
    }

}
