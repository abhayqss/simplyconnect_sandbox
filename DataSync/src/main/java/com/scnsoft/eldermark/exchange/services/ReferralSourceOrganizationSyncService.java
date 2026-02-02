package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.ReferralSourceOrganizationDao;
import com.scnsoft.eldermark.exchange.fk.ReferralSourceOrganizationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.OrgReferralSourceData;
import com.scnsoft.eldermark.exchange.model.source.OrgReferralSourceFacilityData;
import com.scnsoft.eldermark.exchange.model.target.ReferralSourceOrganization;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ReferralSourceIdResolver;
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
public class ReferralSourceOrganizationSyncService
		extends	StandardSyncService<OrgReferralSourceFacilityData, Long, ReferralSourceOrganizationForeignKeys> {

	@Autowired
    @Qualifier("orgReferralSourceFacilitySourceDao")
    private StandardSourceDao<OrgReferralSourceFacilityData, Long> sourceDao;

    @Autowired
    private ReferralSourceOrganizationDao targetDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(ReferralSourceSyncService.class);
        dependencies.add(CompanySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<OrgReferralSourceFacilityData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(OrgReferralSourceFacilityData.TABLE_NAME, OrgReferralSourceFacilityData.RECORD_ID,
                OrgReferralSourceFacilityData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ReferralSourceOrganizationForeignKeys> resolveForeignKeys(
            DatabaseSyncContext syncContext, OrgReferralSourceFacilityData sourceEntity) {

        DatabaseInfo database = syncContext.getDatabase();
        ReferralSourceIdResolver orgReferralSourceIdResolver = syncContext.getSharedObject(ReferralSourceIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        ReferralSourceOrganizationForeignKeys foreignKeys = new ReferralSourceOrganizationForeignKeys();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(OrgReferralSourceFacilityData.TABLE_NAME,
                sourceEntity.getId());
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        Long orgReferralSourceLegacyId = sourceEntity.getOrgRefSourceId();
        if (!Utils.isNullOrZero(orgReferralSourceLegacyId)) {
            try {
                foreignKeys.setReferralSourceId(
                        orgReferralSourceIdResolver.getId(orgReferralSourceLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(OrgReferralSourceData.TABLE_NAME, orgReferralSourceLegacyId));
            }
        }

        String companyLegacyId = sourceEntity.getFacility();
        if (!Utils.isEmpty(companyLegacyId)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(companyLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, companyLegacyId));
            }
        }
        return new FKResolveResult<ReferralSourceOrganizationForeignKeys>(foreignKeys, errors);
    }
    
	@Override
	protected void doEntitiesInsertion(
			DatabaseSyncContext syncContext,
			List<OrgReferralSourceFacilityData> sourceEntities,
			Map<OrgReferralSourceFacilityData, ReferralSourceOrganizationForeignKeys> foreignKeysMap) {
		List<ReferralSourceOrganization> targetEntities = new ArrayList<ReferralSourceOrganization>();
        for (OrgReferralSourceFacilityData sourceEntity : sourceEntities) {
        	ReferralSourceOrganizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

        	ReferralSourceOrganization targetEntity = new ReferralSourceOrganization();
            targetEntity.setLegacyId(sourceEntity.getId());
            targetEntity.setDatabaseId(syncContext.getDatabaseId());
            targetEntity.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            targetEntities.add(targetEntity);
        }
        targetDao.insert(targetEntities);
	}

	@Override
	protected void doEntitiesUpdate(
			DatabaseSyncContext syncContext,
			List<OrgReferralSourceFacilityData> sourceEntities,
			Map<OrgReferralSourceFacilityData, ReferralSourceOrganizationForeignKeys> foreignKeysMap,
			IdMapping<Long> idMapping) {
		for (OrgReferralSourceFacilityData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            ReferralSourceOrganizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            ReferralSourceOrganization.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            targetDao.update(updatable, id);
        }
	}

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        targetDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private ReferralSourceOrganization.Updatable createUpdatable(OrgReferralSourceFacilityData sourceEntity,
    		ReferralSourceOrganizationForeignKeys foreignKeys) {
    	ReferralSourceOrganization.Updatable updatable = new ReferralSourceOrganization.Updatable();
        updatable.setReferralSourceId(foreignKeys.getReferralSourceId());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        return updatable;
    }

}
