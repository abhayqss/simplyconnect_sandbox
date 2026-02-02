package com.scnsoft.eldermark.service.assessment.arizona;

import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class ArizonaMatrixMonthlyNotificationSenderImpl implements ArizonaMatrixMonthlyNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(ArizonaMatrixMonthlyNotificationSenderImpl.class);

    @Value("${arizona.matrix.monthly.notification.timezone}")
    private String timeZone;

    @Autowired
    private ArizonaMatrixMonthlyNotificationService arizonaMatrixMonthlyNotificationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Override
    @Scheduled(cron = "${arizona.matrix.monthly.notification.cron}", zone = "${arizona.matrix.monthly.notification.timezone}")
    public void sendNotifications() {

        var now = LocalDateTime.now(ZoneId.of(timeZone));
        var from = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0).toInstant(ZoneOffset.UTC);
        var to = DateTimeUtils.plusMonths(from, 1).minusMillis(1);

        logger.info("Sending Arizona Matrix Monthly report {} - {} started", from, to);

        assessmentService.findOrganizationIdsWithEnabledAssessment(Assessment.ARIZONA_SSM)
            .forEach(organizationId ->
                arizonaMatrixMonthlyNotificationService.generateNotifications(organizationId, from, to).stream()
                    .map(exchangeMailService::sendArizonaMatrixMonthlyNotification)
                    .forEach(it -> {
                        try {
                            it.get();
                        } catch (Exception ignore) {
                        }
                    })
            );

        logger.info("Sending Arizona Matrix Monthly report finished");
    }
}
