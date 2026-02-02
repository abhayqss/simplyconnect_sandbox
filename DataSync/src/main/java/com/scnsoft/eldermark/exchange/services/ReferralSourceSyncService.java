package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.ReferralSourceDao;
import com.scnsoft.eldermark.exchange.model.source.OrgReferralSourceData;
import com.scnsoft.eldermark.exchange.model.target.ReferralSource;
import com.scnsoft.eldermark.exchange.model.target.ReferralSource.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.ReferralSourceIdResolver;
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
public class ReferralSourceSyncService
		extends	StandardSyncService<OrgReferralSourceData, Long, Void> {

	@Autowired
    @Qualifier("orgReferralSourceDao")
    private StandardSourceDao<OrgReferralSourceData, Long> sourceDao;

    @Value("${orgreferralsources.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Autowired
    private ReferralSourceDao targetDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
    	return Collections.emptyList();
    }

    @Override
    protected StandardSourceDao<OrgReferralSourceData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(OrgReferralSourceData.TABLE_NAME, OrgReferralSourceData.ORG_REF_SOURCE_ID,
                OrgReferralSourceData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<Void> resolveForeignKeys(
            DatabaseSyncContext syncContext, OrgReferralSourceData sourceEntity) {
    	 return null;
    }
    
    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<OrgReferralSourceData> sourceEntities,
                                       Map<OrgReferralSourceData, Void> foreignKeysMap) {
    	List<ReferralSource> targetEntities = new ArrayList<ReferralSource>();
        for (OrgReferralSourceData sourceEntity : sourceEntities) {
   
            ReferralSource targetEntity = new ReferralSource();
            targetEntity.setDatabaseId(syncContext.getDatabaseId());
            targetEntity.setLegacyId(sourceEntity.getId());
            targetEntity.setUpdatable(createUpdatable(sourceEntity));

            targetEntities.add(targetEntity);
        }

        targetDao.insert(targetEntities);
    }
    
	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext,
			List<OrgReferralSourceData> sourceEntities,
			Map<OrgReferralSourceData, Void> foreignKeysMap,
			IdMapping<Long> idMapping) {
		for (OrgReferralSourceData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());

            ReferralSource.Updatable updatable = createUpdatable(sourceEntity);
            targetDao.update(updatable, id);
        }
	}
    
    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext,
                                String legacyIdString) {
        targetDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = targetDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(ReferralSourceIdResolver.class, new ReferralSourceIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = targetDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private Updatable createUpdatable(OrgReferralSourceData sourceEntity) {
    	ReferralSource.Updatable updatable = new ReferralSource.Updatable();
    	updatable.setCreateDate(sourceEntity.getCreateDate());
    	updatable.setName(sourceEntity.getName());
    	return updatable;
	}


    
    /*private OrgReferralSource.Updatable createUpdatable(OrgReferralSourceData sourceEntity,
                                                        OrgReferralSourceForeignKeys foreignKeys) {
        OrgReferralSource.Updatable updatable = new OrgReferralSource.Updatable();
        updatable.setAReferralSource(sourceEntity.getAReferralSource());

        String employeeLegacyId = sourceEntity.getEmployeeOwner();
        if ("*ALL*".equalsIgnoreCase(employeeLegacyId)) {
            updatable.setRelatedToAllFacilities(true);
        } else {
            updatable.setRelatedToAllFacilities(false);
        }
        updatable.setEmployeeOwnerId(foreignKeys.getEmployeeOwnerId());

        return updatable;
    }*/
    
}
