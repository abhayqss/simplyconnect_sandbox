package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailAccountDetailsFactoryImpl implements MailAccountDetailsFactory {

    private
    @Value("${secure.email.domain}")
    String domain;

    @Value("${secure.email.marketplace.sender}")
    private String marketplaceSender;

    @Autowired
    private DatabasesDao databasesDao;

    @Override
    public DirectAccountDetails createMailAccountDetails(ExchangeUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("userDetails cannot be null");
        }

        String secureMessaging = userDetails.getSecureMessaging();
        String companyCode = userDetails.getEmployee().getDatabase().getSystemSetup().getLoginCompanyId().toLowerCase();

        return new DirectAccountDetails(secureMessaging, companyCode, domain);
    }

    @Override
    public DirectAccountDetails createMailAccountDetails(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("userDetails cannot be null");
        }
        String secureMessaging = employee.getSecureMessaging();
        String companyCode = employee.getDatabase().getSystemSetup().getLoginCompanyId().toLowerCase();

        return new DirectAccountDetails(secureMessaging, companyCode, domain);
    }

    @Override
    public DirectAccountDetails createRootAccountDetails(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("userDetails cannot be null");
        }

        String companyCode = employee.getDatabase().getSystemSetup().getLoginCompanyId().toLowerCase();

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
