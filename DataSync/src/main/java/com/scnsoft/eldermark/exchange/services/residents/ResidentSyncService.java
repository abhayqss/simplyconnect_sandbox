package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.SyncQualifiers;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.model.vo.OrganizationAdvanceDirective;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.OrganizationAddressResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.CompanySyncService;
import com.scnsoft.eldermark.exchange.services.ProblemSyncService;
import com.scnsoft.eldermark.exchange.services.StandardSyncService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResidentSyncService extends StandardSyncService<ResidentData, Long, ResidentForeignKeys> {
    @Autowired
    @Qualifier("personDao")
    private PersonDao personDao;

    @Autowired
    private PersonNameDao nameDao;

    @Autowired
    private PersonAddressDao addressDao;

    @Autowired
    private PersonTelecomDao telecomDao;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private ResidentDocumentsDao residentDocumentsDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private CustodianDao custodianDao;

    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Autowired
    private ResLanguageDao languageDao;

    @Autowired
    private ResidentOrderDao residentOrderDao;

    @Autowired
    private ResidentNoteDao residentNoteDao;

    @Autowired
    private ResidentHealthPlanDao residentHealthPlanDao;

    @Autowired
    private PayerDao payerDao;

    @Autowired
    private ParticipantDao participantDao;

    @Autowired
    private PolicyActivityDao policyActivityDao;

    @Autowired
    private CoveragePlanDescriptionDao coveragePlanDescriptionDao;

    @Autowired
    private OrganizationHieConsentPolicyDao organizationHieConsentPolicyDao;

    @Autowired
    private PersonNameAssembler nameAssembler;

    @Autowired
    private PersonAddressAssembler addressAssembler;

    @Autowired
    private PersonTelecomAssembler telecomAssembler;

    @Autowired
    private ResidentAssembler residentAssembler;

    @Autowired
    private CustodianAssembler custodianAssembler;

    @Autowired
    private ResidentAuthorAssembler authorAssembler;

    @Autowired
    private ResidentAdvanceDirectiveAssembler advancedDirectiveAssembler;

    @Autowired
    private ResidentLanguageAssembler languageAssembler;

    @Autowired
    private ResidentOrderAssembler ordersAssembler;

    @Autowired
    private ResidentNotesAssembler notesAssembler;

    @Autowired
    private PayerAssembler payerAssembler;

    @Autowired
    @Qualifier("residentSourceDao")
    private StandardSourceDao<ResidentData, Long> residentSourceDao;

    @Autowired
    private CcdCodesValidator ccdCodesValidator;
    
    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private ProblemSyncService problemSyncService;

    @Autowired
    private MPIDao mpiDao;

    @Autowired
    private ResidentUpdateQueueService residentUpdateQueueService;

    @Value("${residents.idmapping.cache.size}")
    private int residentsIdMappingLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencyServices = new ArrayList<Class<? extends SyncService>>();
        dependencyServices.add(CompanySyncService.class);
        return dependencyServices;
    }

    @Override
    protected StandardSourceDao<ResidentData, Long> getSourceDao() {
        return residentSourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ResidentData.TABLE_NAME, ResidentData.RES_NUMBER, ResidentData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return residentDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ResidentForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                      ResidentData resident) {
        final CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        DatabaseInfo database = syncContext.getDatabase();

        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResidentData.TABLE_NAME, resident.getId());
        ResidentForeignKeys foreignKeys = new ResidentForeignKeys();
        
        String facilityLegacyId = resident.getFacility();
        if (!Utils.isEmpty(facilityLegacyId)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facilityLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facilityLegacyId));
            }
        }

        Long genderId = ExchangeUtils.replaceZeroByNull(resident.getGenderId());
        if (genderId != null) {
            if (ccdCodesValidator.validate(genderId)) {
                foreignKeys.setGenderId(genderId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResidentData.GENDER_CCDID, genderId));
            }
        }

        Long maritalStatusId = ExchangeUtils.replaceZeroByNull(resident.getMaritalStatusId());
        if (maritalStatusId != null) {
            if (ccdCodesValidator.validate(maritalStatusId)) {
                foreignKeys.setMaritalStatusId(maritalStatusId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResidentData.MARITAL_STATUS_CCDID, maritalStatusId));
            }
        }

        Long raceId = ExchangeUtils.replaceZeroByNull(resident.getRaceId());
        if (raceId != null) {
            if (ccdCodesValidator.validate(raceId)) {
                foreignKeys.setRaceId(raceId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResidentData.RACE_CCDID, raceId));
            }
        }

        Long religionId = ExchangeUtils.replaceZeroByNull(resident.getReligionId());
        if (religionId != null) {
            if (ccdCodesValidator.validate(religionId)) {
                foreignKeys.setReligionId(religionId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResidentData.RELIGION_CCDID, religionId));
            }
        }

        Long primaryLanguageId = ExchangeUtils.replaceZeroByNull(resident.getPrimaryLanguageId());
        if (primaryLanguageId != null) {
            if (ccdCodesValidator.validate(primaryLanguageId)) {
                foreignKeys.setPrimaryLanguageId(primaryLanguageId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResidentData.PRIMARY_LANGUAGE_CCDID, primaryLanguageId));
            }
        }

        return new FKResolveResult<ResidentForeignKeys>(foreignKeys, errors);
    }
    
    private void updateInactiveResidentDocuments(ResidentData resident, IdAware database) {
    	Pattern pattern = Pattern.compile("Res#(.*?),");
    	if (StringUtils.isEmpty(resident.getInternalLog())) {
    	    return;
        }
		Matcher matcher = pattern.matcher(resident.getInternalLog());
		ArrayList<Long> inactiveResidentsLegacyId = new ArrayList<>();
		
		while (matcher.find()) {
			if(matcher.group(1) != null && !matcher.group(1).isEmpty()) {
				try {
					inactiveResidentsLegacyId.add(Long.valueOf(matcher.group(1).trim()));
				}
				catch(Exception e) {}
			}
		}
		
		System.out.println("Keeper resident number : "+ resident.getId() +
			"Tossed residents number list: "+  inactiveResidentsLegacyId.toString());
		
		if(!inactiveResidentsLegacyId.isEmpty()) {
			for(Long legacyId : inactiveResidentsLegacyId) {
				residentDocumentsDao.updateInactiveResidentDocuments(resident.getId(), legacyId, database.getId());
			}
		}
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResidentData> sourceResidents,
                                       Map<ResidentData, ResidentForeignKeys> foreignKeysMap) {

        OrganizationAddressResolver organizationAddressResolver =
                syncContext.getSharedObject(OrganizationAddressResolver.class);
        Set<Long> mappedConsanaResidentIds = new HashSet<>();

        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping = syncContext.getTargetOrganizationsIdMapping();
        IdMapping<String> personsIdMapping = insertPersons(database, sourceResidents, foreignKeysMap, targetOrganizationsIdMapping);
        IdMapping<Long> custodiansIdMapping = insertCustodians(database, sourceResidents, foreignKeysMap, targetOrganizationsIdMapping);
        IdMapping<Long> residentsIdMapping = insertResidents(database, sourceResidents, personsIdMapping, custodiansIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertMPI(sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertNames(database, sourceResidents, personsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertAddresses(database, sourceResidents, personsIdMapping, foreignKeysMap, organizationAddressResolver, targetOrganizationsIdMapping);
        insertTelecoms(database, sourceResidents, personsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertAuthors(database, sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertAdvanceDirectives(database, sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertLanguages(database, sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertOrders(database, sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        insertNotes(database, sourceResidents, residentsIdMapping, foreignKeysMap, targetOrganizationsIdMapping);
        syncResidentsHealthPlans(sourceResidents, residentsIdMapping, personsIdMapping, database, foreignKeysMap, targetOrganizationsIdMapping);
        
        for(ResidentData sourceResident : sourceResidents) {
	        if(sourceResident.getInternalLog()!=null && !sourceResident.getInternalLog().isEmpty()) {
	    		updateInactiveResidentDocuments(sourceResident, syncContext.getDatabase());
	        }
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeysMap.get(sourceResident).getFacilityOrganizationId());
                if (!StringUtils.isEmpty(mappedDatabaseIdWithId.getConsanaXOwningId())) {
                    ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                    Long facilityNewId = foreignKeys.getFacilityOrganizationId();
                    Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), facilityNewId));
                    mappedConsanaResidentIds.add(mappedNewId);
                }
                updateInactiveResidentDocuments(sourceResident, new IdAwareImpl(mappedDatabaseIdWithId.getDatabaseId()));
            }
        }

        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(residentsIdMapping, sourceResidents, "RESIDENT");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "RESIDENT");
        }
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResidentData> sourceResidents,
                                    Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                    IdMapping<Long> residentsIdMapping) {
        OrganizationAddressResolver organizationAddressResolver = syncContext.getSharedObject(OrganizationAddressResolver.class);

        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping = syncContext.getTargetOrganizationsIdMapping();

        List<String> stringLegacyIds = Utils.getStringIds(sourceResidents);
        IdMapping<String> personsIdMapping = personDao.getIdMapping(database, PersonType.RESIDENT, stringLegacyIds);
        IdMapping<Long> custodianIdMapping = custodianDao.getIdMapping(database, Utils.getIds(sourceResidents));
        IdMapping<String> personNamesIdMapping = nameDao.getIdMapping(syncContext.getDatabase(), PersonType.RESIDENT, stringLegacyIds);
        IdMapping<String> personAddressesIdMapping = addressDao.getIdMapping(syncContext.getDatabase(), PersonType.RESIDENT, stringLegacyIds);
        HashMap<Integer, IdMapping<String>> personTelecomIdMappings = new HashMap<Integer, IdMapping<String>>();
        personTelecomIdMappings.put(SyncQualifiers.TELECOM_EMAIL, telecomDao.getIdMapping(syncContext.getDatabase(), PersonType.RESIDENT, SyncQualifiers.TELECOM_EMAIL, stringLegacyIds));
        personTelecomIdMappings.put(SyncQualifiers.TELECOM_PHONE1, telecomDao.getIdMapping(syncContext.getDatabase(), PersonType.RESIDENT, SyncQualifiers.TELECOM_PHONE1, stringLegacyIds));
        personTelecomIdMappings.put(SyncQualifiers.TELECOM_PHONE2, telecomDao.getIdMapping(syncContext.getDatabase(), PersonType.RESIDENT, SyncQualifiers.TELECOM_PHONE2, stringLegacyIds));

        Map<String, OrganizationAdvanceDirective> organizationsAdvDirectives = organizationDao.getCompaniesAdvanceDirectivesMap(database);
        Set<Long> mappedConsanaResidentIds = new HashSet<>();

        for (ResidentData sourceResident : sourceResidents) {
            Long residentLegacyId = sourceResident.getId();
            long residentNewId = residentsIdMapping.getNewIdOrThrowException(residentLegacyId);
            long personId = personsIdMapping.getNewIdOrThrowException(residentLegacyId.toString());
            Long nameId = personNamesIdMapping.getNewId(residentLegacyId.toString());
            Long addressId = personAddressesIdMapping.getNewId(residentLegacyId.toString());
            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
            Long facilityNewId = foreignKeys.getFacilityOrganizationId();
            Long custodianNewId = custodianIdMapping.getNewId(residentLegacyId);

            updateResidentAndRelated(organizationAddressResolver, database, organizationsAdvDirectives, sourceResident, residentNewId, personId, foreignKeys, facilityNewId, custodianNewId, targetOrganizationsIdMapping, false, nameId, addressId, personTelecomIdMappings);

            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeysMap.get(sourceResident).getFacilityOrganizationId());
                long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), PersonType.RESIDENT, sourceResident.getId().toString());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), facilityNewId));
                Long mappedNewFacilityId = mappedDatabaseIdWithId.getId();
                long mappedCustodianId = custodianDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), residentLegacyId);
                if (!StringUtils.isEmpty(mappedDatabaseIdWithId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(mappedNewId);
                }
                updateResidentAndRelated(organizationAddressResolver, new IdAwareImpl(mappedDatabaseIdWithId.getDatabaseId()), organizationsAdvDirectives, sourceResident, mappedNewId, mappedPersonId, foreignKeys, mappedNewFacilityId, mappedCustodianId, targetOrganizationsIdMapping, true);
            }


        }

        syncResidentsHealthPlans(sourceResidents, residentsIdMapping, personsIdMapping, database, foreignKeysMap, targetOrganizationsIdMapping);

        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(residentsIdMapping, sourceResidents, "RESIDENT");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "RESIDENT");
        }
    }

    private void updateResidentAndRelated(OrganizationAddressResolver organizationAddressResolver, IdAware database, Map<String,
            OrganizationAdvanceDirective> organizationsAdvDirectives, ResidentData sourceResident, long residentNewId, long personId,
                                          ResidentForeignKeys foreignKeys, Long facilityNewId, Long custodianNewId, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping, boolean isMapped) {
        updateResidentAndRelated(organizationAddressResolver, database, organizationsAdvDirectives, sourceResident, residentNewId, personId, foreignKeys, facilityNewId, custodianNewId, targetOrganizationsIdMapping, isMapped, null, null, null);
    }

    private void updateResidentAndRelated(OrganizationAddressResolver organizationAddressResolver, IdAware database, Map<String,
            OrganizationAdvanceDirective> organizationsAdvDirectives, ResidentData sourceResident, long residentNewId, long personId,
                                          ResidentForeignKeys foreignKeys, Long facilityNewId, Long custodianNewId, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping, boolean isMapped, Long nameId, Long addressId, HashMap<Integer, IdMapping<String>> personTelecomIdMappings) {
        PersonName.Updatable personNameUpdate = nameAssembler.createNameUpdatable(sourceResident);
        if (nameId != null) {
            nameDao.updateById(personNameUpdate, nameId);
        } else {
            nameDao.update(personNameUpdate, personId);
        }


        PersonAddress.Updatable personAddressUpdate;
        if (facilityNewId != null) {
            //Org address is taken from source organization (not mapped)
            OrganizationAddress facilityAddress = organizationAddressResolver.getCompanyAddress(foreignKeys.getFacilityOrganizationId());
            personAddressUpdate = addressAssembler.createAddressUpdatable(sourceResident, facilityAddress);
        } else {
            personAddressUpdate = addressAssembler.createEmptyAddressUpdatable();
        }
        if (addressId != null) {
            addressDao.updateById(personAddressUpdate, addressId);
        } else {
            addressDao.update(personAddressUpdate, personId);
        }


        if (custodianNewId == null) { // remove check after full import and change to getNewIdOrThrowException
            Custodian custodian = isMapped ? custodianAssembler.createMappedCustodian(sourceResident, foreignKeys, targetOrganizationsIdMapping)
                    : custodianAssembler.createCustodian(sourceResident, database.getId(), foreignKeys);
            custodianDao.insert(Arrays.asList(custodian));
        } else {
            Custodian.Updatable custodianUpdatable = custodianAssembler.createCustodianUpdatable(facilityNewId);
            custodianDao.update(custodianUpdatable, custodianNewId);
        }

        List<PersonTelecom> telecoms = telecomAssembler.createAllTelecoms(sourceResident, personId, database.getId());
        telecomDao.insertOrUpdate(telecoms, personTelecomIdMappings);

        Resident.Updatable residentUpdate = isMapped ? residentAssembler.createMappedResidentUpdatable(sourceResident, foreignKeys, targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId()))
            : residentAssembler.createResidentUpdatable(sourceResident, foreignKeys);
        residentDao.update(residentUpdate, residentNewId);

        Author.Updatable authorUpdate = authorAssembler.createAuthorUpdatable(facilityNewId);
        authorDao.updateCcdHeaderAuthor(authorUpdate, residentNewId);

        List<AdvanceDirective> advanceDirectives = new ArrayList<AdvanceDirective>();
        OrganizationAdvanceDirective organization = organizationsAdvDirectives.get(sourceResident.getFacility());
        advanceDirectiveDao.deleteForResident(residentNewId);
        advanceDirectives.addAll(advancedDirectiveAssembler.createAdvanceDirectivesForResident(sourceResident, organization, residentNewId, database.getId()));
        advanceDirectiveDao.insert(advanceDirectives);

        Language.Updatable languageUpdate = languageAssembler.createLanguageUpdatable(sourceResident, foreignKeys);
        languageDao.update(languageUpdate, residentNewId);

        residentOrderDao.deleteForResident(residentNewId);
        residentOrderDao.insert(ordersAssembler.getResidentOrders(sourceResident, residentNewId, database.getId()));

        residentNoteDao.deleteForResident(residentNewId);
        residentNoteDao.insert(notesAssembler.getResidentNotes(sourceResident, residentNewId, database.getId()));

        problemSyncService.updateResidentBirthDate(sourceResident, residentNewId);

        if(sourceResident.getInternalLog()!=null && !sourceResident.getInternalLog().isEmpty()) {
            updateInactiveResidentDocuments(sourceResident, database);
        }
    }


    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext,
                                String legacyIdString) {
        //Residents deletion was disabled, because they may have documents attached and it's
        //unlikely that admitted residents can be ever deleted.
        //In case resident was deleted, mark it as inactive in exchange database

        DatabaseInfo database = syncContext.getDatabase();
        Long residentNewID = residentDao.getNewId(database.getId(), legacyIdString);
        residentDao.markAsInactive(database, legacyIdString);
        Long facilityId = residentDao.getOrganizationId(residentNewID);

        Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping = syncContext.getTargetOrganizationsIdMapping();
        if (MappingUtils.hasMappingForResidentOrganization(facilityId, targetOrganizationsIdMapping)) {
            DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(facilityId);
            Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(legacyIdString, facilityId));
            residentDao.markAsInactive(new IdAwareImpl(mappedDatabaseIdWithId.getDatabaseId()), MappingUtils.generateMappedLegacyId(legacyIdString, facilityId));
            if(!StringUtils.isEmpty(mappedDatabaseIdWithId.getConsanaXOwningId()) && mappedNewId != null) {
                residentUpdateQueueService.insert(mappedNewId, "RESIDENT");
            }
        }

        if(!StringUtils.isEmpty(database.getConsanaXOwningId()) && residentNewID != null) {
            residentUpdateQueueService.insert(residentNewID, "RESIDENT");
        }

        /*long legacyResidentId = Long.valueOf(legacyIdString);
        IdMapping<Long> residentNewIds = residentDao.getIdMapping(database, Arrays.asList(legacyResidentId));
        Long residentNewId = residentNewIds.getNewId(legacyResidentId);

        mpiDao.markPersonAsDeleted(residentNewId);*/

        /*
        PersonType personType = PersonType.RESIDENT;

        languageDao.delete(database, legacyResidentId);
        advanceDirectiveDao.delete(database, legacyResidentId);
        authorDao.deleteCcdHeaderAuthor(database, legacyResidentId);

        telecomDao.delete(database, personType, legacyResidentId, SyncQualifiers.TELECOM_PHONE1);
        telecomDao.delete(database, personType, legacyResidentId, SyncQualifiers.TELECOM_PHONE2);
        telecomDao.delete(database, personType, legacyResidentId, SyncQualifiers.TELECOM_EMAIL);

        addressDao.delete(database, personType, legacyResidentId);
        nameDao.delete(database, personType, legacyResidentId);
        custodianDao.delete(database, legacyResidentId);
        residentDao.delete(database, legacyResidentId);
        personDao.delete(database, personType, legacyResidentId);
        residentNoteDao.deleteForResident(residentNewId);
        residentOrderDao.deleteForResident(residentNewId);

        for(Long healthPlanId : residentHealthPlanDao.getHealthPlanIdsForResident(residentNewId)) {
            coveragePlanDescriptionDao.delete(database, healthPlanId);
            policyActivityDao.delete(database, healthPlanId);
            participantDao.delete(database, ParticipantType.POLICY_TARGET, healthPlanId);
            payerDao.delete(database, healthPlanId);
            organizationDao.delete(database, OrganizationType.PAYER, healthPlanId.toString());

            residentHealthPlanDao.delete(healthPlanId);
        }*/
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        DatabaseInfo database = context.getDatabase();
        final IdMapping<Long> idMapping = residentDao.getIdMapping(database, residentsIdMappingLimit);

        context.putSharedObject(ResidentIdResolver.class, new ResidentIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = residentDao.getId(database, legacyId);
                }
                return newId;
            }
        });
        context.setMappedResidentIds(residentDao.getMappedResidentIds(database.getId()));
    }

    private IdMapping<String> insertPersons(DatabaseInfo database, List<ResidentData> sourceResidents,
                                            Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                            Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<Person> persons = new ArrayList<Person>();
        for (ResidentData sourceResident : sourceResidents) {
            Person person = new Person();
            person.setDatabaseId(database.getId());
            person.setLegacyTable(PersonType.RESIDENT.getTableName());
            person.setLegacyId(sourceResident.getId().toString());
            person.setTypeCodeId(null);
            persons.add(person);

            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeysMap.get(sourceResident).getFacilityOrganizationId());
                Person mappedPerson = new Person();
                mappedPerson.setDatabaseId(mappedDatabaseIdWithId.getDatabaseId());
                mappedPerson.setLegacyTable(PersonType.RESIDENT.getTableName());
                mappedPerson.setLegacyId(sourceResident.getId().toString());
                mappedPerson.setTypeCodeId(null);
                persons.add(mappedPerson);
            }
        }
        long personLastId = personDao.getLastId();
        personDao.insert(persons);
        return personDao.getIdMapping(database, PersonType.RESIDENT, personLastId);
    }

    private IdMapping<Long> insertCustodians(DatabaseInfo database, List<ResidentData> sourceResidents,
                                             Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                             Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<Custodian> custodians = new ArrayList<Custodian>();
        for (ResidentData sourceResident : sourceResidents) {
            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
            custodians.add(custodianAssembler.createCustodian(sourceResident, database.getId(), foreignKeys));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                custodians.add(custodianAssembler.createMappedCustodian(sourceResident, foreignKeys, targetOrganizationsIdMapping));
            }
        }
        long lastIdBeforeInsert = custodianDao.getLastId();
        custodianDao.insert(custodians);
        return custodianDao.getIdMapping(database, lastIdBeforeInsert);
    }

    private IdMapping<Long> insertResidents(DatabaseInfo database, List<ResidentData> sourceResidents,
                                            IdMapping<String> personsIdMapping, IdMapping<Long> custodiansIdMapping,
                                            Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                            Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<Resident> residents = new ArrayList<Resident>();
        for (ResidentData sourceResident : sourceResidents) {
            Long residentLegacyId = sourceResident.getId();
            long personId = personsIdMapping.getNewIdOrThrowException(residentLegacyId.toString());
            long custodianId = custodiansIdMapping.getNewIdOrThrowException(residentLegacyId);
            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
            String residentHieConsentPolicy = "OPT_OUT";
            if (foreignKeys.getFacilityOrganizationId() != null) {
                String hieConsentPolicy = organizationHieConsentPolicyDao.getHieConsentPolicy(foreignKeys.getFacilityOrganizationId());
                if (!StringUtils.isEmpty(hieConsentPolicy)) {
                    residentHieConsentPolicy = hieConsentPolicy;
                }
            }
            residents.add(residentAssembler.createResident(sourceResident, personId, custodianId, database.getId(), foreignKeys, residentHieConsentPolicy));

            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithOrganizationId = targetOrganizationsIdMapping.get(foreignKeysMap.get(sourceResident).getFacilityOrganizationId());
                long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithOrganizationId.getDatabaseId(), PersonType.RESIDENT, residentLegacyId.toString());
                long mappedCustodianId = custodianDao.getNewId(mappedDatabaseIdWithOrganizationId.getDatabaseId(), residentLegacyId);
                String mappedResidentHieConsentPolicy = organizationHieConsentPolicyDao.getHieConsentPolicy(mappedDatabaseIdWithOrganizationId.getId());
                if (StringUtils.isEmpty(mappedResidentHieConsentPolicy)) {
                    mappedResidentHieConsentPolicy = "OPT_OUT";
                }
                residents.add(residentAssembler.createMappedResident(sourceResident, mappedPersonId, mappedCustodianId, foreignKeys, mappedDatabaseIdWithOrganizationId, mappedResidentHieConsentPolicy));
            }
        }

        long residentLastId = residentDao.getLastId();
        residentDao.insert(residents);
        return residentDao.getIdMapping(database, residentLastId);
    }

    private void insertAdvanceDirectives(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping,
                                         Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<AdvanceDirective> advanceDirectives = new ArrayList<AdvanceDirective>();
        Map<String, OrganizationAdvanceDirective> organizationsMap = organizationDao.getCompaniesAdvanceDirectivesMap(database);
        for (ResidentData sourceResident : sourceResidents) {
            long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            OrganizationAdvanceDirective organization = organizationsMap.get(sourceResident.getFacility());
            advanceDirectives.addAll(advancedDirectiveAssembler.createAdvanceDirectivesForResident(sourceResident, organization, residentNewId, database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                advanceDirectives.addAll(advancedDirectiveAssembler.createAdvanceDirectivesForResident(sourceResident, organization, mappedNewId, mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        advanceDirectiveDao.insert(advanceDirectives);
    }

    private void insertOrders(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping, Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<ResidentOrder> residentOrders = new ArrayList<ResidentOrder>();
        for (ResidentData sourceResident : sourceResidents) {
            long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            residentOrders.addAll(ordersAssembler.getResidentOrders(sourceResident, residentNewId, database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                residentOrders.addAll(ordersAssembler.getResidentOrders(sourceResident, mappedNewId, mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        residentOrderDao.insert(residentOrders);
    }

    private void insertNotes(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping,
                             Map<ResidentData,ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<ResidentNotes> residentNotes = new ArrayList<ResidentNotes>();
        for (ResidentData sourceResident : sourceResidents) {
            long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            residentNotes.addAll(notesAssembler.getResidentNotes(sourceResident, residentNewId, database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                residentNotes.addAll(notesAssembler.getResidentNotes(sourceResident, mappedNewId, mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        residentNoteDao.insert(residentNotes);
    }

    /*private void syncResidentsHealthPlans(List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping, IdMapping<String> personsIdMapping, DatabaseInfo database,
                                          Map<ResidentData,ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        for (ResidentData sourceResident : sourceResidents) {
            Long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            Long residentPersonNewId = personsIdMapping.getNewIdOrThrowException(sourceResident.getId().toString());

            Map<Long, ResidentHealthPlan> srcValues= residentHealthPlanDao.getHealthPlansForResident(residentNewId);
            List<ResidentHealthPlan> destValues = payerAssembler.getResidentHealthPlans(sourceResident, residentNewId, database.getId());

            Set<Long> toDelete = new HashSet<Long>();
            Set<ResidentHealthPlan> toAdd = new HashSet<ResidentHealthPlan>();

            for(Map.Entry<Long, ResidentHealthPlan> existingPlan : srcValues.entrySet()) {
                if(!destValues.contains(existingPlan.getValue())) {
                    toDelete.add(existingPlan.getKey());
                }
            }
            for(ResidentHealthPlan newPlan : destValues) {
                if(!srcValues.containsValue(newPlan)) {
                    toAdd.add(newPlan);
                }
            }

            // delete
            for(Long healthPlanId : toDelete) {
                coveragePlanDescriptionDao.delete(database, healthPlanId);
                policyActivityDao.delete(database, healthPlanId);
                participantDao.delete(database, ParticipantType.POLICY_TARGET, healthPlanId);
                payerDao.delete(database, healthPlanId);
                organizationDao.delete(database, OrganizationType.PAYER, healthPlanId.toString());

                residentHealthPlanDao.delete(healthPlanId);
            }

            if (!toAdd.isEmpty()) {
                // insert ResidentHealthPlans
                List<ResidentHealthPlan> listToAdd = new ArrayList<ResidentHealthPlan>();
                listToAdd.addAll(toAdd);

                long lastId = residentHealthPlanDao.getLastId();
                residentHealthPlanDao.insert(listToAdd);
                Map<Long, ResidentHealthPlan> healthPlans = residentHealthPlanDao.getHealthPlansForResident(residentNewId, lastId);

                // insert Payers, Participants, PolicyActivities, CoveragePlanDescriptions
                IdMapping<Long> payersIdMapping = insertPayers(database, healthPlans);
                IdMapping<Long> participantsIdMapping = insertParticipants(database, healthPlans, residentPersonNewId);
                IdMapping<String> payerOrganizationsIdMapping = insertPayerOrganizations(database, healthPlans);
                IdMapping<Long> policyActivitiesIdMapping = insertPolicyActivities(database, healthPlans, sourceResident, payersIdMapping, participantsIdMapping, payerOrganizationsIdMapping);
                insertCoveragePlanDescriptions(database, healthPlans, policyActivitiesIdMapping);
            }
        }
    }*/

    private void syncResidentsHealthPlans(List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping, IdMapping<String> personsIdMapping, DatabaseInfo database,
                                          Map<ResidentData,ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        for (ResidentData sourceResident : sourceResidents) {
            Long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            Long residentPersonNewId = personsIdMapping.getNewIdOrThrowException(sourceResident.getId().toString());

            syncResidentHealthPlans(database, sourceResident, residentNewId, residentPersonNewId);

            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), PersonType.RESIDENT, sourceResident.getId().toString());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                syncResidentHealthPlans(new IdAwareImpl(mappedDatabaseIdWithId.getDatabaseId()), sourceResident, mappedNewId, mappedPersonId);
            }
        }
    }

    private void syncResidentHealthPlans(IdAware database, ResidentData sourceResident, Long residentNewId, Long residentPersonNewId) {
        Map<Long, ResidentHealthPlan> srcValues= residentHealthPlanDao.getHealthPlansForResident(residentNewId);
        List<ResidentHealthPlan> destValues = payerAssembler.getResidentHealthPlans(sourceResident, residentNewId, database.getId());

        Set<Long> toDelete = new HashSet<Long>();
        Set<ResidentHealthPlan> toAdd = new HashSet<ResidentHealthPlan>();

        for(Map.Entry<Long, ResidentHealthPlan> existingPlan : srcValues.entrySet()) {
            if(!destValues.contains(existingPlan.getValue())) {
                toDelete.add(existingPlan.getKey());
            }
        }
        for(ResidentHealthPlan newPlan : destValues) {
            if(!srcValues.containsValue(newPlan)) {
                toAdd.add(newPlan);
            }
        }

        // delete
        for(Long healthPlanId : toDelete) {
            coveragePlanDescriptionDao.delete(database, healthPlanId);
            policyActivityDao.delete(database, healthPlanId);
            participantDao.delete(database, ParticipantType.POLICY_TARGET, healthPlanId);
            payerDao.delete(database, healthPlanId);
            organizationDao.delete(database, OrganizationType.PAYER, healthPlanId.toString());

            residentHealthPlanDao.delete(healthPlanId);
        }

        if (!toAdd.isEmpty()) {
            // insert ResidentHealthPlans
            List<ResidentHealthPlan> listToAdd = new ArrayList<ResidentHealthPlan>();
            listToAdd.addAll(toAdd);

            long lastId = residentHealthPlanDao.getLastId();
            residentHealthPlanDao.insert(listToAdd);
            Map<Long, ResidentHealthPlan> healthPlans = residentHealthPlanDao.getHealthPlansForResident(residentNewId, lastId);

            // insert Payers, Participants, PolicyActivities, CoveragePlanDescriptions
            IdMapping<Long> payersIdMapping = insertPayers(database, healthPlans);
            IdMapping<Long> participantsIdMapping = insertParticipants(database, healthPlans, residentPersonNewId);
            IdMapping<String> payerOrganizationsIdMapping = insertPayerOrganizations(database, healthPlans);
            IdMapping<Long> policyActivitiesIdMapping = insertPolicyActivities(database, healthPlans, sourceResident, payersIdMapping, participantsIdMapping, payerOrganizationsIdMapping);
            insertCoveragePlanDescriptions(database, healthPlans, policyActivitiesIdMapping);
        }
    }

    private IdMapping<Long> insertPayers(IdAware database, Map<Long, ResidentHealthPlan> healthPlans) {
        List<Payer> payers = new ArrayList<Payer>();
        for (Map.Entry<Long, ResidentHealthPlan> entry : healthPlans.entrySet()) {
            payers.add(payerAssembler.createPayer(entry.getValue(), entry.getKey(), database.getId()));
        }
        long lastPayerIdBeforeInsert = payerDao.getLastId();
        payerDao.insert(payers);
        return payerDao.getIdMapping(database, lastPayerIdBeforeInsert);
    }

    private IdMapping<Long> insertParticipants(IdAware database, Map<Long, ResidentHealthPlan> healthPlans, Long personNewId) {
        List<Participant> participants = new ArrayList<Participant>();
        for (Long healthPlanId : healthPlans.keySet()) {
            participants.add(payerAssembler.createParticipant(personNewId, healthPlanId, database.getId()));
        }
        long lastParticipantIdBeforeInsert = participantDao.getLastId();
        participantDao.insert(participants);
        return participantDao.getIdMapping(database, ParticipantType.POLICY_TARGET, lastParticipantIdBeforeInsert);
    }

    private IdMapping<String> insertPayerOrganizations(IdAware database, Map<Long, ResidentHealthPlan> healthPlans) {
        List<Organization> organizations = new ArrayList<Organization>();
        for (Map.Entry<Long, ResidentHealthPlan> entry : healthPlans.entrySet()) {
            organizations.add(payerAssembler.createPayerOrganization(entry.getValue(), entry.getKey(), database.getId()));
        }

        long lastOrganizationIdBeforeInsert = organizationDao.getLastId();
        organizationDao.insert(organizations);
        return organizationDao.getPayerIdMapping(database, lastOrganizationIdBeforeInsert);
    }

    private IdMapping<Long> insertPolicyActivities(IdAware database, Map<Long, ResidentHealthPlan> healthPlans, ResidentData resident,
                                                   IdMapping<Long> payersIdMapping, IdMapping<Long> participantsIdMapping, IdMapping<String> payerOrganizationIdMapping) {
        List<PolicyActivity> policyActivities = new ArrayList<PolicyActivity>();
        for (Map.Entry<Long, ResidentHealthPlan> entry : healthPlans.entrySet()) {
            Long healthPlanId = entry.getKey();
            long payerNewId = payersIdMapping.getNewIdOrThrowException(healthPlanId);
            long participantNewId = participantsIdMapping.getNewIdOrThrowException(healthPlanId);
            long payerOrganizationNewId = payerOrganizationIdMapping.getNewIdOrThrowException(healthPlanId.toString());

            policyActivities.add(payerAssembler.createPolicyActivity(payerNewId, participantNewId, healthPlanId, database.getId(), resident, payerOrganizationNewId, entry.getValue()));
        }
        long lastPolicyActivityIdBeforeInsert = policyActivityDao.getLastId();
        policyActivityDao.insert(policyActivities);
        return policyActivityDao.getIdMapping(database, lastPolicyActivityIdBeforeInsert);
    }

    private void insertCoveragePlanDescriptions(IdAware database, Map<Long, ResidentHealthPlan> healthPlans, IdMapping<Long> policyActivitiesIdMapping) {
        List<CoveragePlanDescription> coveragePlanDescriptions = new ArrayList<CoveragePlanDescription>();
        for (Map.Entry<Long, ResidentHealthPlan> entry : healthPlans.entrySet()) {
            long policyActivityNewId = policyActivitiesIdMapping.getNewIdOrThrowException(entry.getKey());

            coveragePlanDescriptions.add(payerAssembler.createCoveragePlanDescription(entry.getValue(), policyActivityNewId, entry.getKey(), database.getId()));
        }
        coveragePlanDescriptionDao.insert(coveragePlanDescriptions);
    }

    private void insertNames(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<String> personsIdMapping, Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<PersonName> names = new ArrayList<PersonName>();
        for (ResidentData sourceResident : sourceResidents) {
            long personId = personsIdMapping.getNewIdOrThrowException(sourceResident.getId().toString());
            names.add(nameAssembler.createName(sourceResident, personId, database));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), PersonType.RESIDENT, sourceResident.getId().toString());
                names.add(nameAssembler.createMappedName(sourceResident, mappedPersonId, foreignKeys, targetOrganizationsIdMapping));
            }
        }
        nameDao.insert(names);
    }

    private void insertMPI(List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping, Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<PersonIdentifier> entries = new ArrayList<PersonIdentifier>();
        for (ResidentData sourceResident : sourceResidents) {
            Long newResidentId = residentsIdMapping.getNewId(sourceResident.getId());
            PersonIdentifier personIdentifier = new PersonIdentifier();

            personIdentifier.setRegistryPatientId(UUID.randomUUID().toString());
            personIdentifier.setMerged("N");
            personIdentifier.setDeleted("N");
            personIdentifier.setPatientId(newResidentId.toString());
            personIdentifier.setResidentId(newResidentId);
            //TODO REMOVE HARDCODE!!!
            personIdentifier.setAssigningAuthorityUniversalType("ISO");
            personIdentifier.setAssigningAuthorityUniversal("2.16.840.1.113883.3.6492");
            personIdentifier.setAssigningAuthorityNamespace("EXCHANGE");
            personIdentifier.setAssigningAuthority("EXCHANGE&2.16.840.1.113883.3.6492&ISO");
            entries.add(personIdentifier);
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                Long newFacilityOrganizationId = foreignKeysMap.get(sourceResident).getFacilityOrganizationId();
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(newFacilityOrganizationId);
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), newFacilityOrganizationId));
                if (mappedNewId != null) {
                    PersonIdentifier mappedPersonIdentifier = new PersonIdentifier();
                    mappedPersonIdentifier.setRegistryPatientId(UUID.randomUUID().toString());
                    mappedPersonIdentifier.setMerged("N");
                    mappedPersonIdentifier.setDeleted("N");
                    mappedPersonIdentifier.setPatientId(mappedNewId.toString());
                    mappedPersonIdentifier.setResidentId(mappedNewId);
                    //TODO REMOVE HARDCODE!!!
                    mappedPersonIdentifier.setAssigningAuthorityUniversalType("ISO");
                    mappedPersonIdentifier.setAssigningAuthorityUniversal("2.16.840.1.113883.3.6492");
                    mappedPersonIdentifier.setAssigningAuthorityNamespace("EXCHANGE");
                    mappedPersonIdentifier.setAssigningAuthority("EXCHANGE&2.16.840.1.113883.3.6492&ISO");
                    entries.add(mappedPersonIdentifier);
                }
            }
        }
        mpiDao.insert(entries);
    }

    private void insertAddresses(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<String> personsIdMapping,
                                 Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                 OrganizationAddressResolver organizationAddressResolver, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<PersonAddress> addresses = new ArrayList<PersonAddress>();
        for (ResidentData sourceResident : sourceResidents) {
            long personId = personsIdMapping.getNewIdOrThrowException(sourceResident.getId().toString());

            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
            Long facilityNewId = foreignKeys.getFacilityOrganizationId();

            PersonAddress address;
            if (facilityNewId != null) {
                OrganizationAddress facilityAddress = organizationAddressResolver.getCompanyAddress(facilityNewId);
                address = addressAssembler.createAddress(sourceResident, facilityAddress, personId, database.getId());
            } else {
                address = addressAssembler.createEmptyAddress(sourceResident, personId, database.getId());
            }
            addresses.add(address);

            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), PersonType.RESIDENT, sourceResident.getId().toString());
                PersonAddress mappedAddress;
                if (facilityNewId != null) {
                    //Org address is taken from source organization (not mapped)
                    OrganizationAddress facilityAddress = organizationAddressResolver.getCompanyAddress(facilityNewId);
                    mappedAddress = addressAssembler.createAddress(sourceResident, facilityAddress, mappedPersonId, mappedDatabaseIdWithId.getDatabaseId());
                } else {
                    mappedAddress = addressAssembler.createEmptyAddress(sourceResident, mappedPersonId, mappedDatabaseIdWithId.getDatabaseId());
                }
                addresses.add(mappedAddress);
            }
        }

        addressDao.insert(addresses);
    }

    private void insertTelecoms(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<String> personsIdMapping, Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();
        for (ResidentData sourceResident : sourceResidents) {
            long personId = personsIdMapping.getNewIdOrThrowException(sourceResident.getId().toString());
            telecoms.addAll(telecomAssembler.createAllTelecoms(sourceResident, personId, database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedPersonId = personDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(), PersonType.RESIDENT, sourceResident.getId().toString());
                telecoms.addAll(telecomAssembler.createAllTelecoms(sourceResident, mappedPersonId, mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        telecomDao.insert(telecoms);
    }

    private void insertAuthors(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping,
                               Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<Author> authors = new ArrayList<Author>();
        for (ResidentData sourceResident : sourceResidents) {
            long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());

            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
            Long facilityNewId = foreignKeys.getFacilityOrganizationId();
            authors.add(authorAssembler.createAuthor(facilityNewId, residentNewId,
                    sourceResident.getId(), database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                authors.add(authorAssembler.createAuthor(mappedDatabaseIdWithId.getId(), mappedNewId,
                        sourceResident.getId(), mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        authorDao.insert(authors);
    }

    private void insertLanguages(DatabaseInfo database, List<ResidentData> sourceResidents, IdMapping<Long> residentsIdMapping,
                                 Map<ResidentData, ResidentForeignKeys> foreignKeysMap, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        List<Language> languages = new ArrayList<Language>();
        for (ResidentData sourceResident : sourceResidents) {
            ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);

            long residentNewId = residentsIdMapping.getNewIdOrThrowException(sourceResident.getId());
            languages.add(languageAssembler.createLanguage(sourceResident, foreignKeys, residentNewId, database.getId()));
            if (MappingUtils.hasMappingForResidentOrganization(sourceResident, foreignKeysMap, targetOrganizationsIdMapping)) {
                DatabaseIdWithId mappedDatabaseIdWithId = targetOrganizationsIdMapping.get(foreignKeys.getFacilityOrganizationId());
                Long mappedNewId = residentDao.getNewId(mappedDatabaseIdWithId.getDatabaseId(),  MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
                languages.add(languageAssembler.createLanguage(sourceResident, foreignKeys, mappedNewId, mappedDatabaseIdWithId.getDatabaseId()));
            }
        }
        languageDao.insert(languages);
    }
}
