package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.support.SubmitSupportTicketDto;
import com.scnsoft.eldermark.dto.support.SupportTicketDto;
import com.scnsoft.eldermark.service.SupportTicketService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class SupportTicketFacadeImpl implements SupportTicketFacade {

    @Autowired
    private Converter<SupportTicketDto, SubmitSupportTicketDto> submitSupportTicketConverter;

    @Autowired
    private SupportTicketService supportTicketService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @Transactional
    // todo auth
    public Long create(SupportTicketDto dto) {
        var submitDto = Objects.requireNonNull(submitSupportTicketConverter.convert(dto));

        submitDto.setAuthor(loggedUserService.getCurrentEmployee());
        submitDto.setCreationDate(Instant.now());

        return supportTicketService.submit(submitDto).getId();
    }
}
