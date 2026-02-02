package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.SyncQualifiers;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.PersonTelecom;
import com.scnsoft.eldermark.exchange.model.target.PersonType;
import com.scnsoft.eldermark.exchange.model.target.TelecomUseCodes;
import com.scnsoft.eldermark.exchange.normalizers.PersonPhonesNormalizer;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonTelecomAssemblerImpl implements PersonTelecomAssembler {
    @Autowired
    private PersonPhonesNormalizer phonesNormalizer;

    @Override
    public PersonTelecom.Updatable createHomePhoneUpdatable(ResidentData resident) {
        return createPhoneUpdatable(resident.getHomePhone(), TelecomUseCodes.HOME_PHONE);
    }

    @Override
    public PersonTelecom.Updatable createOtherPhoneUpdatable(ResidentData resident) {
        return createPhoneUpdatable(resident.getOtherPhone(), TelecomUseCodes.OTHER_PHONE);
    }

    @Override
    public PersonTelecom.Updatable createEmailUpdatable(ResidentData resident) {
        String email = resident.getEmail();

        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(email);
        updatable.setValueNormalized(email);
        updatable.setUseCode(TelecomUseCodes.EMAIL.getValue());
        return updatable;
    }

    @Override
    public PersonTelecom.Updatable createEmailUpdatable(EmployeeData employeeData) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(employeeData.getEmail());
        updatable.setValueNormalized(employeeData.getEmail());
        updatable.setUseCode(TelecomUseCodes.EMAIL.getValue());
        return updatable;
    }

    @Override
    public PersonTelecom.Updatable createHomePhoneUpdatable(EmployeeData employeeData) {
        return createPhoneUpdatable(employeeData.getHomePhone(), TelecomUseCodes.WORK_PLACE);
    }

    @Override
    public PersonTelecom createEmailTelecom(EmployeeData employeeData, long personId, DatabaseInfo database) {
        PersonTelecom telecomEmail = new PersonTelecom();
        telecomEmail.setUpdatable(createEmailUpdatable(employeeData));
        telecomEmail.setPersonId(personId);
        telecomEmail.setSyncQualifier(SyncQualifiers.TELECOM_EMAIL);
        telecomEmail.setLegacyId(employeeData.getId());
        telecomEmail.setLegacyTable(PersonType.EMPLOYEE.getTableName());
        telecomEmail.setDatabaseId(database.getId());
        return telecomEmail;
    }

    @Override
    public PersonTelecom createHomePhoneTelecom(EmployeeData employeeData, long personId, DatabaseInfo database) {
        PersonTelecom telecomHome = new PersonTelecom();
        telecomHome.setUpdatable(createHomePhoneUpdatable(employeeData));
        telecomHome.setPersonId(personId);
        telecomHome.setSyncQualifier(SyncQualifiers.TELECOM_PHONE1);
        telecomHome.setLegacyId(employeeData.getId());
        telecomHome.setLegacyTable(PersonType.EMPLOYEE.getTableName());
        telecomHome.setDatabaseId(database.getId());
        return telecomHome;
    }

    @Override
    public List<PersonTelecom> createAllTelecoms(ResidentData resident, long personId, long databaseId) {
        String legacyTable = PersonType.RESIDENT.getTableName();
        String legacyResidentId = resident.getId().toString();

        PersonTelecom telecomHome = new PersonTelecom();
        telecomHome.setUpdatable(createHomePhoneUpdatable(resident));
        telecomHome.setPersonId(personId);
        telecomHome.setSyncQualifier(SyncQualifiers.TELECOM_PHONE1);
        telecomHome.setLegacyId(legacyResidentId);
        telecomHome.setLegacyTable(legacyTable);
        telecomHome.setDatabaseId(databaseId);

        PersonTelecom telecomEmail = new PersonTelecom();
        telecomEmail.setUpdatable(createEmailUpdatable(resident));
        telecomEmail.setPersonId(personId);
        telecomEmail.setSyncQualifier(SyncQualifiers.TELECOM_EMAIL);
        telecomEmail.setLegacyId(legacyResidentId);
        telecomEmail.setLegacyTable(legacyTable);
        telecomEmail.setDatabaseId(databaseId);

        PersonTelecom telecomOther = new PersonTelecom();
        telecomOther.setUpdatable(createOtherPhoneUpdatable(resident));
        telecomOther.setPersonId(personId);
        telecomOther.setSyncQualifier(SyncQualifiers.TELECOM_PHONE2);
        telecomOther.setLegacyId(legacyResidentId);
        telecomOther.setLegacyTable(legacyTable);
        telecomOther.setDatabaseId(databaseId);

        List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();
        telecoms.add(telecomHome);
        telecoms.add(telecomEmail);
        telecoms.add(telecomOther);
        return telecoms;
    }

    private PersonTelecom.Updatable createPhoneUpdatable(String phoneNumber, TelecomUseCodes useCode) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(Utils.ensureLeadingPlusInPhoneNumberExists(phoneNumber));
        updatable.setValueNormalized(phonesNormalizer.normalizePhone(phoneNumber));
        updatable.setUseCode(useCode.getValue());
        return updatable;
    }
}
