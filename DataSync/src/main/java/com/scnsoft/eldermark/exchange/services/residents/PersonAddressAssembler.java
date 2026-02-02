package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.OrganizationAddress;
import com.scnsoft.eldermark.exchange.model.target.PersonAddress;
import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface PersonAddressAssembler {
    PersonAddress.Updatable createEmptyAddressUpdatable();

    PersonAddress.Updatable createAddressUpdatable(ResidentData sourceResident,
                                                   OrganizationAddress companyAddress);

    PersonAddress createAddress(ResidentData sourceResident, OrganizationAddress facilityAddress,
                                long personId, long databaseId);

    PersonAddress createEmptyAddress(ResidentData sourceResident, long personId, long databaseId);

    PersonAddress.Updatable createAddressUpdatable(EmployeeData sourceEmployee);

    PersonAddress createAddress(EmployeeData sourceEmployee,long personId, DatabaseInfo database);
}
