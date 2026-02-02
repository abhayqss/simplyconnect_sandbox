package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.client.ClientDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientListItemDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryListItemDto;
import com.scnsoft.eldermark.mobile.dto.employee.FavouriteDto;
import com.scnsoft.eldermark.mobile.facade.ClientFacade;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientFacade clientFacade;

    @GetMapping
    public Response<List<ClientListItemDto>> find(MobileClientFilter filter,
                                                  Pageable pageRequest) {
        return Response.pagedResponse(clientFacade.find(filter, pageRequest));
    }

    @GetMapping("/{clientId}")
    public Response<ClientDto> findById(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientFacade.findById(clientId));
    }

    @PutMapping("/{clientId}/favourite")
    public Response<Void> setFavourite(@PathVariable("clientId") Long clientId, @Valid @RequestBody FavouriteDto favouriteDto) {
        clientFacade.setFavourite(clientId, favouriteDto.getFavourite());
        return Response.successResponse();
    }

    @GetMapping("/{clientId}/location-history")
    public Response<List<ClientLocationHistoryListItemDto>> findLocationHistory(@PathVariable("clientId") Long clientId,
                                                                                Pageable pageRequest) {
        return Response.pagedResponse(clientFacade.findLocationHistory(clientId, pageRequest));
    }

    @PutMapping("/{clientId}/location-history")
    public Response<Long> reportClientLocation(@PathVariable("clientId") Long clientId,
                                               @Valid @RequestBody ClientLocationHistoryDto locationHistoryDto) {
        locationHistoryDto.setClientId(clientId);
        return Response.successResponse(clientFacade.reportLocation(locationHistoryDto));
    }

    @GetMapping("/{clientId}/location-history/{locationId}")
    public Response<ClientLocationHistoryDto> findLocationHistoryById(@PathVariable("locationId") Long locationId) {
        return Response.successResponse(clientFacade.findLocationHistoryById(locationId));
    }
}
