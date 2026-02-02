package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.support.SubmitSupportTicketDto;
import com.scnsoft.eldermark.entity.SupportTicket;

public interface SupportTicketService extends ProjectingService<Long>{

    SupportTicket submit(SubmitSupportTicketDto dto);
}
