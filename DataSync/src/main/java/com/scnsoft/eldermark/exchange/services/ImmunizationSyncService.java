package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ImmunizationDao;
import com.scnsoft.eldermark.exchange.dao.target.ImmunizationMedicationInformationDao;
import com.scnsoft.eldermark.exchange.dao.target.OrganizationDao;
import com.scnsoft.eldermark.exchange.dao.target.ReactionObservationDao;
import com.scnsoft.eldermark.exchange.fk.ImmunizationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResImmunizationData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.exchange.validators.CcdCodesValidator;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImmunizationSyncService extends StandardSyncService<ResImmunizationData, String, ImmunizationForeignKeys> {

	@Autowired
	@Qualifier("resImmunizationSourceDao")
	private StandardSourceDao<ResImmunizationData, String> sourceDao;

	@Autowired
	private ImmunizationDao immunizationDao;

	@Autowired
	private ImmunizationMedicationInformationDao immunizationMedicationInformationDao;

	@Autowired
	private ReactionObservationDao reactionObservationDao;

    @Autowired
	private OrganizationDao organizationDao;

	@Autowired
	private CcdCodesValidator ccdCodesValidator;

	@Value("${immunization.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(ResidentSyncService.class);
		return dependencies;
	}

	@Override
	protected StandardSourceDao<ResImmunizationData, String> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResImmunizationData.TABLE_NAME, ResImmunizationData.ID, ResImmunizationData.class);
	}

	@Override
	protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
		return immunizationDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	@Override
	protected FKResolveResult<ImmunizationForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                          ResImmunizationData sourceEntity) {
		ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

		DatabaseInfo database = syncContext.getDatabase();

        ImmunizationForeignKeys foreignKeys = new ImmunizationForeignKeys();
		FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResImmunizationData.TABLE_NAME, sourceEntity.getId());
		List<FKResolveError> errors = new ArrayList<FKResolveError>();


		Long residentLegacyId = sourceEntity.getResNumber();
		if (residentLegacyId != null) {
			try {
				long residentId = residentIdResolver.getId(residentLegacyId, database);
				foreignKeys.setResidentId(residentId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
			}
		}

		Long routeCcdId = ExchangeUtils.replaceZeroByNull(sourceEntity.getRouteId());
		if (routeCcdId != null) {
			if (ccdCodesValidator.validate(routeCcdId)) {
				foreignKeys.setRouteId(routeCcdId);
			} else {
				errors.add(errorFactory.newInvalidCcdIdError(ResImmunizationData.ROUTE_CCDID, routeCcdId));
			}
		}

		Long injectionSiteId = ExchangeUtils.replaceZeroByNull(sourceEntity.getInjectionSiteId());
		if (injectionSiteId != null) {
			if (ccdCodesValidator.validate(injectionSiteId)) {
				foreignKeys.setInjectionSiteId(injectionSiteId);
			} else {
				errors.add(errorFactory.newInvalidCcdIdError(ResImmunizationData.INJECTION_SITE_CCDID, injectionSiteId));
			}
		}

		Long vaccineId = ExchangeUtils.replaceZeroByNull(sourceEntity.getVaccineId());
		if (vaccineId != null) {
			if (ccdCodesValidator.validate(vaccineId)) {
				foreignKeys.setVaccineId(vaccineId);
			} else {
				errors.add(errorFactory.newInvalidCcdIdError(ResImmunizationData.VACCINE_CCDID, vaccineId));
			}

		}
		return new FKResolveResult<ImmunizationForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResImmunizationData> sourceEntities,
			Map<ResImmunizationData, ImmunizationForeignKeys> foreignKeysMap) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        IdMapping<String> organizationsIdMapping = insertOrganization(sourceEntities, database);
        IdMapping<String> medInfoIdMapping = insertImmunizationMedicalInformation(sourceEntities, database, foreignKeysMap, organizationsIdMapping);
		IdMapping<String> reactionsIdMapping = insertReactionObservation(sourceEntities, database, foreignKeysMap);
		insertImmunizations(sourceEntities, database, foreignKeysMap, medInfoIdMapping, reactionsIdMapping, mappedResidentIds);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResImmunizationData> sourceEntities,
			Map<ResImmunizationData, ImmunizationForeignKeys> foreignKeysMap, IdMapping<String> immunizationsIdMapping) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		List<String> immunizationsLegacyIds = Utils.getIds(sourceEntities);

		IdMapping<String> medInfoIdMapping = immunizationMedicationInformationDao.getIdMapping(database, immunizationsLegacyIds);
		IdMapping<String> reactionIdMapping = reactionObservationDao.getIdMapping(database, ReactionObservationType.IMMUNIZATION, immunizationsLegacyIds);
        IdMapping<String> organizationsIdMapping = organizationDao.getImmunizationManufactorerIdMapping(database, immunizationsLegacyIds);

		for (ResImmunizationData sourceEntity : sourceEntities) {
            ImmunizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			String immunizationLegacyId = sourceEntity.getId();
			long immunizationNewId = immunizationsIdMapping.getNewIdOrThrowException(immunizationLegacyId);

			Immunization.Updatable immunizationUpdatable = createImmunizationUpdatable(sourceEntity, foreignKeys);
			immunizationDao.update(immunizationUpdatable, immunizationNewId);

			long medInfoId = medInfoIdMapping.getNewIdOrThrowException(immunizationLegacyId);
            ImmunizationMedicationInformation.Updatable medInfoUpdatable = createMedicalInformationUpdatable(sourceEntity, foreignKeys);
			immunizationMedicationInformationDao.update(medInfoUpdatable, medInfoId);

			long reactionId = reactionIdMapping.getNewIdOrThrowException(immunizationLegacyId);
			ReactionObservation.Updatable reactionObservationUpdatable = createReactionObservationUpdatable(sourceEntity);
			reactionObservationDao.update(reactionObservationUpdatable, reactionId);

			long organizationId = organizationsIdMapping.getNewIdOrThrowException(immunizationLegacyId);
			Organization.Updatable organizationUpdatable = createOrganizationUpdatable(sourceEntity);
			organizationDao.update(organizationUpdatable, organizationId);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				long immunizationMappedNewId = immunizationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), immunizationLegacyId, databaseIdWithMappedResidentId.getId());
				Immunization.Updatable immunizationMappedUpdatable = createMappedImmunizationUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId());
				immunizationDao.update(immunizationMappedUpdatable, immunizationMappedNewId);
			}
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

		 Long residentId = immunizationDao.getResidentId(database, legacyIdString);

		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			immunizationDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyIdString, databaseIdWithMappedResidentId.getId());
		}
        immunizationDao.delete(database, legacyIdString);
        reactionObservationDao.delete(database, ReactionObservationType.IMMUNIZATION.getLegacyTableName(), legacyIdString);
        immunizationMedicationInformationDao.delete(database, legacyIdString);
        organizationDao.delete(database, OrganizationType.IMMUNIZATION_MANUFACTORER, legacyIdString);
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	private IdMapping<String> insertImmunizationMedicalInformation(List<ResImmunizationData> sourceEntities, DatabaseInfo database,
			Map<ResImmunizationData, ImmunizationForeignKeys> foreignKeysMap, IdMapping<String> organizationIdMapping) {
		List<ImmunizationMedicationInformation> medInformations = new ArrayList<ImmunizationMedicationInformation>();

		for (ResImmunizationData sourceEntity : sourceEntities) {
            ImmunizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            String sourceEntityId = sourceEntity.getId();

            ImmunizationMedicationInformation.Updatable updatable = createMedicalInformationUpdatable(sourceEntity, foreignKeys);

            ImmunizationMedicationInformation medInformation = new ImmunizationMedicationInformation();
			medInformation.setDatabaseId(database.getId());
			medInformation.setLegacyId(sourceEntityId);
			medInformation.setOrganizationId(organizationIdMapping.getNewIdOrThrowException(sourceEntityId));
			medInformation.setUpdatable(updatable);

			medInformations.add(medInformation);
		}

		long lastId = immunizationMedicationInformationDao.getLastId();
        immunizationMedicationInformationDao.insert(medInformations);
		return immunizationMedicationInformationDao.getIdMapping(database, lastId);
	}

	private IdMapping<String> insertReactionObservation(List<ResImmunizationData> sourceEntities, DatabaseInfo database,
			Map<ResImmunizationData, ImmunizationForeignKeys> foreignKeysMap) {
		List<ReactionObservation> reactionObservations = new ArrayList<ReactionObservation>();

		for (ResImmunizationData sourceEntity : sourceEntities) {
            ImmunizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            String sourceEntityId = sourceEntity.getId();

            ReactionObservation.Updatable updatable = createReactionObservationUpdatable(sourceEntity);

            ReactionObservation reactionObservation = new ReactionObservation();
			reactionObservation.setDatabaseId(database.getId());
			reactionObservation.setLegacyId(sourceEntityId);
			reactionObservation.setUpdatable(updatable);
            reactionObservation.setLegacyTable(ReactionObservationType.IMMUNIZATION.getLegacyTableName());

            reactionObservations.add(reactionObservation);
		}

		long lastId = reactionObservationDao.getLastId();
        reactionObservationDao.insert(reactionObservations);
		return reactionObservationDao.getIdMapping(database, ReactionObservationType.IMMUNIZATION, lastId);
	}

	private IdMapping<String> insertOrganization(List<ResImmunizationData> sourceEntities, DatabaseInfo database) {
		List<Organization> organizations = new ArrayList<Organization>();

		for (ResImmunizationData sourceEntity : sourceEntities) {
            String sourceEntityId = sourceEntity.getId();

            Organization.Updatable updatable = createOrganizationUpdatable(sourceEntity);

            Organization organization = new Organization();
			organization.setDatabaseId(database.getId());
			organization.setLegacyId(sourceEntityId);
			organization.setUpdatable(updatable);
            organization.setLegacyTable(OrganizationType.IMMUNIZATION_MANUFACTORER.getLegacyTableName());

            organizations.add(organization);
		}

		long lastId = organizationDao.getLastId();
        organizationDao.insert(organizations);
		return organizationDao.getImmunizationManufactorerIdMapping(database, lastId);
	}

	private IdMapping<String> insertImmunizations(List<ResImmunizationData> sourceEntities, DatabaseInfo database,
												  Map<ResImmunizationData, ImmunizationForeignKeys> foreignKeysMap, IdMapping<String> medInfoIdMapping,
												  IdMapping<String> reactionsIdMapping, Map<Long, DatabaseIdWithId> mappedResidentIds) {
		List<Immunization> immunizations = new ArrayList<Immunization>();
		for (ResImmunizationData sourceEntity : sourceEntities) {
            String sourceId = sourceEntity.getId();
			long medInfoId = medInfoIdMapping.getNewIdOrThrowException(sourceId);
			long reactionId = reactionsIdMapping.getNewIdOrThrowException(sourceId);
            ImmunizationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Immunization.Updatable updatable = createImmunizationUpdatable(sourceEntity, foreignKeys);

            Immunization immunization = new Immunization();
			immunization.setLegacyId(sourceId);
			immunization.setDatabaseId(database.getId());
			immunization.setUpdatable(updatable);
			immunization.setImmunizationMedicationInformationId(medInfoId);
			immunization.setReactionObservationId(reactionId);

            immunizations.add(immunization);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());

				Immunization.Updatable mappedUpdatable = createMappedImmunizationUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId());

				Immunization mappedImmunization = new Immunization();
				mappedImmunization.setLegacyId(sourceId);
				mappedImmunization.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedImmunization.setUpdatable(mappedUpdatable);
				mappedImmunization.setImmunizationMedicationInformationId(medInfoId);
				mappedImmunization.setReactionObservationId(reactionId);

				immunizations.add(mappedImmunization);
			}
		}

		long lastId = immunizationDao.getLastId();
        immunizationDao.insert(immunizations);
		return immunizationDao.getIdMapping(database, lastId);
	}

	private ImmunizationMedicationInformation.Updatable createMedicalInformationUpdatable(ResImmunizationData sourceEntity,
                                                                                          ImmunizationForeignKeys foreignKeys) {
        ImmunizationMedicationInformation.Updatable medInformationUpdatable = new ImmunizationMedicationInformation.Updatable();
		medInformationUpdatable.setCodeId(foreignKeys.getVaccineId());
		medInformationUpdatable.setText(sourceEntity.getVaccineName());
		medInformationUpdatable.setLotNumberText(sourceEntity.getVaccineLotNumber());
		return medInformationUpdatable;
	}

	private ReactionObservation.Updatable createReactionObservationUpdatable(ResImmunizationData sourceEntity) {
        ReactionObservation.Updatable updatable = new ReactionObservation.Updatable();
		updatable.setReactionText(sourceEntity.getAdverseReaction());
		return updatable;
	}

	private Organization.Updatable createOrganizationUpdatable(ResImmunizationData sourceEntity) {
        Organization.Updatable updatable = new Organization.Updatable();
		updatable.setName(sourceEntity.getManufacturerName());
		return updatable;
	}

	private Immunization.Updatable createImmunizationUpdatable(ResImmunizationData sourceEntity, ImmunizationForeignKeys foreignKeys, Long residentId) {
        Immunization.Updatable updatable = new Immunization.Updatable();

        updatable.setRefusal(Boolean.FALSE);
        updatable.setMoodCode("EVN");
        updatable.setStatusCode("completed");
        updatable.setImmunizationStopped(sourceEntity.getDateReceived());
        updatable.setResidentId(foreignKeys.getResidentId());
        if (!Utils.isEmpty(sourceEntity.getAdministeredBy())) {
            updatable.setText("Administered By: "  + sourceEntity.getAdministeredBy());
        }
		updatable.setSiteCodeId(foreignKeys.getInjectionSiteId());
		updatable.setRouteCodeId(foreignKeys.getRouteId());
		updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
		return updatable;
	}

	private Immunization.Updatable createImmunizationUpdatable(ResImmunizationData sourceEntity, ImmunizationForeignKeys foreignKeys) {
		return createImmunizationUpdatable(sourceEntity, foreignKeys, null);
	}

	private Immunization.Updatable createMappedImmunizationUpdatable(ResImmunizationData sourceEntity, ImmunizationForeignKeys foreignKeys, Long mappedResidentId) {
		return createImmunizationUpdatable(sourceEntity, foreignKeys, mappedResidentId);
	}
}
