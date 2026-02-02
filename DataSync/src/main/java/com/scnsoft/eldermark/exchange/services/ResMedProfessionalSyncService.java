package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ResMedProfessionalDao;
import com.scnsoft.eldermark.exchange.fk.ResMedProfessionalForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.ResMedProfessional;
import com.scnsoft.eldermark.exchange.model.target.ResMedProfessional.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedicalProfessionalIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedicalProfessionalRoleIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
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
public class ResMedProfessionalSyncService extends StandardSyncService<ResMedProfessionalsData, Long, ResMedProfessionalForeignKeys> {

	@Autowired
	@Qualifier("resMedProfessionalSourceDao")
	private StandardSourceDao<ResMedProfessionalsData, Long> sourceDao;

	@Autowired
	private ResMedProfessionalDao resMedProfessionalDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(MedicalProfessionalRoleSyncService.class);
		dependencies.add(CompanySyncService.class);
		dependencies.add(ResidentSyncService.class);
		dependencies.add(MedicalProfessionalSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResMedProfessionalsData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResMedProfessionalsData.TABLE_NAME, ResMedProfessionalsData.ID_COLUMN, ResMedProfessionalsData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResMedProfessionalsData> sourceEntities,
			Map<ResMedProfessionalsData, ResMedProfessionalForeignKeys> foreignKeysMap) {
		List<ResMedProfessional> resMedProfessionals = new ArrayList<ResMedProfessional>();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResMedProfessionalsData sourceEntity : sourceEntities) {
			ResMedProfessionalForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedProfessional resMedProfessional = new ResMedProfessional();
			resMedProfessional.setLegacyId(sourceEntity.getId());
			resMedProfessional.setDatabaseId(syncContext.getDatabaseId());
			resMedProfessional.setUpdatable(createUpdatable(sourceEntity, foreignKeys));
			resMedProfessionals.add(resMedProfessional);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				ResMedProfessional mappedResMedProfessional = new ResMedProfessional();
				mappedResMedProfessional.setLegacyId(sourceEntity.getId());
				mappedResMedProfessional.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedResMedProfessional.setUpdatable(createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId()));
				resMedProfessionals.add(mappedResMedProfessional);
			}
		}
		resMedProfessionalDao.insert(resMedProfessionals);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResMedProfessionalsData> sourceEntities,
			Map<ResMedProfessionalsData, ResMedProfessionalForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResMedProfessionalsData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResMedProfessionalForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedProfessional.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resMedProfessionalDao.update(update, id);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				Long mappedNewId = resMedProfessionalDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
				ResMedProfessional.Updatable mappedUpdate = createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId());
				resMedProfessionalDao.update(mappedUpdate, mappedNewId);
			}
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Long legacyId = Long.valueOf(legacyIdString);
		Long residentId = resMedProfessionalDao.getResidentId(syncContext.getDatabase(), legacyId);
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			resMedProfessionalDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
		}
		resMedProfessionalDao.delete(syncContext.getDatabase(), legacyId);
	}

	@Override
	protected FKResolveResult<ResMedProfessionalForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			ResMedProfessionalsData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        MedicalProfessionalIdResolver medicalProfessionalIdResolver = syncContext.getSharedObject(MedicalProfessionalIdResolver.class);
        MedicalProfessionalRoleIdResolver medicalProfessionalRoleIdResolver = syncContext.getSharedObject(MedicalProfessionalRoleIdResolver.class);

        ResMedProfessionalForeignKeys foreignKeys = new ResMedProfessionalForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResMedProfessionalsData.TABLE_NAME, entity.getId());
        
        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }
        
        Long residentLegacyId = entity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }
        
        Long medicalProfessionalRoleLegacyId = entity.getRoleCode();
        if (!Utils.isNullOrZero(medicalProfessionalRoleLegacyId)) {
            try {
                foreignKeys.setMedicalProfessionalRoleId(medicalProfessionalRoleIdResolver.getId(medicalProfessionalRoleLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(MedicalProfessionalRoleData.TABLE_NAME, medicalProfessionalRoleLegacyId));
            }
        }
        
        Long medicalProfessionalLegacyId = entity.getMedProfessionalCode();
        if (!Utils.isNullOrZero(medicalProfessionalLegacyId)) {
            try {
                foreignKeys.setMedProfessionalId(medicalProfessionalIdResolver.getId(medicalProfessionalLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(MedicalProfessionalData.TABLE_NAME, medicalProfessionalLegacyId));
            }
        }

        return new FKResolveResult<ResMedProfessionalForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resMedProfessionalDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(ResMedProfessionalsData sourceEntity, ResMedProfessionalForeignKeys foreignKeys) {
		return createUpdatable(sourceEntity, foreignKeys, null);
	}

	private Updatable createMappedUpdatable(ResMedProfessionalsData sourceEntity, ResMedProfessionalForeignKeys foreignKeys, Long mappedResidentId) {
		return createUpdatable(sourceEntity, foreignKeys, mappedResidentId);
	}
	
	private Updatable createUpdatable(ResMedProfessionalsData sourceEntity, ResMedProfessionalForeignKeys foreignKeys, Long residentId) {
		ResMedProfessional.Updatable updatable = new ResMedProfessional.Updatable();

		updatable.setRank(sourceEntity.getRank());

		updatable.setResidentId(residentId != null ?  residentId : foreignKeys.getResidentId());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setMedProfessionalId(foreignKeys.getMedProfessionalId());
		updatable.setMedicalProfessionalRoleId(foreignKeys.getMedicalProfessionalRoleId());
		return updatable;
	}
	
}
