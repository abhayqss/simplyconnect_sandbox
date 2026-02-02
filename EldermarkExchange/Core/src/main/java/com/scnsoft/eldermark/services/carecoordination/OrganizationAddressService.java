package com.scnsoft.eldermark.services.carecoordination;

public interface OrganizationAddressService {
    /**
     * sets longitude and latitude for addresses which are not up-to-date
     */
    void populateAllLocationForOutdatedAddresses();
}
