package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.PersonName;
import com.scnsoft.eldermark.exchange.model.target.PersonType;
import com.scnsoft.eldermark.exchange.normalizers.PersonNamesNormalizer;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonNameAssemblerImpl implements PersonNameAssembler {
    @Autowired
    private PersonNamesNormalizer normalizer;

    @Override
    public PersonName.Updatable createNameUpdatable(ResidentData resident) {
        PersonName.Updatable updatable = new PersonName.Updatable();

        String familyName = resident.getLastName();
        String givenName = resident.getFirstName();
        String middleName = resident.getMiddleName();

        updatable.setFamily(familyName);
        updatable.setFamilyNormalized(normalizer.normalizeName(familyName));

        updatable.setGiven(givenName);
        updatable.setGivenNormalized(normalizer.normalizeName(givenName));

        updatable.setMiddle(middleName);
        updatable.setMiddleNormalized(normalizer.normalizeName(middleName));

        updatable.setPrefix(resident.getSalutation());
        updatable.setSuffix(resident.getSuffixName());

        updatable.setCallMe(resident.getPreferredName());

        updatable.setNameUse("L");

        return updatable;
    }

    @Override
    public PersonName.Updatable createNameUpdatable(EmployeeData employeeData) {
        PersonName.Updatable updatable = new PersonName.Updatable();

        updatable.setFamily(employeeData.getLastName());
        updatable.setGiven(employeeData.getFirstName());
        updatable.setNameUse("L");

        return updatable;
    }

    @Override
    public PersonName createName(EmployeeData employeeData, long personId, DatabaseInfo database) {
        PersonName name = new PersonName();
        name.setUpdatable(createNameUpdatable(employeeData));
        name.setPersonId(personId);
        name.setDatabaseId(database.getId());
        name.setLegacyTable(PersonType.EMPLOYEE.getTableName());
        name.setLegacyId(employeeData.getId());
        return name;
    }

    @Override
    public PersonName createName(ResidentData resident, long personId, DatabaseInfo database) {
        PersonName name = new PersonName();
        name.setUpdatable(createNameUpdatable(resident));
        name.setPersonId(personId);
        name.setDatabaseId(database.getId());
        name.setLegacyTable(PersonType.RESIDENT.getTableName());
        name.setLegacyId(resident.getId().toString());
        return name;
    }

    @Override
    public PersonName createMappedName(ResidentData resident, long personId, ResidentForeignKeys foreignKeys, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
        PersonName name = new PersonName();
        name.setUpdatable(createNameUpdatable(resident));
        name.setPersonId(personId);
        name.setDatabaseId(mappedDatabaseIdWithId.getDatabaseId());
        name.setLegacyTable(PersonType.RESIDENT.getTableName());
        name.setLegacyId(resident.getId().toString());
        return name;
    }
}
