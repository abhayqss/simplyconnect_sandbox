package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.client.ClientDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientListItemDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryListItemDto;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientFacade {

    Page<ClientListItemDto> find(MobileClientFilter filter, Pageable pageRequest);

    ClientDto findById(Long clientId);

    void setFavourite(Long clientId, boolean favourite);

    Page<ClientLocationHistoryListItemDto> findLocationHistory(Long clientId, Pageable pageable);

    ClientLocationHistoryDto findLocationHistoryById(Long locationId);

    Long reportLocation(ClientLocationHistoryDto clientLocationHistoryDto);
}
