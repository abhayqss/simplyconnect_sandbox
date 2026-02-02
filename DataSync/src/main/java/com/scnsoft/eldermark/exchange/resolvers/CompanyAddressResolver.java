package com.scnsoft.eldermark.exchange.resolvers;

import com.scnsoft.eldermark.exchange.model.target.OrganizationAddress;

public interface CompanyAddressResolver {
    OrganizationAddress getCompanyAddress(long companyId);
}
