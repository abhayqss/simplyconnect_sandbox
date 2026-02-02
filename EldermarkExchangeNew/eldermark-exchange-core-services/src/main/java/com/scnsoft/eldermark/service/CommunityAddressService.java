package com.scnsoft.eldermark.service;

public interface CommunityAddressService {

	void populateAllLocationForOutdatedAddresses(Long organizationId);

	void populateAllLocationForOutdatedAddresses();
}
