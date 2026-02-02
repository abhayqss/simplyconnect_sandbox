package com.scnsoft.eldermark.mobile.converters.client.location;

import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryListItemDto;
import com.scnsoft.eldermark.mobile.projection.client.location.ClientLocationHistoryListItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientLocationHistoryListItemDtoConverter
        extends BaseClientLocationHistoryDtoConverter
        implements Converter<ClientLocationHistoryListItem, ClientLocationHistoryListItemDto> {

    @Override
    public ClientLocationHistoryListItemDto convert(ClientLocationHistoryListItem source) {
        var target = new ClientLocationHistoryListItemDto();
        fillBase(source, target);
        return target;
    }
}
