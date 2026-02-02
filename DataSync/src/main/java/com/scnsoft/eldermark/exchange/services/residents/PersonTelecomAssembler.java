package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.PersonTelecom;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.List;

public interface PersonTelecomAssembler {
    PersonTelecom.Updatable createHomePhoneUpdatable(ResidentData resident);

    PersonTelecom.Updatable createOtherPhoneUpdatable(ResidentData resident);

    PersonTelecom.Updatable createEmailUpdatable(ResidentData resident);

    PersonTelecom createHomePhoneTelecom(EmployeeData employeeData, long personId, DatabaseInfo database);

    List<PersonTelecom> createAllTelecoms(ResidentData resident, long personId, long databaseId);

    PersonTelecom.Updatable createEmailUpdatable(EmployeeData employeeData);

    PersonTelecom.Updatable createHomePhoneUpdatable(EmployeeData employeeData);

    PersonTelecom createEmailTelecom(EmployeeData employeeData, long personId, DatabaseInfo database);
}
