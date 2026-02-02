package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Organization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Marketplace;

@Component
public class DirectAccountDetailsFactoryImpl implements DirectAccountDetailsFactory {

    private @Value("${secure.email.domain}") String domain;

    @Value("${secure.email.marketplace.sender}")
    private String marketplaceSender;

    @Override
    public DirectAccountDetails createMailAccountDetails(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("userDetails cannot be null");
        }
        String secureMessaging = employee.getSecureMessaging();
        String companyCode = employee.getOrganization().getSystemSetup().getLoginCompanyId().toLowerCase();

        return new DirectAccountDetails(secureMessaging, companyCode, domain);
    }

    @Override
    public DirectAccountDetails createOrganizationAccountDetails(Organization organization) {
        String companyCode = organization.getSystemSetup().getLoginCompanyId().toLowerCase();
//        companyCode = "rba"; //todo testing only
        return new DirectAccountDetails(null, companyCode, domain);
    }

    @Override
    public DirectAccountDetails createMarketplaceAccountDetails(Marketplace marketplace) {
        if (marketplace == null) {
            throw new IllegalArgumentException("marketplace cannot be null");
        }
        return new DirectAccountDetails(null, marketplaceSender, domain);
    }

}
