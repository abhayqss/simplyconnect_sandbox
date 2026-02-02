package com.scnsoft.eldermark.converter.dto2entity.support;

import com.scnsoft.eldermark.dto.support.SubmitSupportTicketDto;
import com.scnsoft.eldermark.dto.support.SupportTicketDto;
import com.scnsoft.eldermark.service.SupportTicketTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SubmitSupportTicketConverter implements Converter<SupportTicketDto, SubmitSupportTicketDto> {

    @Autowired
    private SupportTicketTypeService supportTicketTypeService;

    @Override
    public SubmitSupportTicketDto convert(SupportTicketDto source) {
        var target = new SubmitSupportTicketDto();

        target.setAuthorPhoneNumber(source.getPhone());
        target.setMessage(source.getMessageText());
        if (CollectionUtils.isNotEmpty(source.getAttachmentFiles())) {
            target.setAttachmentFiles(source.getAttachmentFiles());
        } else {
            target.setAttachmentFiles(new ArrayList<>());
        }
        target.setType(supportTicketTypeService.findById(source.getTypeId()));

        return target;
    }
}
