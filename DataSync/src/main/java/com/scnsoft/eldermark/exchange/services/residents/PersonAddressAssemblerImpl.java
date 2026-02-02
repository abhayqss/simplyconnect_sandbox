package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.OrganizationAddress;
import com.scnsoft.eldermark.exchange.model.target.PersonAddress;
import com.scnsoft.eldermark.exchange.model.target.PersonType;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import org.springframework.stereotype.Component;

@Component
public class PersonAddressAssemblerImpl implements PersonAddressAssembler {
    @Override
    public PersonAddress.Updatable createAddressUpdatable(ResidentData sourceResident,
                                                          OrganizationAddress companyAddress) {
        String streetAddress = companyAddress.getStreetAddress() + ", Unit# " + sourceResident.getUnitNumber();

        PersonAddress.Updatable updatable = new PersonAddress.Updatable();
        updatable.setPostalAddressUse("HP");
        updatable.setStreetAddress(streetAddress);
        updatable.setCity(companyAddress.getCity());
        updatable.setState(companyAddress.getState());
        updatable.setCountry(companyAddress.getCountry());
        updatable.setPostalCode(companyAddress.getPostalCode());
        return updatable;
    }

    @Override
    public PersonAddress.Updatable createEmptyAddressUpdatable() {
        PersonAddress.Updatable updatable = new PersonAddress.Updatable();
        updatable.setPostalAddressUse("HP");
        return updatable;
    }

    @Override
    public PersonAddress createEmptyAddress(ResidentData sourceResident, long personId, long databaseId) {
        PersonAddress address = new PersonAddress();
        address.setPersonId(personId);
        address.setUpdatable(createEmptyAddressUpdatable());
        address.setDatabaseId(databaseId);
        address.setLegacyTable(PersonType.RESIDENT.getTableName());
        address.setLegacyId(sourceResident.getId().toString());
        return address;
    }

    @Override
    public PersonAddress createAddress(ResidentData sourceResident, OrganizationAddress facilityAddress,
                                       long personId, long databaseId) {
        PersonAddress address = new PersonAddress();
        address.setPersonId(personId);
        address.setUpdatable(createAddressUpdatable(sourceResident, facilityAddress));
        address.setDatabaseId(databaseId);
        address.setLegacyTable(PersonType.RESIDENT.getTableName());
        address.setLegacyId(sourceResident.getId().toString());
        return address;
    }

    @Override
    public PersonAddress.Updatable createAddressUpdatable(EmployeeData sourceEmployee) {
        PersonAddress.Updatable updatable = new PersonAddress.Updatable();
        updatable.setPostalAddressUse("HP");
        updatable.setStreetAddress(sourceEmployee.getAddress());
        updatable.setCity(sourceEmployee.getCity());
        updatable.setState(sourceEmployee.getState());
        updatable.setCountry("US");
        updatable.setPostalCode(sourceEmployee.getZip());
        return updatable;
    }

    @Override
    public PersonAddress createAddress(EmployeeData sourceEmployee, long personId, DatabaseInfo database) {
        PersonAddress address = new PersonAddress();
        address.setPersonId(personId);
        address.setUpdatable(createAddressUpdatable(sourceEmployee));
        address.setDatabaseId(database.getId());
        address.setLegacyTable(PersonType.EMPLOYEE.getTableName());
        address.setLegacyId(sourceEmployee.getId());
        return  address;
    }
}
