package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.TransportationDto;

public interface TransportationService {

    TransportationDto requestNewRide(Long clientId);

    TransportationDto rideHistory(Long clientId);
}
