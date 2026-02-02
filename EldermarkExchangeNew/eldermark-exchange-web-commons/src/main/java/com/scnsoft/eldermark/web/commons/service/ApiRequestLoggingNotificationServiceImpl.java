package com.scnsoft.eldermark.web.commons.service;

import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.web.commons.dto.basic.ApiRequestLoggingNotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiRequestLoggingNotificationServiceImpl implements ApiRequestLoggingNotificationService {

    private static final String SUBJECT = "Request execution time notification";

    @Autowired
    private ExchangeMailService mailService;

    @Value(value = "${request.execution.time}")
    private long triggerExecutionTime;

    @Value(value = "${request.execution.notification.enabled}")
    private boolean isNotificationEnabled;

    @Value("#{'${request.execution.notification.emails}'.split(',')}")
    private List<String> emails;

    @Override
    @Async
    public void sendEmailNotifications(ApiRequestLoggingNotificationDto dto) {
        if (isNotificationEnabled) {
            if (dto.getExecutionTime() >= triggerExecutionTime) {
                var message =
                        "Environment: " + dto.getEnvironment() + "\n" +
                                "Request: " + dto.getMethod() + " " + dto.getEndpoint() + "\n\n" +
                                "Request params: " + dto.getQueryString() + "\n\n" +
                                "Employee id: " + dto.getEmployeeId() + "\n" +
                                "Started: " + dto.getStartTime() + "\n" +
                                "Execution time: " + String.format("%.3f", dto.getExecutionTime()) + " s";
                mailService.sendSimpleEmail(emails, SUBJECT, message);
            }
        }
    }
}
