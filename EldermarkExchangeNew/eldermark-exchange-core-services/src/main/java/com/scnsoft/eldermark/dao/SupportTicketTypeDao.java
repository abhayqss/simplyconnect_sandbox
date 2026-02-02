package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SupportTicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketTypeDao extends JpaRepository<SupportTicketType, Long> {
}
