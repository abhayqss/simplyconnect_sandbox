package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.SupportTicketSubmittedNotification;

import java.util.List;

public interface SupportTicketNotificationService {

    void sendNotifications(List<SupportTicketSubmittedNotification> notifications);
}
