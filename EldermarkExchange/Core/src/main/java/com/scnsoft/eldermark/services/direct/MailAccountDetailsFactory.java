package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;

public interface MailAccountDetailsFactory {
    DirectAccountDetails createMailAccountDetails(ExchangeUserDetails userDetails);
    DirectAccountDetails createMailAccountDetails(Employee employee);

    DirectAccountDetails createRootAccountDetails(Employee employee);

    DirectAccountDetails createMarketplaceAccountDetails(Marketplace marketplace);
}
