package com.scnsoft.eldermark.mobile.converters.client.location;

import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryListItemDto;
import com.scnsoft.eldermark.mobile.projection.client.location.ClientLocationHistoryListItem;
import com.scnsoft.eldermark.util.DateTimeUtils;

public class BaseClientLocationHistoryDtoConverter {

    protected void fillBase(ClientLocationHistoryListItem source, ClientLocationHistoryListItemDto target) {
        target.setId(source.getId());
        target.setClientId(source.getClientId());
        target.setSeenDatetime(DateTimeUtils.toEpochMilli(source.getSeenDatetime()));
        target.setLongitude(source.getLongitude());
        target.setLatitude(source.getLatitude());
    }

}
