package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedScheduleCodeDao;
import com.scnsoft.eldermark.exchange.fk.MedScheduleCodesForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.MedScheduleCodeData;
import com.scnsoft.eldermark.exchange.model.target.MedScheduleCode;
import com.scnsoft.eldermark.exchange.model.target.MedScheduleCode.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MedScheduleCodeSyncService extends StandardSyncService<MedScheduleCodeData, Long, MedScheduleCodesForeignKeys> {
	
	@Autowired
	@Qualifier("medScheduleCodeSourceDao")
	private StandardSourceDao<MedScheduleCodeData, Long> sourceDao;

	@Autowired
	private MedScheduleCodeDao medScheduleCodesDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<MedScheduleCodeData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedScheduleCodeData.TABLE_NAME, MedScheduleCodeData.ID_COLUMN, MedScheduleCodeData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedScheduleCodeData> sourceEntities,
			Map<MedScheduleCodeData, MedScheduleCodesForeignKeys> foreignKeysMap) {
		List<MedScheduleCode> medScheduleCodes = new ArrayList<MedScheduleCode>();
        for (MedScheduleCodeData sourceEntity : sourceEntities) {
            MedScheduleCodesForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            MedScheduleCode medScheduleCode = new MedScheduleCode();
            medScheduleCode.setLegacyId(sourceEntity.getId());
            medScheduleCode.setDatabaseId(syncContext.getDatabaseId());
            medScheduleCode.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            medScheduleCodes.add(medScheduleCode);
        }
        medScheduleCodesDao.insert(medScheduleCodes);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedScheduleCodeData> sourceEntities,
			Map<MedScheduleCodeData, MedScheduleCodesForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedScheduleCodeData sourceEntity : sourceEntities) {
            long legacyId = sourceEntity.getId();
            long id = idMapping.getNewIdOrThrowException(legacyId);
            MedScheduleCodesForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            MedScheduleCode.Updatable update = createUpdatable(sourceEntity, foreignKeys);
            medScheduleCodesDao.update(update, id);
        }
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medScheduleCodesDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedScheduleCodesForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			MedScheduleCodeData entity) {
		DatabaseInfo database = syncContext.getDatabase();
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedScheduleCodeData.TABLE_NAME, entity.getId());
        MedScheduleCodesForeignKeys foreignKeys = new MedScheduleCodesForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        return new FKResolveResult<MedScheduleCodesForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medScheduleCodesDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedScheduleCodeData sourceEntity, MedScheduleCodesForeignKeys foreignKeys) {
		MedScheduleCode.Updatable updatable = new MedScheduleCode.Updatable();
		updatable.setDescription(sourceEntity.getDescription());
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setPassingTimes(sourceEntity.getPassingTimes());
		updatable.setPrn(sourceEntity.getPrn());
		updatable.setSmSigCode(sourceEntity.getSmSigCode());
		updatable.setSmSigDescription(sourceEntity.getSmSigDescription());
		updatable.setUnitStationIds(sourceEntity.getUnitStationIds());
		return updatable;
	}

}
