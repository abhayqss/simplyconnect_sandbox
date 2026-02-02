package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.TransportationDto;

public interface TransportationFacade {

    TransportationDto requestNewRide(Long clientId);

    TransportationDto rideHistory(Long clientId);

    boolean hasAccess();
}
