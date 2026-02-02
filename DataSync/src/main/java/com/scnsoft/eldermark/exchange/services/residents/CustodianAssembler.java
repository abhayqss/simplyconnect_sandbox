package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Custodian;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;

import java.util.Map;

public interface CustodianAssembler {
    Custodian.Updatable createCustodianUpdatable(Long facilityNewId);

    Custodian createCustodian(ResidentData sourceResident, long databaseId, ResidentForeignKeys foreignKeys);

    Custodian createMappedCustodian(ResidentData sourceResident, ResidentForeignKeys foreignKeys, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping);
}
