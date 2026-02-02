package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.DiagnosisSetupDao;
import com.scnsoft.eldermark.exchange.model.IcdCodeSet;
import com.scnsoft.eldermark.exchange.model.source.DiagnosisSetupData;
import com.scnsoft.eldermark.exchange.model.target.DiagnosisSetup;
import com.scnsoft.eldermark.exchange.resolvers.CcdCodeResolver;
import com.scnsoft.eldermark.exchange.resolvers.DiagnosisSetupIdResolver;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.EntityMetadata;
import com.scnsoft.eldermark.framework.IdMapping;
import com.scnsoft.eldermark.framework.SyncService;
import com.scnsoft.eldermark.exchange.model.target.DiagnosisSetup.Updatable;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosisSetupSyncService extends StandardSyncService<DiagnosisSetupData, Long, Void> {

	@Autowired
    @Qualifier("diagnosisSetupSourceDao")
    private StandardSourceDao<DiagnosisSetupData, Long> sourceDao;

    @Autowired
    private DiagnosisSetupDao diagnosisSetupDao;

    @Value("${loareason.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		return Collections.emptyList();
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = diagnosisSetupDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(DiagnosisSetupIdResolver.class, new DiagnosisSetupIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = diagnosisSetupDao.getId(database, legacyId);
				}
				return newId;
			}

			@Override
			public IcdCodeSet getCodeSetFor(String icdCodeValue, DatabaseInfo databaseInfo) {
				return diagnosisSetupDao.getCodeSetFor(databaseInfo, icdCodeValue);
			}
		});

		context.putSharedObject(CcdCodeResolver.class, new CcdCodeResolver() {
			@Override
			public Long getOrCreateCcdCodeFor(DatabaseInfo database, String codeOid, String displayName, IcdCodeSet codeSet) {
				return diagnosisSetupDao.getOrCreateCcdCodeFor(database, codeOid, displayName, codeSet);
			}
		});
	}

	@Override
	protected StandardSourceDao<DiagnosisSetupData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(DiagnosisSetupData.TABLE_NAME, DiagnosisSetupData.ID_COLUMN, DiagnosisSetupData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<DiagnosisSetupData> sourceEntities,
			Map<DiagnosisSetupData, Void> foreignKeysMap) {
		List<DiagnosisSetup> diagnosisSetupValues = new ArrayList<DiagnosisSetup>();
        for (DiagnosisSetupData sourceEntity : sourceEntities) {
			DiagnosisSetup diagnosisSetup = new DiagnosisSetup();
			diagnosisSetup.setLegacyId(sourceEntity.getId());
			diagnosisSetup.setDatabaseId(syncContext.getDatabaseId());
			diagnosisSetup.setUpdatable(createUpdatable(sourceEntity));
			diagnosisSetupValues.add(diagnosisSetup);
        }
		diagnosisSetupDao.insert(diagnosisSetupValues);
	}


	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<DiagnosisSetupData> sourceEntities,
			Map<DiagnosisSetupData, Void> foreignKeysMap, IdMapping<Long> idMapping) {
		for (DiagnosisSetupData sourceEntity : sourceEntities) {
			Updatable updatable = createUpdatable(sourceEntity);
            long newId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
			diagnosisSetupDao.update(updatable, newId);
        }

	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		diagnosisSetupDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, DiagnosisSetupData entity) {
		return null;
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return diagnosisSetupDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(DiagnosisSetupData sourceEntity) {
		Updatable updatable = new Updatable();
		updatable.setName(sourceEntity.getName());
		updatable.setCode(sourceEntity.getCode());
		updatable.setIcd10cm(sourceEntity.getIcd10CM());
		updatable.setIcd10pcs(sourceEntity.getIcd10PCS());
		updatable.setIcd9cm(sourceEntity.getIcd9CM());
		updatable.setIsManual("Manual".equalsIgnoreCase(sourceEntity.getManualOrLibrary()));
		updatable.setIsStandardCode(sourceEntity.getStandardCode());
		updatable.setInactive(sourceEntity.getInactive());
		return updatable;
	}


}
