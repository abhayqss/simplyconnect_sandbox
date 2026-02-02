package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.exchange.model.target.OrganizationAddress;

public interface OrganizationAddressResolver {
    OrganizationAddress getCompanyAddress(long companyId);
}
