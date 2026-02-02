package com.scnsoft.eldermark.web.commons.service;

import com.scnsoft.eldermark.web.commons.dto.basic.ApiRequestLoggingNotificationDto;

public interface ApiRequestLoggingNotificationService {

    void sendEmailNotifications(ApiRequestLoggingNotificationDto dto);
}
