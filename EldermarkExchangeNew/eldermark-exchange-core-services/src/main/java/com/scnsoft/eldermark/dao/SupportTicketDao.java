package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.SupportTicket;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketDao extends AppJpaRepository<SupportTicket, Long> {
}
