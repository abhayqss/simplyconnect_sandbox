package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.SupportTicketType;

import java.util.List;

public interface SupportTicketTypeService {

    SupportTicketType findById(Long id);

    List<SupportTicketType> findAll();
}
