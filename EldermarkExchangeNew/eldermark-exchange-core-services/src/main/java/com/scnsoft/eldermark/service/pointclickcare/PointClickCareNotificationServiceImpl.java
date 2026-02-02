package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdAboutToHitNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdReachedNotificationQueueDto;
import com.scnsoft.eldermark.dto.pointclickcare.notification.PccDailyThresholdResetNotificationQueueDto;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpMethod;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
public class PointClickCareNotificationServiceImpl implements PointClickCareNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PointClickCareNotificationServiceImpl.class);

    private final JmsTemplate jmsTemplate;
    private final String dailyLimitAboutToHitDestination;
    private final String dailyLimitReachedDestination;
    private final String dailyLimitResetDestination;
    private final boolean jmsSendingEnabled;
    private final boolean sendApiErrorsEnabled;
    private final List<String> apiErrorsEmails;
    private final String profile;
    private final ExchangeMailService exchangeMailService;


    @Autowired
    public PointClickCareNotificationServiceImpl(JmsTemplate jmsTemplate,
                                                 @Value("${jms.queue.pcc.notification.dailyLimitAboutToHit.destination}") String dailyLimitAboutToHitDestination,
                                                 @Value("${jms.queue.pcc.notification.dailyLimitReached.destination}") String dailyLimitReachedDestination,
                                                 @Value("${jms.queue.pcc.notification.dailyLimitReset.destination}") String dailyLimitResetDestination,
                                                 @Value("${jms.sending.enabled}") boolean jmsSendingEnabled,
                                                 @Value("${pcc.api-errors.notification.enabled}") boolean sendApiErrorsEnabled,
                                                 @Value("#{'${pcc.api-errors.notification.emails}'.split(',')}") List<String> apiErrorsEmails,
                                                 @Value("${portal.url}") String profile,
                                                 ExchangeMailService exchangeMailService) {
        this.jmsTemplate = jmsTemplate;
        this.dailyLimitAboutToHitDestination = dailyLimitAboutToHitDestination;
        this.dailyLimitReachedDestination = dailyLimitReachedDestination;
        this.dailyLimitResetDestination = dailyLimitResetDestination;
        this.jmsSendingEnabled = jmsSendingEnabled;
        this.sendApiErrorsEnabled = sendApiErrorsEnabled;
        this.apiErrorsEmails = apiErrorsEmails;
        this.profile = profile;
        this.exchangeMailService = exchangeMailService;
    }

    @Override
    public boolean sendDailyThresholdAboutToHit(int percentsRemaining, int requestsRemaining, int limit, Instant resetAt) {
        var dto = new PccDailyThresholdAboutToHitNotificationQueueDto(percentsRemaining, requestsRemaining, limit, resetAt);

        if (!jmsSendingEnabled) {
            logger.info("Called PointClickCareNotificationServiceImpl.sendDailyThresholdAboutToHit - jms is disabled.");
            return false;
        }
        try {
            jmsTemplate.convertAndSend(dailyLimitAboutToHitDestination, dto);
            return true;
        } catch (JmsException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean sendDailyThresholdReached(Instant resetAt) {
        var dto = new PccDailyThresholdReachedNotificationQueueDto(resetAt);

        if (!jmsSendingEnabled) {
            logger.info("Called PointClickCareNotificationServiceImpl.sendDailyThresholdReached - jms is disabled.");
            return false;
        }
        try {
            jmsTemplate.convertAndSend(dailyLimitReachedDestination, dto);
            return true;
        } catch (JmsException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean resetDailyThreshold(Instant resetAt) {
        var dto = new PccDailyThresholdResetNotificationQueueDto(resetAt);

        if (!jmsSendingEnabled) {
            logger.info("Called PointClickCareNotificationServiceImpl.reset - jms is disabled.");
            return false;
        }
        try {
            jmsTemplate.convertAndSend(dailyLimitResetDestination, dto);
            return true;
        } catch (JmsException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean sendApiError(PointClickCareApiException apiException, HttpMethod method, String url, Map<String, Object> pathVariables) {
        try {
            if (!sendApiErrorsEnabled) {
                logger.info("Won't send api error - notifications are disabled. Api exception - {}", apiException.getMessage());
                return false;
            }
            if (CollectionUtils.isEmpty(apiErrorsEmails)) {
                logger.info("Won't send api error - no receiver email specified. Api exception - {}", apiException.getMessage());
                return false;
            }
            logger.info("Point Click Care Notification: API exception {}", apiException.getMessage());

            var subject = "PointClickCare Api Error";
            var message = "Environment: " + profile + "\n" +
                    "URL: " + method + " " + url + "\n" +
                    "Path variables: " + pathVariables + "\n" +
                    "Error message: " + apiException.getMessage();
            exchangeMailService.sendSimpleEmail(apiErrorsEmails, subject, message);
            logger.info("Point Click Care Notification: API exception notification");
            return true;
        } catch (Exception e) {
            logger.error("Exception during API exception notification", e);
            return false;
        }
    }
}
