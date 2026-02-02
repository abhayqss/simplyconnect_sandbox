package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.MedicationTreatmentSetupDao;
import com.scnsoft.eldermark.exchange.model.source.MedicationTreatmentSetupData;
import com.scnsoft.eldermark.exchange.model.target.MedicationTreatmentSetup;
import com.scnsoft.eldermark.exchange.model.target.MedicationTreatmentSetup.Updatable;
import com.scnsoft.eldermark.framework.EntityMetadata;
import com.scnsoft.eldermark.framework.IdMapping;
import com.scnsoft.eldermark.framework.SyncService;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MedicationTreatmentSetupSyncService extends StandardSyncService<MedicationTreatmentSetupData, Long, Void> {

	@Autowired
	@Qualifier("medicationTreatmentSetupSourceDao")
	private StandardSourceDao<MedicationTreatmentSetupData, Long> sourceDao;

	@Autowired
	private MedicationTreatmentSetupDao medicationTreatmentSetupDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		return Collections.emptyList();
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<MedicationTreatmentSetupData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedicationTreatmentSetupData.TABLE_NAME, MedicationTreatmentSetupData.ID_COLUMN, MedicationTreatmentSetupData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedicationTreatmentSetupData> sourceEntities,
			Map<MedicationTreatmentSetupData, Void> foreignKeysMap) {
		List<MedicationTreatmentSetup> medicationTreatmentSetups = new ArrayList<MedicationTreatmentSetup>();
        for (MedicationTreatmentSetupData sourceEntity : sourceEntities) {
        	MedicationTreatmentSetup medicationTreatmentSetup = new MedicationTreatmentSetup();
        	medicationTreatmentSetup.setLegacyId(sourceEntity.getId());
        	medicationTreatmentSetup.setDatabaseId(syncContext.getDatabaseId());
        	medicationTreatmentSetup.setUpdatable(createUpdatable(sourceEntity));

        	medicationTreatmentSetups.add(medicationTreatmentSetup);
        }
        medicationTreatmentSetupDao.insert(medicationTreatmentSetups);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedicationTreatmentSetupData> sourceEntities,
			Map<MedicationTreatmentSetupData, Void> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedicationTreatmentSetupData sourceEntity : sourceEntities) {
            MedicationTreatmentSetup.Updatable updatable = createUpdatable(sourceEntity);
            long newId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            medicationTreatmentSetupDao.update(updatable, newId);
        }
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medicationTreatmentSetupDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, MedicationTreatmentSetupData entity) {
		return null;
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medicationTreatmentSetupDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedicationTreatmentSetupData sourceEntity) {
		MedicationTreatmentSetup.Updatable updatable = new MedicationTreatmentSetup.Updatable();
        updatable.setName(sourceEntity.getName());
        updatable.setSideEffects(sourceEntity.getSideEffects());
        return updatable;
	}
	
}
