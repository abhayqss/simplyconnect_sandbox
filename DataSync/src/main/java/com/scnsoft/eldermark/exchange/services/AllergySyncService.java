package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.AllergyForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.AllergyData;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentUpdateQueueService;
import com.scnsoft.eldermark.exchange.validators.CcdCodesValidator;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class AllergySyncService extends StandardSyncService<AllergyData, Long, AllergyForeignKeys> {
    @Autowired
    @Qualifier("allergySourceDao")
    private StandardSourceDao<AllergyData, Long> sourceDao;

    @Autowired
    private AllergyDao allergyDao;

    @Autowired
    private AllergyObservationDao allergyObservationDao;
    
    @Autowired
    private ReactionObservationDao reactionObservationDao;
    
    @Autowired
    private SeverityObservationDao severityObservationDao;
    
    @Autowired
    private AllergyObservationReactionObservationDao allergyObservationReactionObservationDao;

    @Autowired
    private CcdCodesValidator ccdCodesValidator;

    @Autowired
    private ResidentUpdateQueueService residentUpdateQueueService;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(ResidentSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<AllergyData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(AllergyData.TABLE_NAME, AllergyData.ID_COLUMN, AllergyData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return allergyDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<AllergyForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                     AllergyData sourceAllergy) {
        final ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        final CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        DatabaseInfo database = syncContext.getDatabase();

        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        AllergyForeignKeys foreignKeys = new AllergyForeignKeys();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(AllergyData.TABLE_NAME, sourceAllergy.getId());

        String facility = sourceAllergy.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                long companyNewId = companyIdResolver.getId(facility, database);
                foreignKeys.setFacilityOrganizationId(companyNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        Long resNumber = sourceAllergy.getResNumber();
        if (resNumber != null) {
            try {
                long residentNewId = residentIdResolver.getId(resNumber, database);
                foreignKeys.setResidentId(residentNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, resNumber));
            }
        }

        Long allergyCcdId = ExchangeUtils.replaceZeroByNull(sourceAllergy.getAllergyCcdId());
        if (allergyCcdId != null) {
            if (ccdCodesValidator.validate(allergyCcdId)) {
                foreignKeys.setAllergyCodeId(allergyCcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(AllergyData.ALLERGY_CCDID, allergyCcdId));
            }
        }
        
        Long allergyTypeCodeCcdId = ExchangeUtils.replaceZeroByNull(sourceAllergy.getAllergyTypeCcdId());
        if (allergyTypeCodeCcdId != null) {
            if (ccdCodesValidator.validate(allergyTypeCodeCcdId)) {
                foreignKeys.setAllergyTypeCodeId(allergyTypeCodeCcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(AllergyData.ALLERGY_TYPE_CCDID, allergyTypeCodeCcdId));
            }
        }
        
        Long observationStatusCodeCcdId = ExchangeUtils.replaceZeroByNull(sourceAllergy.getStatusCcdId());
        if (observationStatusCodeCcdId != null) {
            if (ccdCodesValidator.validate(observationStatusCodeCcdId)) {
                foreignKeys.setObservationStatusCodeId(observationStatusCodeCcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(AllergyData.OBSERVATION_STATUS_CCDID, observationStatusCodeCcdId));
            }
        }
        
        Long severityCodeId = ExchangeUtils.replaceZeroByNull(sourceAllergy.getSeverityCcdId());
        if (severityCodeId != null) {
            if (ccdCodesValidator.validate(severityCodeId)) {
                foreignKeys.setSeverityCodeId(severityCodeId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(AllergyData.SEVERITY_CCDID, severityCodeId));
            }
        }
        
        return new FKResolveResult<AllergyForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<AllergyData> sourceAllergies,
                                       Map<AllergyData, AllergyForeignKeys> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        
        List<Allergy> allergies = new ArrayList<Allergy>();
        Set<Long> mappedConsanaResidentIds = new HashSet<>();
        for (AllergyData sourceAllergy : sourceAllergies) {
            AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);
            Allergy.Updatable updatable = createAllergyUpdatable(foreignKeys);

            Allergy allergy = new Allergy();
            allergy.setLegacyId(sourceAllergy.getId());
            allergy.setDatabaseId(database.getId());
            allergy.setUpdatable(updatable);
            allergies.add(allergy);
            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Allergy.Updatable mappedUpdatable = createMappedAllergyUpdatable(foreignKeys, databaseIdWithMappedResidentId.getId());
                Allergy mappedAllergy = new Allergy();
                mappedAllergy.setLegacyId(sourceAllergy.getId());
                mappedAllergy.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedAllergy.setUpdatable(mappedUpdatable);
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                allergies.add(mappedAllergy);
            }
        }

        long lastAllergyId = allergyDao.getLastId();
        allergyDao.insert(allergies);
        IdMapping<Long> insertedAllergiesIdMapping = allergyDao.getIdMapping(database, lastAllergyId);
        
        List<SeverityObservation> severityObservations = new ArrayList<SeverityObservation>();
        for (AllergyData sourceAllergy : sourceAllergies) {
        	AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);
			long allergyLegacyId = sourceAllergy.getId();

			SeverityObservation.Updatable updatable = createSeverityObservationUpdatable(sourceAllergy, foreignKeys);
			SeverityObservation severityObservation = new SeverityObservation();
			severityObservation.setDatabaseId(database.getId());
			severityObservation.setLegacyId(allergyLegacyId);
			severityObservation.setLegacyTable(SeverityObservationType.ALLERGY.getLegacyTableName());
			severityObservation.setUpdatable(updatable);

			severityObservations.add(severityObservation);
		}
		long lastSeverityObservationId = severityObservationDao.getLastId();
		severityObservationDao.insert(severityObservations);
		IdMapping<Long> insertedSeverityObservationIdMapping = severityObservationDao.getIdMapping(database,SeverityObservationType.ALLERGY, lastSeverityObservationId);
		

        List<AllergyObservation> observations = new ArrayList<AllergyObservation>();
        for (AllergyData sourceAllergy : sourceAllergies) {
            long allergyLegacyId = sourceAllergy.getId();
            long allergyNewId = insertedAllergiesIdMapping.getNewIdOrThrowException(allergyLegacyId);
            AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);
            long severityObservationNewId = insertedSeverityObservationIdMapping.getNewIdOrThrowException(allergyLegacyId);

            AllergyObservation observation = new AllergyObservation();
            observation.setDatabaseId(database.getId());
            observation.setLegacyId(sourceAllergy.getId());
            observation.setUpdatable(createObservationUpdatable(sourceAllergy, foreignKeys));
            observation.setAllergyId(allergyNewId);
            observation.setSeverityObservationId(severityObservationNewId);
            observations.add(observation);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                long mappedNewId = allergyDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceAllergy.getId(), databaseIdWithMappedResidentId.getId());
                AllergyObservation mappedObservation = new AllergyObservation();
                mappedObservation.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedObservation.setLegacyId(sourceAllergy.getId());
                mappedObservation.setUpdatable(createObservationUpdatable(sourceAllergy, foreignKeys));
                mappedObservation.setAllergyId(mappedNewId);
                mappedObservation.setSeverityObservationId(severityObservationNewId);
                observations.add(mappedObservation);
            }
        }
        long lastAllergyObservationId = allergyObservationDao.getLastId();
        allergyObservationDao.insert(observations);
		IdMapping<Long> insertedAllergyObservationIdMapping = allergyObservationDao.getIdMapping(database, lastAllergyObservationId);
        
        
        List<ReactionObservation> reactionObservations = new ArrayList<ReactionObservation>();
        for (AllergyData sourceAllergy : sourceAllergies) {
            long allergyLegacyId = sourceAllergy.getId();
            AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);

            ReactionObservation reactionObservation = new ReactionObservation();
            reactionObservation.setDatabaseId(database.getId());
            reactionObservation.setLegacyId(String.valueOf(allergyLegacyId));
            reactionObservation.setLegacyTable(ReactionObservationType.ALLERGY.getLegacyTableName());
            reactionObservation.setUpdatable(createReactionObservationUpdatable(sourceAllergy, foreignKeys));
            reactionObservations.add(reactionObservation);
        }
        long lastReactionObservationId = reactionObservationDao.getLastId();
        reactionObservationDao.insert(reactionObservations);
		IdMapping<String> insertedReactionObservationIdMapping = reactionObservationDao.getIdMapping(database,ReactionObservationType.ALLERGY, lastReactionObservationId);
        
        
        List<AllergyObservationReactionObservation> allergyObservationReactionObservations = new ArrayList<AllergyObservationReactionObservation>();
        for (AllergyData sourceAllergy : sourceAllergies) {
            AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);
        	long allergyLegacyId = sourceAllergy.getId();
            long reactionObservationNewId = insertedReactionObservationIdMapping.getNewIdOrThrowException(String.valueOf(allergyLegacyId));
            long allergyObservationNewId = insertedAllergyObservationIdMapping.getNewIdOrThrowException(allergyLegacyId);

            AllergyObservationReactionObservation allergyObservationReactionObservation = new AllergyObservationReactionObservation();
            allergyObservationReactionObservation.setDatabaseId(database.getId());
            allergyObservationReactionObservation.setReactionObservationId(reactionObservationNewId);
            allergyObservationReactionObservation.setAllergyObservationId(allergyObservationNewId);

            allergyObservationReactionObservations.add(allergyObservationReactionObservation);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                long mappedAllergyNewId = allergyDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceAllergy.getId(), databaseIdWithMappedResidentId.getId());
                Long mappedAllergyObservationNewId = allergyObservationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(),allergyLegacyId, mappedAllergyNewId);
                AllergyObservationReactionObservation mappedAllergyObservationReactionObservation = new AllergyObservationReactionObservation();
                mappedAllergyObservationReactionObservation.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedAllergyObservationReactionObservation.setReactionObservationId(reactionObservationNewId);
                mappedAllergyObservationReactionObservation.setAllergyObservationId(mappedAllergyObservationNewId);
                allergyObservationReactionObservations.add(mappedAllergyObservationReactionObservation);
            }
        }
        allergyObservationReactionObservationDao.insert(allergyObservationReactionObservations);

        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, sourceAllergies, "ALLERGY");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "ALLERGY");
        }
    }

	@Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<AllergyData> sourceAllergies,
                                    Map<AllergyData, AllergyForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		
		DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		List<Long> allergyLegacyIds = Utils.getIds(sourceAllergies);
        Set<Long> mappedConsanaResidentIds = new HashSet<>();
		IdMapping<Long> severityObservationsIdMapping = severityObservationDao.getIdMapping(database, SeverityObservationType.ALLERGY, allergyLegacyIds);
		IdMapping<String> reactionObservationsIdMapping = reactionObservationDao.getIdMapping(database, ReactionObservationType.ALLERGY, Utils.getStringIds(sourceAllergies));
		
        for (AllergyData sourceAllergy : sourceAllergies) {
            long allergyLegacyId = sourceAllergy.getId();
            long allergyNewId = idMapping.getNewIdOrThrowException(allergyLegacyId);
            AllergyForeignKeys foreignKeys = foreignKeysMap.get(sourceAllergy);
            
            long severityObservationNewId = severityObservationsIdMapping.getNewIdOrThrowException(allergyLegacyId);
            SeverityObservation.Updatable severityObservationUpdate = createSeverityObservationUpdatable(sourceAllergy, foreignKeys);
            severityObservationDao.update(severityObservationUpdate, severityObservationNewId);

            Allergy.Updatable allergyUpdate = createAllergyUpdatable(foreignKeys);
            allergyDao.update(allergyUpdate, allergyNewId);

            AllergyObservation.Updatable observationUpdate = createObservationUpdatable(sourceAllergy, foreignKeys);
            allergyObservationDao.update(observationUpdate, allergyNewId);

            long reactionObservationNewId = reactionObservationsIdMapping.getNewIdOrThrowException(String.valueOf(allergyLegacyId));
            ReactionObservation.Updatable reactionObservationUpdate = createReactionObservationUpdatable(sourceAllergy, foreignKeys);
            reactionObservationDao.update(reactionObservationUpdate, reactionObservationNewId);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                long mappedNewId = allergyDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceAllergy.getId(), databaseIdWithMappedResidentId.getId());
                Allergy.Updatable mappedAllergyUpdate = createMappedAllergyUpdatable(foreignKeys, databaseIdWithMappedResidentId.getId());
                allergyDao.update(mappedAllergyUpdate, mappedNewId);
                AllergyObservation.Updatable mappedObservationUpdate = createObservationUpdatable(sourceAllergy, foreignKeys);
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                allergyObservationDao.update(mappedObservationUpdate, mappedNewId);
            }
        }

        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, sourceAllergies, "ALLERGY");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "ALLERGY");
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        long legacyId = Long.valueOf(legacyIdString);
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        Long residentId = allergyDao.getResidentId(database, legacyId);

        int mappedDeletedAllergyObservationsCount = 0;
        int mappedDeletedAllergiesCount = 0;
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            Long newMappedAllergyId = allergyDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
            mappedDeletedAllergyObservationsCount = allergyObservationDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, newMappedAllergyId);
            mappedDeletedAllergiesCount = allergyDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
        }
        int deletedAllergyObservationsCount = allergyObservationDao.delete(syncContext.getDatabase(), legacyId);
        int deletedAllergiesCount = allergyDao.delete(syncContext.getDatabase(), legacyId);
        int deletedSeverityObservationsCount = severityObservationDao.delete(syncContext.getDatabase(), SeverityObservationType.ALLERGY.getLegacyTableName(), legacyId);
        int deletedReactionObservationsCount = reactionObservationDao.delete(syncContext.getDatabase(), ReactionObservationType.ALLERGY.getLegacyTableName(), String.valueOf(legacyId));
        if(!StringUtils.isEmpty(database.getConsanaXOwningId()) && residentId != null &&
                deletedAllergyObservationsCount + deletedAllergiesCount + deletedSeverityObservationsCount + deletedReactionObservationsCount  >= 1) {
            residentUpdateQueueService.insert(residentId, "ALLERGY");
        }
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            if(!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId()) && databaseIdWithMappedResidentId.getId() != null &&
                    mappedDeletedAllergyObservationsCount + mappedDeletedAllergiesCount + deletedSeverityObservationsCount + deletedReactionObservationsCount  >= 1) {
                residentUpdateQueueService.insert(databaseIdWithMappedResidentId.getId(), "ALLERGY");
            }
        }
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private Allergy.Updatable createAllergyUpdatable(AllergyForeignKeys foreignKeys) {
        Allergy.Updatable updatable = new Allergy.Updatable();
        updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
        updatable.setResidentId(foreignKeys.getResidentId());
        return updatable;
    }

    private Allergy.Updatable createMappedAllergyUpdatable(AllergyForeignKeys foreignKeys, long residentId) {
        Allergy.Updatable updatable = new Allergy.Updatable();
        updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
        updatable.setResidentId(residentId);
        return updatable;
    }

    private AllergyObservation.Updatable createObservationUpdatable(AllergyData sourceAllergy,
                                                                    AllergyForeignKeys foreignKeys) {
        String allergyName = sourceAllergy.getAllergy();

        AllergyObservation.Updatable updatable = new AllergyObservation.Updatable();
        updatable.setProductCodeId(foreignKeys.getAllergyCodeId());
        updatable.setProductText(allergyName);
        updatable.setAllergyTypeCodeId(foreignKeys.getAllergyTypeCodeId());
        updatable.setAllergyTypeText(sourceAllergy.getAllergyType());
        updatable.setEffectiveTimeHigh(sourceAllergy.getOnsetDate());
        updatable.setObservationStatusCodeId(foreignKeys.getObservationStatusCodeId());
        return updatable;
    }
    
    private SeverityObservation.Updatable createSeverityObservationUpdatable(
			AllergyData sourceAllergy, AllergyForeignKeys foreignKeys) {
    	SeverityObservation.Updatable updatable = new SeverityObservation.Updatable();
    	updatable.setSeverityCodeId(foreignKeys.getSeverityCodeId());
    	updatable.setSeverityText(sourceAllergy.getSeverity());
		return updatable;
	}
    
	private ReactionObservation.Updatable createReactionObservationUpdatable(
			AllergyData sourceAllergy, AllergyForeignKeys foreignKeys) {
		ReactionObservation.Updatable updatable = new ReactionObservation.Updatable();
		updatable.setReactionText(sourceAllergy.getReaction());
		return updatable;
	}

}
