package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SupportTicketSubmittedNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketSubmittedNotificationDao extends JpaRepository<SupportTicketSubmittedNotification, Long> {
}
