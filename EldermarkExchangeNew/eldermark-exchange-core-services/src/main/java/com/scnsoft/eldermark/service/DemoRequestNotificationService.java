package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.DemoRequest;

public interface DemoRequestNotificationService {
    void sendDemoRequestSubmittedNotifications(DemoRequest demoRequest);
}
