package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SupportTicket;
import com.scnsoft.eldermark.entity.SupportTicketReceiverConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketReceiverConfigurationDao extends JpaRepository<SupportTicketReceiverConfiguration, Long> {
}
