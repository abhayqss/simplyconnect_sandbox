package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.MedicalProfessionalRoleDao;
import com.scnsoft.eldermark.exchange.model.source.MedicalProfessionalRoleData;
import com.scnsoft.eldermark.exchange.model.target.MedicalProfessionalRole;
import com.scnsoft.eldermark.exchange.model.target.MedicalProfessionalRole.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.MedicalProfessionalRoleIdResolver;
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
public class MedicalProfessionalRoleSyncService extends StandardSyncService<MedicalProfessionalRoleData, Long, Void> {

	@Autowired
	@Qualifier("medicalProfessionalRoleSourceDao")
	private StandardSourceDao<MedicalProfessionalRoleData, Long> sourceDao;

	@Autowired
	private MedicalProfessionalRoleDao medicalProfessionalRoleDao;

	@Value("${medicalprofessionalrole.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		return Collections.emptyList();
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = medicalProfessionalRoleDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(MedicalProfessionalRoleIdResolver.class, new MedicalProfessionalRoleIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = medicalProfessionalRoleDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	@Override
	protected StandardSourceDao<MedicalProfessionalRoleData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedicalProfessionalRoleData.TABLE_NAME, MedicalProfessionalRoleData.ID_COLUMN, MedicalProfessionalRoleData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedicalProfessionalRoleData> sourceEntities,
			Map<MedicalProfessionalRoleData, Void> foreignKeysMap) {
		List<MedicalProfessionalRole> medicalProfessionalRoles = new ArrayList<MedicalProfessionalRole>();
        for (MedicalProfessionalRoleData sourceEntity : sourceEntities) {
        	MedicalProfessionalRole medicalProfessionalRole = new MedicalProfessionalRole();
        	medicalProfessionalRole.setLegacyId(sourceEntity.getId());
        	medicalProfessionalRole.setDatabaseId(syncContext.getDatabaseId());
        	medicalProfessionalRole.setUpdatable(createUpdatable(sourceEntity));

        	medicalProfessionalRoles.add(medicalProfessionalRole);
        }
        medicalProfessionalRoleDao.insert(medicalProfessionalRoles);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedicalProfessionalRoleData> sourceEntities,
			Map<MedicalProfessionalRoleData, Void> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedicalProfessionalRoleData sourceEntity : sourceEntities) {
			MedicalProfessionalRole.Updatable updatable = createUpdatable(sourceEntity);
            long newId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            medicalProfessionalRoleDao.update(updatable, newId);
        }
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medicalProfessionalRoleDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, MedicalProfessionalRoleData entity) {
		return null;
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medicalProfessionalRoleDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedicalProfessionalRoleData sourceEntity) {
		MedicalProfessionalRole.Updatable updatable = new MedicalProfessionalRole.Updatable();
		updatable.setDescription(sourceEntity.getDescription());
		updatable.setInactive(sourceEntity.getInactive());
		return updatable;
	}

	
}
