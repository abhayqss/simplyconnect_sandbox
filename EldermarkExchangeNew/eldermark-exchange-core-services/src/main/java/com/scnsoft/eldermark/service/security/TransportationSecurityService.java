package com.scnsoft.eldermark.service.security;

public interface TransportationSecurityService {

    boolean canRequestNewRide(Long clientId);

    boolean canViewRideHistory(Long clientId);

    boolean hasAccess();
}
