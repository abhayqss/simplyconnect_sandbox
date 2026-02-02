package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.TransportationDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.TransportationService;
import com.scnsoft.eldermark.service.security.TransportationSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class TransportationFacadeImpl implements TransportationFacade {

    @Autowired
    private TransportationService transportationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransportationSecurityService transportationSecurityService;

    @Override
    @PreAuthorize("@transportationSecurityService.canRequestNewRide(#clientId)")
    public TransportationDto requestNewRide(@P("clientId") Long clientId) {
        clientService.validateActive(clientId);
        return transportationService.requestNewRide(clientId);
    }

    @Override
    @PreAuthorize("@transportationSecurityService.canViewRideHistory(#clientId)")
    public TransportationDto rideHistory(@P("clientId") Long clientId) {
        return transportationService.rideHistory(clientId);
    }

    @Override
    public boolean hasAccess() {
        return transportationSecurityService.hasAccess();
    }
}
