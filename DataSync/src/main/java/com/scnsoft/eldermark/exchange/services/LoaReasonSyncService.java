package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.LoaReasonDao;
import com.scnsoft.eldermark.exchange.model.source.LoaReasonData;
import com.scnsoft.eldermark.exchange.model.target.LoaReason;
import com.scnsoft.eldermark.exchange.model.target.LoaReason.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.LoaReasonIdResolver;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.EntityMetadata;
import com.scnsoft.eldermark.framework.IdMapping;
import com.scnsoft.eldermark.framework.SyncService;
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
public class LoaReasonSyncService extends StandardSyncService<LoaReasonData, Long, Void> {
	
	@Autowired
    @Qualifier("loaReasonSourceDao")
    private StandardSourceDao<LoaReasonData, Long> sourceDao;

    @Autowired
    private LoaReasonDao loaReasonDao;
    
    @Value("${loareason.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		return Collections.emptyList();
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = loaReasonDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(LoaReasonIdResolver.class, new LoaReasonIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = loaReasonDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	@Override
	protected StandardSourceDao<LoaReasonData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(LoaReasonData.TABLE_NAME, LoaReasonData.ID_COLUMN, LoaReasonData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<LoaReasonData> sourceEntities,
			Map<LoaReasonData, Void> foreignKeysMap) {
		List<LoaReason> loaReasons = new ArrayList<LoaReason>();
        for (LoaReasonData sourceEntity : sourceEntities) {
        	LoaReason loaReason = new LoaReason();
        	loaReason.setLegacyId(sourceEntity.getId());
        	loaReason.setDatabaseId(syncContext.getDatabaseId());
        	loaReason.setUpdatable(createUpdatable(sourceEntity));

        	loaReasons.add(loaReason);
        }
        loaReasonDao.insert(loaReasons);
	}

	
	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<LoaReasonData> sourceEntities,
			Map<LoaReasonData, Void> foreignKeysMap, IdMapping<Long> idMapping) {
		for (LoaReasonData sourceEntity : sourceEntities) {
			LoaReason.Updatable updatable = createUpdatable(sourceEntity);
            long newId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            loaReasonDao.update(updatable, newId);
        }
		
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		loaReasonDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, LoaReasonData entity) {
		return null;
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return loaReasonDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(LoaReasonData sourceEntity) {
		LoaReason.Updatable updatable = new LoaReason.Updatable();
		updatable.setDescription(sourceEntity.getDescription());
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setReasonTypeCode(sourceEntity.getReasonTypeCode());
		return updatable;
	}


}
