package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Custodian;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustodianAssemblerImpl implements CustodianAssembler {
    @Override
    public Custodian.Updatable createCustodianUpdatable(Long facilityNewId) {
        Custodian.Updatable updatable = new Custodian.Updatable();
        updatable.setOrganizationId(facilityNewId);

        return updatable;
    }

    @Override
    public Custodian createCustodian(ResidentData sourceResident, long databaseId, ResidentForeignKeys foreignKeys) {
        Custodian custodian = new Custodian();
        custodian.setDatabaseId(databaseId);
        custodian.setLegacyId(sourceResident.getId());
        custodian.setUpdatable(createCustodianUpdatable(foreignKeys.getFacilityOrganizationId()));
        return custodian;
    }

    @Override
    public Custodian createMappedCustodian(ResidentData sourceResident, ResidentForeignKeys foreignKeys, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
        Custodian custodian = new Custodian();
        custodian.setDatabaseId(mappedDatabaseIdWithId.getDatabaseId());
        custodian.setLegacyId(sourceResident.getId());
        custodian.setUpdatable(createCustodianUpdatable(mappedDatabaseIdWithId.getId()));
        return custodian;
    }
}
