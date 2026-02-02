package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedTimeCodeDao;
import com.scnsoft.eldermark.exchange.fk.MedTimeCodeForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.MedTimeCodeData;
import com.scnsoft.eldermark.exchange.model.target.MedTimeCode;
import com.scnsoft.eldermark.exchange.model.target.MedTimeCode.Updatable;
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
public class MedTimeCodeSyncService extends StandardSyncService<MedTimeCodeData, Long, MedTimeCodeForeignKeys> {

	@Autowired
	@Qualifier("medTimeCodeSourceDao")
	private StandardSourceDao<MedTimeCodeData, Long> sourceDao;

	@Autowired
	private MedTimeCodeDao medTimeCodeDao;

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
	protected StandardSourceDao<MedTimeCodeData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedTimeCodeData.TABLE_NAME, MedTimeCodeData.ID_COLUMN, MedTimeCodeData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedTimeCodeData> sourceEntities,
			Map<MedTimeCodeData, MedTimeCodeForeignKeys> foreignKeysMap) {
		List<MedTimeCode> medTimeCodes = new ArrayList<MedTimeCode>();
        for (MedTimeCodeData sourceEntity : sourceEntities) {
            MedTimeCodeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            MedTimeCode medTimeCode = new MedTimeCode();
            medTimeCode.setLegacyId(sourceEntity.getId());
            medTimeCode.setDatabaseId(syncContext.getDatabaseId());
            medTimeCode.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            medTimeCodes.add(medTimeCode);
        }
        medTimeCodeDao.insert(medTimeCodes);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedTimeCodeData> sourceEntities,
			Map<MedTimeCodeData, MedTimeCodeForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedTimeCodeData sourceEntity : sourceEntities) {
            long legacyId = sourceEntity.getId();
            long id = idMapping.getNewIdOrThrowException(legacyId);
            MedTimeCodeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            MedTimeCode.Updatable update = createUpdatable(sourceEntity, foreignKeys);
            medTimeCodeDao.update(update, id);
        }
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medTimeCodeDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedTimeCodeForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, MedTimeCodeData entity) {
		DatabaseInfo database = syncContext.getDatabase();
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedTimeCodeData.TABLE_NAME, entity.getId());
        MedTimeCodeForeignKeys foreignKeys = new MedTimeCodeForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        return new FKResolveResult<MedTimeCodeForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medTimeCodeDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedTimeCodeData sourceEntity, MedTimeCodeForeignKeys foreignKeys) {
		MedTimeCode.Updatable updatable = new MedTimeCode.Updatable();
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setName(sourceEntity.getName());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setPrn(sourceEntity.getPrn());
		updatable.setTimeRangeBegin(sourceEntity.getTimeRangeBegin());
		updatable.setTimeRangeBeginAlpha(sourceEntity.getTimeRangeBeginAlpha());
		updatable.setTimeRangeEnd(sourceEntity.getTimeRangeEnd());
		updatable.setTimeRangeEndAlpha(sourceEntity.getTimeRangeEndAlpha());
		return updatable;
	}

}
