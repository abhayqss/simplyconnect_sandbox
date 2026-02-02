package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.PersonName;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.Map;

public interface PersonNameAssembler {
    PersonName.Updatable createNameUpdatable(ResidentData resident);
    PersonName.Updatable createNameUpdatable(EmployeeData employeeData);

    PersonName createName(ResidentData resident, long personId, DatabaseInfo database);
    PersonName createName(EmployeeData employeeData, long personId, DatabaseInfo database);

    PersonName createMappedName(ResidentData resident, long personId, ResidentForeignKeys foreignKeys, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping);
}
