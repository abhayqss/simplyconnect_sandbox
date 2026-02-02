package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.MedicationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.MedicalProfessionalData;
import com.scnsoft.eldermark.exchange.model.source.PharmacyData;
import com.scnsoft.eldermark.exchange.model.source.ResMedicationData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.*;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentUpdateQueueService;
import com.scnsoft.eldermark.exchange.services.rxnorm.RxNormMappingService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MedicationSyncService extends StandardSyncService<ResMedicationData, Long, MedicationForeignKeys> {
	public static String RX_NORM_CODE_SYSTEM = "2.16.840.1.113883.6.88";

	@Autowired
	@Qualifier("resMedicationSourceDao")
	private StandardSourceDao<ResMedicationData, Long> sourceDao;

	@Autowired
	private MedicationDao medicationDao;

	@Autowired
	private MedicationInformationDao medicationInformationDao;

	@Autowired
	private MedicationSupplyOrderDao medicationSupplyOrderDao;
	
	@Autowired
	private MedicationReportDao medicationReportDao;

	@Autowired
	private MedicationDispenseDao medicationDispenseDao;

	@Autowired
	private MedicationToMedicationDispenseDao medicationToMedicationDispenseDao;

	@Autowired
	private CcdCodesValidator ccdCodesValidator;

	@Autowired
	private CcdCodeDao ccdCodeDao;

	@Autowired
	private RxNormMappingService rxnormMappingService;

	@Autowired
	private ResidentUpdateQueueService residentUpdateQueueService;

	@Value("${medication.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(PharmacySyncService.class);
		dependencies.add(MedicalProfessionalSyncService.class);
		dependencies.add(ResidentSyncService.class);
		return dependencies;
	}

	@Override
	protected StandardSourceDao<ResMedicationData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResMedicationData.TABLE_NAME, ResMedicationData.RES_MED_ID, ResMedicationData.class);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medicationDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	@Override
	protected FKResolveResult<MedicationForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			ResMedicationData sourceMedication) {
		PharmacyIdResolver pharmacyIdResolver = syncContext.getSharedObject(PharmacyIdResolver.class);
        AuthorIdResolver authorIdResolver = syncContext
				.getSharedObject(AuthorIdResolver.class);
		ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
		MedicalProfessionalIdResolver medicalProfessionalIdResolver = syncContext.getSharedObject(MedicalProfessionalIdResolver.class);

		DatabaseInfo database = syncContext.getDatabase();

		MedicationForeignKeys foreignKeys = new MedicationForeignKeys();
		FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResMedicationData.TABLE_NAME, sourceMedication.getId());
		List<FKResolveError> errors = new ArrayList<FKResolveError>();

		Long pharmacyLegacyId = sourceMedication.getPharmacyId();
		// For medications that are treatments, foreign key to pharmacy is '0'
		// (not 'NULL'),
		// but in this case '0' has the same meaning as 'NULL'
		if (pharmacyLegacyId != null && pharmacyLegacyId != 0) {
			try {
				long pharmacyId = pharmacyIdResolver.getId(pharmacyLegacyId, database);
				foreignKeys.setManufacturerId(pharmacyId);
				foreignKeys.setProviderId(pharmacyId);
				foreignKeys.setPharmacyId(pharmacyId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(PharmacyData.TABLE_NAME, pharmacyLegacyId));
			}
		}

		Long dispensingPharmacyLegacyId = sourceMedication.getDispensingPharmacyId();
		if (dispensingPharmacyLegacyId != null && dispensingPharmacyLegacyId != 0) {
			try {
				long dispensingPharmacyId = pharmacyIdResolver.getId(dispensingPharmacyLegacyId, database);
				foreignKeys.setDispensingPharmacyId(dispensingPharmacyId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(PharmacyData.TABLE_NAME, dispensingPharmacyLegacyId));
			}
		}

		Long medProfessionalLegacyId = sourceMedication.getMedProfessionalId();
		// Assume that '0' FK is the same as 'NULL'
		if (medProfessionalLegacyId != null && medProfessionalLegacyId != 0) {
			try {
				long medProfessionalId = authorIdResolver.getId(medProfessionalLegacyId, database);
				foreignKeys.setAuthorId(medProfessionalId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(MedicalProfessionalData.TABLE_NAME, medProfessionalLegacyId));
			}
		}
		if (medProfessionalLegacyId != null && medProfessionalLegacyId != 0) {
			try {
				long medProfessionalId = medicalProfessionalIdResolver.getId(medProfessionalLegacyId, database);
				foreignKeys.setMedicalProfessionalId(medProfessionalId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(MedicalProfessionalData.TABLE_NAME, medProfessionalLegacyId));
			}
		}

		Long residentLegacyId = sourceMedication.getResNumber();
		if (residentLegacyId != null) {
			try {
				long residentId = residentIdResolver.getId(residentLegacyId, database);
				foreignKeys.setResidentId(residentId);
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
			}
		}

		Long routeCcdId = ExchangeUtils.replaceZeroByNull(sourceMedication.getRouteCcdId());
		if (routeCcdId != null) {
			if (ccdCodesValidator.validate(routeCcdId)) {
				foreignKeys.setDeliveryMethodCodeId(routeCcdId);
				foreignKeys.setRouteCodeId(routeCcdId);
			} else {
				errors.add(errorFactory.newInvalidCcdIdError(ResMedicationData.ROUTE_CCDID, routeCcdId));
			}
		}

		String ndcCode = sourceMedication.getNdc();
		if (!Utils.isEmpty(ndcCode)) {
			String rxNormCode = rxnormMappingService.getRxNormCode(ndcCode);
			if (rxNormCode != null) {
				CcdCode code = ccdCodeDao.getCode(rxNormCode, RX_NORM_CODE_SYSTEM);
				if (code != null) {
					foreignKeys.setProductNameCodeId(code.getId());
				}
			}
		}
		return new FKResolveResult<MedicationForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResMedicationData> sourceMedications,
			Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Set<Long> mappedConsanaResidentIds = new HashSet<>();

		IdMapping<Long> medInfoIdMapping = insertMedicalInformation(sourceMedications, database, foreignKeysMap);
		IdMapping<Long> supplyOrdersIdMapping = insertSupplyOrders(sourceMedications, database, foreignKeysMap);
		IdMapping<Long> medicationsIdMapping = insertMedications(sourceMedications, database, foreignKeysMap, medInfoIdMapping,
				supplyOrdersIdMapping, mappedResidentIds);

		IdMapping<Long> medicationDispensesIdMapping = insertMedicationDispenses(sourceMedications, database, foreignKeysMap);
		insertMedicationDispenseRelations(sourceMedications, medicationsIdMapping, medicationDispensesIdMapping, database, foreignKeysMap, mappedResidentIds);
		insertMedicationReport(sourceMedications, medicationsIdMapping, database, foreignKeysMap, mappedResidentIds);
		if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
			residentUpdateQueueService.insert(foreignKeysMap, sourceMedications, "MEDICATION");
		}
		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);
			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
					mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
				}
			}
		}
		if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
			residentUpdateQueueService.insert(mappedConsanaResidentIds, "MEDICATION");
		}
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResMedicationData> sourceMedications,
			Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap, IdMapping<Long> medicationsIdMapping) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Set<Long> mappedConsanaResidentIds = new HashSet<>();
		List<Long> medicationsLegacyIds = Utils.getIds(sourceMedications);
		List<Long> medicationsNewIds = new ArrayList<Long>(medicationsIdMapping.getNewIds());

		IdMapping<Long> medInfoIdMapping = medicationInformationDao.getIdMapping(database, MedicationInformationType.MEDICATION,
				medicationsLegacyIds);

		IdMapping<Long> supplyOrdersIdMapping = medicationSupplyOrderDao.getIdMapping(database,
				MedicationSupplyOrderType.MEDICATION, medicationsLegacyIds);
		
		IdMapping<Long> medicationReportIdMapping = medicationReportDao.getIdMapping(database,
				MedicationReportType.MEDICATION, medicationsLegacyIds);

		Map<Long, Long> medicationIdToDispenseIdMap = medicationToMedicationDispenseDao
				.getMedicationIdToDispenseIdMap(medicationsNewIds);

		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);

			long medicationLegacyId = sourceMedication.getId();
			long medicationNewId = medicationsIdMapping.getNewIdOrThrowException(medicationLegacyId);

			Medication.Updatable medicationUpdatable = createMedicationUpdatable(sourceMedication, foreignKeys);
			medicationDao.update(medicationUpdatable, medicationNewId);

			long medInfoId = medInfoIdMapping.getNewIdOrThrowException(medicationLegacyId);
			MedicationInformation.Updatable medInfoUpdatable = createMedicalInformationUpdatable(sourceMedication, foreignKeys);
			medicationInformationDao.update(medInfoUpdatable, medInfoId);

			long supplyOrderId = supplyOrdersIdMapping.getNewIdOrThrowException(medicationLegacyId);
			MedicationSupplyOrder.Updatable supplyOrderUpdatable = createSupplyOrderUpdatable(sourceMedication, foreignKeys);
			medicationSupplyOrderDao.update(supplyOrderUpdatable, supplyOrderId);
			
			Long medicationReportId = medicationReportIdMapping.getNewId(medicationLegacyId);
            MedicationReport.Updatable medicationReportUpdatable = createMedicationReportUpdatable(sourceMedication);
            medicationReportDao.update(medicationReportUpdatable, medicationReportId);

			long dispenseId = medicationIdToDispenseIdMap.get(medicationNewId);
			MedicationDispense.Updatable dispenseUpdatable = createMedicationDispenseUpdatable(sourceMedication, foreignKeys);
			medicationDispenseDao.update(dispenseUpdatable, dispenseId);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				Long newMedicationMappedId = medicationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), medicationLegacyId, databaseIdWithMappedResidentId.getId());
				Medication.Updatable mappedMedicationUpdatable = createMedicationUpdatable(sourceMedication, foreignKeys, databaseIdWithMappedResidentId.getId());
				medicationDao.update(mappedMedicationUpdatable, newMedicationMappedId);

				Long newMedicationReportMappedId = medicationReportDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), medicationLegacyId, newMedicationMappedId);
				MedicationReport.Updatable mappedMedicationReportUpdatable = createMedicationReportUpdatable(sourceMedication);
				medicationReportDao.update(mappedMedicationReportUpdatable, newMedicationReportMappedId);

				if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
					mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
				}
			}
		}
		if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
			residentUpdateQueueService.insert(foreignKeysMap, sourceMedications, "MEDICATION");
		}
		if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
			residentUpdateQueueService.insert(mappedConsanaResidentIds, "MEDICATION");
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		long legacyId = Long.valueOf(legacyIdString);
		String tableName = MedicationDispenseType.MEDICATION.getLegacyTableName();

		Long residentId = medicationDao.getResidentId(database, legacyId);;

		int mappedDeletedMedicationsCount = 0;
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			Long newMappedId = medicationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
			medicationToMedicationDispenseDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), tableName, legacyId, newMappedId);
			medicationReportDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), MedicationReportType.MEDICATION.getLegacyTableName(), legacyId, newMappedId);
			mappedDeletedMedicationsCount = medicationDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
		}
		medicationToMedicationDispenseDao.delete(database, tableName, legacyId);
		int deletedMedicationDispenseCount = medicationDispenseDao.delete(database, tableName, legacyId);
        medicationReportDao.delete(database, MedicationReportType.MEDICATION.getLegacyTableName(), legacyId);
		int deletedMedicationsCount = medicationDao.delete(database, legacyId);
		int deletedMedicationInformationCount = medicationInformationDao.delete(database, MedicationInformationType.MEDICATION.getLegacyTableName(), legacyId);
		int deletedMedicationSupplyOrderCount = medicationSupplyOrderDao.delete(database, MedicationSupplyOrderType.MEDICATION.getLegacyTableName(), legacyId);
		if(!StringUtils.isEmpty(database.getConsanaXOwningId()) && residentId != null &&
				deletedMedicationDispenseCount + deletedMedicationsCount + deletedMedicationInformationCount + deletedMedicationSupplyOrderCount  >= 1) {
			residentUpdateQueueService.insert(residentId, "MEDICATION");
		}
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			if(!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId()) && databaseIdWithMappedResidentId.getId() != null &&
					deletedMedicationDispenseCount + mappedDeletedMedicationsCount + deletedMedicationInformationCount + deletedMedicationSupplyOrderCount  >= 1) {
				residentUpdateQueueService.insert(databaseIdWithMappedResidentId.getId(), "MEDICATION");
			}
		}
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = medicationDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(MedicationIdResolver.class, new MedicationIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = medicationDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	private IdMapping<Long> insertMedicalInformation(List<ResMedicationData> sourceMedications, DatabaseInfo database,
			Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap) {
		List<MedicationInformation> medInformations = new ArrayList<MedicationInformation>();

		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);
			long sourceMedicationId = sourceMedication.getId();

			MedicationInformation.Updatable updatable = createMedicalInformationUpdatable(sourceMedication, foreignKeys);

			MedicationInformation medInformation = new MedicationInformation();
			medInformation.setDatabaseId(database.getId());
			medInformation.setLegacyTable(MedicationInformationType.MEDICATION.getLegacyTableName());
			medInformation.setLegacyId(sourceMedicationId);
			medInformation.setUpdatable(updatable);

			medInformations.add(medInformation);
		}

		long lastId = medicationInformationDao.getLastId();
		medicationInformationDao.insert(medInformations);
		return medicationInformationDao.getIdMapping(database, MedicationInformationType.MEDICATION, lastId);
	}
	
	private IdMapping<Long> insertMedicationReport(List<ResMedicationData> sourceMedications, IdMapping<Long> medicationsIdMapping, DatabaseInfo database, Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> mappedResidentIds) {
		List<MedicationReport> medicationReports = new ArrayList<MedicationReport>();

		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);
			long legacyId = sourceMedication.getId();
			long medicationNewId = medicationsIdMapping.getNewIdOrThrowException(legacyId);

			MedicationReport.Updatable updatable = createMedicationReportUpdatable(sourceMedication);

			MedicationReport medicationReport = new MedicationReport();
			medicationReport.setDatabaseId(database.getId());
			medicationReport.setLegacyTable(MedicationReportType.MEDICATION.getLegacyTableName());
			medicationReport.setLegacyId(legacyId);
			medicationReport.setMedicationId(medicationNewId);
			medicationReport.setUpdatable(updatable);

			medicationReports.add(medicationReport);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				Long newMappedId = medicationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
				MedicationReport.Updatable mappedUpdatable = createMedicationReportUpdatable(sourceMedication);
				MedicationReport mappedMedicationReport = new MedicationReport();
				mappedMedicationReport.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedMedicationReport.setLegacyTable(MedicationReportType.MEDICATION.getLegacyTableName());
				mappedMedicationReport.setLegacyId(legacyId);
				mappedMedicationReport.setMedicationId(newMappedId);
				mappedMedicationReport.setUpdatable(mappedUpdatable);
				medicationReports.add(mappedMedicationReport);
			}
		}

		long lastId = medicationReportDao.getLastId();
		medicationReportDao.insert(medicationReports);
		return medicationReportDao.getIdMapping(database, MedicationReportType.MEDICATION, lastId);
	}

	private IdMapping<Long> insertSupplyOrders(List<ResMedicationData> sourceMedications, DatabaseInfo database,
			Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap) {
		List<MedicationSupplyOrder> supplyOrders = new ArrayList<MedicationSupplyOrder>();
		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);
			long sourceMedicationId = sourceMedication.getId();

			MedicationSupplyOrder.Updatable updatable = createSupplyOrderUpdatable(sourceMedication, foreignKeys);

			MedicationSupplyOrder supplyOrder = new MedicationSupplyOrder();
			supplyOrder.setDatabaseId(database.getId());
			supplyOrder.setLegacyTable(MedicationSupplyOrderType.MEDICATION.getLegacyTableName());
			supplyOrder.setLegacyId(sourceMedicationId);
			supplyOrder.setUpdatable(updatable);

			supplyOrders.add(supplyOrder);
		}

		long lastId = medicationSupplyOrderDao.getLastId();
		medicationSupplyOrderDao.insert(supplyOrders);
		return medicationSupplyOrderDao.getIdMapping(database, MedicationSupplyOrderType.MEDICATION, lastId);
	}

	private IdMapping<Long> insertMedications(List<ResMedicationData> sourceMedications, DatabaseInfo database,
											  Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap, IdMapping<Long> medInfoIdMapping,
											  IdMapping<Long> supplyOrdersIdMapping, Map<Long, DatabaseIdWithId> mappedResidentIds) {
		List<Medication> medications = new ArrayList<Medication>();
		for (ResMedicationData sourceMedication : sourceMedications) {
			long sourceMedicationId = sourceMedication.getId();
			long medInfoId = medInfoIdMapping.getNewIdOrThrowException(sourceMedicationId);
			long supplyOrderId = supplyOrdersIdMapping.getNewIdOrThrowException(sourceMedicationId);
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);

			Medication.Updatable updatable = createMedicationUpdatable(sourceMedication, foreignKeys);

			Medication medication = new Medication();
			medication.setLegacyId(sourceMedicationId);
			medication.setDatabaseId(database.getId());
			medication.setUpdatable(updatable);
			medication.setMedicationInformationId(medInfoId);
			medication.setMedicationSupplyOrder(supplyOrderId);

			medications.add(medication);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				Medication.Updatable mappedUpdatable = createMappedMedicationUpdatable(sourceMedication, foreignKeys, databaseIdWithMappedResidentId.getId());

				Medication mappedMedication = new Medication();
				mappedMedication.setLegacyId(sourceMedicationId);
				mappedMedication.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedMedication.setUpdatable(mappedUpdatable);
				mappedMedication.setMedicationInformationId(medInfoId);
				mappedMedication.setMedicationSupplyOrder(supplyOrderId);

				medications.add(mappedMedication);
			}
		}

		long lastId = medicationDao.getLastId();
		medicationDao.insert(medications);
		return medicationDao.getIdMapping(database, lastId);
	}

	private IdMapping<Long> insertMedicationDispenses(List<ResMedicationData> sourceMedications, DatabaseInfo database,
			Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap) {
		List<MedicationDispense> medicationDispenses = new ArrayList<MedicationDispense>();
		for (ResMedicationData sourceMedication : sourceMedications) {
			long sourceMedicationId = sourceMedication.getId();
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);

			MedicationDispense.Updatable updatable = createMedicationDispenseUpdatable(sourceMedication, foreignKeys);

			MedicationDispense medicationDispense = new MedicationDispense();
			medicationDispense.setDatabaseId(database.getId());
			medicationDispense.setLegacyTable(MedicationDispenseType.MEDICATION.getLegacyTableName());
			medicationDispense.setLegacyId(sourceMedicationId);
			medicationDispense.setUpdatable(updatable);

			medicationDispenses.add(medicationDispense);
		}

		long lastId = medicationDispenseDao.getLastId();
		medicationDispenseDao.insert(medicationDispenses);
		return medicationDispenseDao.getIdMapping(database, MedicationDispenseType.MEDICATION, lastId);
	}

	private void insertMedicationDispenseRelations(List<ResMedicationData> sourceMedications,
												   IdMapping<Long> medicationsIdMapping, IdMapping<Long> medicationDispensesIdMapping, DatabaseInfo database, Map<ResMedicationData, MedicationForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> mappedResidentIds) {
		List<MedicationToMedicationDispenseRelation> relations = new ArrayList<MedicationToMedicationDispenseRelation>();
		for (ResMedicationData sourceMedication : sourceMedications) {
			MedicationForeignKeys foreignKeys = foreignKeysMap.get(sourceMedication);
			long medicationLegacyId = sourceMedication.getId();
			long medicationNewId = medicationsIdMapping.getNewIdOrThrowException(medicationLegacyId);
			long medDispenseId = medicationDispensesIdMapping.getNewIdOrThrowException(medicationLegacyId);

			MedicationToMedicationDispenseRelation relation = new MedicationToMedicationDispenseRelation();
			relation.setDatabaseId(database.getId());
			relation.setLegacyTable(MedicationDispenseType.MEDICATION.getLegacyTableName());
			relation.setLegacyId(medicationLegacyId);
			relation.setMedicationId(medicationNewId);
			relation.setDispenseId(medDispenseId);

			relations.add(relation);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				Long newMappedMedicationId = medicationDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), medicationLegacyId, databaseIdWithMappedResidentId.getId());
				MedicationToMedicationDispenseRelation mappedRelation = new MedicationToMedicationDispenseRelation();
				mappedRelation.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedRelation.setLegacyTable(MedicationDispenseType.MEDICATION.getLegacyTableName());
				mappedRelation.setLegacyId(medicationLegacyId);
				mappedRelation.setMedicationId(newMappedMedicationId);
				mappedRelation.setDispenseId(medDispenseId);
				relations.add(mappedRelation);
			}
		}
		medicationToMedicationDispenseDao.insert(relations);
	}

	private MedicationInformation.Updatable createMedicalInformationUpdatable(ResMedicationData sourceMedication,
			MedicationForeignKeys foreignKeys) {
		MedicationInformation.Updatable medInformationUpdatable = new MedicationInformation.Updatable();
		medInformationUpdatable.setProductNameCodeId(foreignKeys.getProductNameCodeId());
		medInformationUpdatable.setProductNameText(sourceMedication.getMedication());
		medInformationUpdatable.setManufacturerId(foreignKeys.getManufacturerId());
		return medInformationUpdatable;
	}

	private MedicationSupplyOrder.Updatable createSupplyOrderUpdatable(ResMedicationData sourceMedication,
			MedicationForeignKeys foreignKeys) {
		MedicationSupplyOrder.Updatable updatable = new MedicationSupplyOrder.Updatable();
		updatable.setAuthorId(foreignKeys.getAuthorId());
		updatable.setMedicalProfessionalId(foreignKeys.getMedicalProfessionalId());
		return updatable;
	}

	private Medication.Updatable createMedicationUpdatable(ResMedicationData sourceMedication, MedicationForeignKeys foreignKeys) {
		return createMedicationUpdatable(sourceMedication, foreignKeys, null);
	}

	private Medication.Updatable createMappedMedicationUpdatable(ResMedicationData sourceMedication, MedicationForeignKeys foreignKeys, Long mappedResidentId) {
		return createMedicationUpdatable(sourceMedication, foreignKeys, mappedResidentId);
	}

	private Medication.Updatable createMedicationUpdatable(ResMedicationData sourceMedication, MedicationForeignKeys foreignKeys, Long residentId) {
		Medication.Updatable updatable = new Medication.Updatable();
		updatable.setFreeTextSig(sourceMedication.getInstructions() + "\n" + sourceMedication.getFacilityInstructionNote());
		java.sql.Date prescriptionEndDate = sourceMedication.getPrescriptionEndDate();
		java.sql.Time prescriptionEndTime = sourceMedication.getPrescriptionEndTime();
		if (prescriptionEndDate != null) {
			if (prescriptionEndTime != null) {
				updatable.setMedicationStopped(Utils.mergeDateTime(prescriptionEndDate, prescriptionEndTime));
			} else {
				updatable.setMedicationStopped(prescriptionEndDate);
			}
		}
		java.sql.Date effectiveDate = sourceMedication.getEffectiveDate();
		java.sql.Time effectiveTime = sourceMedication.getEffectiveTime();
		if (effectiveDate != null) {
			if (effectiveTime != null) {
				updatable.setMedicationStarted(Utils.mergeDateTime(effectiveDate, effectiveTime));
			} else {
				updatable.setMedicationStarted(effectiveDate);
			}
		}

		if (sourceMedication.getPrnScheduled() != null && sourceMedication.getPrnScheduled()) {
			updatable.setAdministrationTimingValue(sourceMedication.getSchedule());
		} else if (sourceMedication.getRecurringTaskData() != null && sourceMedication.getRecurringTaskData().equalsIgnoreCase("Daily")){
			updatable.setAdministrationTimingValue(sourceMedication.getSchedule() + "\n" + sourceMedication.getPassingTimes());
		} else {
			updatable.setAdministrationTimingValue(sourceMedication.getRecurringTaskData() + "\n" + sourceMedication.getPassingTimes());
		}

		updatable.setAdministrationTimingUnit(sourceMedication.getPassingTimes());
		updatable.setRouteCodeId(foreignKeys.getRouteCodeId());
		updatable.setDeliveryMethodCodeId(foreignKeys.getDeliveryMethodCodeId());
		updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
		updatable.setEndDateFuture(sourceMedication.getEndDateFuture());
		updatable.setPharmacyOriginDate(sourceMedication.getPharmacyOriginDate());
		updatable.setPharmRxId(sourceMedication.getPharmRxId());
		updatable.setDispensingPharmacyId(foreignKeys.getDispensingPharmacyId());
		updatable.setRefillDate(sourceMedication.getRefillDate());
		updatable.setLastUpdate(sourceMedication.getLastUpdate());
		updatable.setStopDeliveryAfterDate(sourceMedication.getStopDeliveryAfterDate());
		updatable.setPharmacyId(foreignKeys.getPharmacyId());
		updatable.setPrnScheduled(sourceMedication.getPrnScheduled());
		updatable.setSchedule(sourceMedication.getSchedule());
		updatable.setRecurrence(sourceMedication.getRecurringTaskData());
		return updatable;
	}

	private MedicationDispense.Updatable createMedicationDispenseUpdatable(ResMedicationData sourceMedication,
			MedicationForeignKeys foreignKeys) {
		MedicationDispense.Updatable updatable = new MedicationDispense.Updatable();
		updatable.setPrescriptionNumber(sourceMedication.getPrescriptionNumber());
		updatable.setProviderId(foreignKeys.getProviderId());
		return updatable;
	}

	private MedicationReport.Updatable createMedicationReportUpdatable(ResMedicationData sourceMedication) {
		MedicationReport.Updatable updatable = new MedicationReport.Updatable();
		updatable.setDosage(sourceMedication.getDosage());
		updatable.setIndicatedFor(sourceMedication.getIndicatedFor());
		updatable.setSchedule(sourceMedication.getSchedule());
		updatable.setEffectiveDate(sourceMedication.getEffectiveDate());
		updatable.setOrigin(sourceMedication.getOrigin());
		updatable.setAdministerByNurseOnly(sourceMedication.getAdministerByNurseOnly());
		return updatable;
	}

}
