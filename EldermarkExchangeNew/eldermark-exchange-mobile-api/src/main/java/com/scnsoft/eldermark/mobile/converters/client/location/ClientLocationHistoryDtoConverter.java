package com.scnsoft.eldermark.mobile.converters.client.location;

import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryDto;
import com.scnsoft.eldermark.mobile.projection.client.location.ClientLocationHistoryItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientLocationHistoryDtoConverter
        extends BaseClientLocationHistoryDtoConverter
        implements Converter<ClientLocationHistoryItem, ClientLocationHistoryDto> {

    @Override
    public ClientLocationHistoryDto convert(ClientLocationHistoryItem source) {
        var target = new ClientLocationHistoryDto();
        fillBase(source, target);

        target.setReportedByFirstName(source.getReportedByFirstName());
        target.setReportedByLastName(source.getReportedByLastName());
        return target;
    }
}
