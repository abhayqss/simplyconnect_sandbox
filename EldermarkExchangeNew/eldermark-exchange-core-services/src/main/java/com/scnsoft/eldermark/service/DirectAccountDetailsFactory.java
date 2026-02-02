package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.Organization;

public interface DirectAccountDetailsFactory {

	DirectAccountDetails createMailAccountDetails(Employee employee);

    DirectAccountDetails createOrganizationAccountDetails(Organization organization);

    DirectAccountDetails createMarketplaceAccountDetails(Marketplace marketplace);

}
