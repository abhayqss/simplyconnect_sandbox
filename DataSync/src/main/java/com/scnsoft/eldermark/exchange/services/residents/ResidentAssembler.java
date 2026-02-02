package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Resident;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;

public interface ResidentAssembler {
    Resident.Updatable createResidentUpdatable(ResidentData sourceResident, ResidentForeignKeys foreignKeys);

    Resident.Updatable createMappedResidentUpdatable(ResidentData sourceResident, ResidentForeignKeys foreignKeys, DatabaseIdWithId targetOrganizationIdAndDatabaseId);

    Resident createResident(ResidentData sourceResident, long personId, long custodianId, long databaseId, ResidentForeignKeys foreignKeys, String hieConsentPolicy);

    Resident createMappedResident(ResidentData sourceResident, long personId, long custodianId, ResidentForeignKeys foreignKeys, DatabaseIdWithId targetOrganizationIdAndDatabaseId, String mappedResidentHieConsentPolicy);
}
