package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.SyncQualifiers;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.MedicalProfessionalForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.MedicalProfessionalData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.normalizers.PersonNamesNormalizer;
import com.scnsoft.eldermark.exchange.normalizers.PersonPhonesNormalizer;
import com.scnsoft.eldermark.exchange.resolvers.AuthorIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedicalProfessionalIdResolver;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MedicalProfessionalSyncService extends StandardSyncService<MedicalProfessionalData, Long, MedicalProfessionalForeignKeys> {
    @Autowired
    @Qualifier("medicalProfessionalSourceDao")
    private StandardSourceDao<MedicalProfessionalData, Long> sourceDao;

    @Value("${medicalprofessionals.idmapping.cache.size}")
    private int idMappingLimit;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private MedicalProfessionalDao medicalProfessionalDao;

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
    private PersonNamesNormalizer namesNormalizer;

    @Autowired
    private PersonPhonesNormalizer phonesNormalizer;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencyServices = new ArrayList<Class<? extends SyncService>>();
        dependencyServices.add(CompanySyncService.class);
        return dependencyServices;
    }

    @Override
    protected StandardSourceDao<MedicalProfessionalData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(MedicalProfessionalData.TABLE_NAME, MedicalProfessionalData.CODE,
                MedicalProfessionalData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return medicalProfessionalDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<MedicalProfessionalForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                                 MedicalProfessionalData entity) {
        final CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        DatabaseInfo database = syncContext.getDatabase();

        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedicalProfessionalData.TABLE_NAME, entity.getId());
        MedicalProfessionalForeignKeys foreignKeys = new MedicalProfessionalForeignKeys();

        String facilityLegacyId = entity.getFacility();
        if (!Utils.isEmpty(facilityLegacyId)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facilityLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facilityLegacyId));
            }
        }

        return new FKResolveResult<MedicalProfessionalForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext,
                                       List<MedicalProfessionalData> medicalProfessionals,
                                       Map<MedicalProfessionalData, MedicalProfessionalForeignKeys> foreignKeysMap) {
        IdMapping<String> existingPersonsIdMapping = personDao.getIdMapping(syncContext.getDatabase(), PersonType.MED_PROFESSIONALS,
                Utils.getStringIds(medicalProfessionals));

        final List<MedicalProfessionalData> newEntities = new ArrayList<MedicalProfessionalData>();
        final List<MedicalProfessionalData> existingEntities = new ArrayList<MedicalProfessionalData>();
        for (MedicalProfessionalData sourceEntity : medicalProfessionals) {
            if (existingPersonsIdMapping.containsLegacyId(sourceEntity.getId().toString())) {
                existingEntities.add(sourceEntity);
            } else {
                newEntities.add(sourceEntity);
            }
        }

        DatabaseInfo database = syncContext.getDatabase();

        if(!existingEntities.isEmpty()) {
            List<MedicalProfessional> medicalProfessionalList = new ArrayList<MedicalProfessional>();
            for (MedicalProfessionalData sourceData : existingEntities) {
                MedicalProfessionalForeignKeys foreignKeys = foreignKeysMap.get(sourceData);
                long personId = existingPersonsIdMapping.getNewIdOrThrowException(sourceData.getId().toString());

                MedicalProfessional medicalProfessional = new MedicalProfessional();
                medicalProfessional.setDatabaseId(database.getId());
                medicalProfessional.setLegacyId(sourceData.getId());
                medicalProfessional.setUpdatable(createMedicalProfessionalUpdatable(sourceData, personId, foreignKeys));

                medicalProfessionalList.add(medicalProfessional);
            }
            medicalProfessionalDao.insert(medicalProfessionalList);
        }

        if(!newEntities.isEmpty()) {
            List<Person> persons = new ArrayList<Person>();
            for (MedicalProfessionalData medicalProfessional : newEntities) {
                Person person = new Person();
                person.setDatabaseId(database.getId());
                person.setLegacyTable(PersonType.MED_PROFESSIONALS.getTableName());
                person.setLegacyId(medicalProfessional.getId().toString());

                persons.add(person);
            }
            long lastId = personDao.getLastId();
            personDao.insert(persons);
            IdMapping<String> idMapping = personDao.getIdMapping(database, PersonType.MED_PROFESSIONALS, lastId);

            List<Author> authors = new ArrayList<Author>();
            for (MedicalProfessionalData medicalProfessional : newEntities) {
                long personId = idMapping.getNewIdOrThrowException(medicalProfessional.getId().toString());

                Author.Updatable updatable = new Author.Updatable();
                updatable.setPersonId(personId);

                Author author = new Author();
                author.setDatabaseId(database.getId());
                author.setLegacyTable(AuthorType.MEDICAL_PROFESSIONALS.getLegacyTableName());
                author.setLegacyId(medicalProfessional.getId());
                author.setUpdatable(updatable);

                authors.add(author);
            }
            authorDao.insert(authors);

            List<PersonName> names = new ArrayList<PersonName>();
            for (MedicalProfessionalData medicalProfessional : newEntities) {
                long personId = idMapping.getNewIdOrThrowException(medicalProfessional.getId().toString());

                PersonName name = new PersonName();
                name.setDatabaseId(database.getId());
                name.setLegacyTable(PersonType.MED_PROFESSIONALS.getTableName());
                name.setLegacyId(medicalProfessional.getId().toString());
                name.setUpdatable(createNameUpdatable(medicalProfessional));
                name.setPersonId(personId);

                names.add(name);
            }
            nameDao.insert(names);

            List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();
            for (MedicalProfessionalData medicalProfessional : newEntities) {
                long personId = idMapping.getNewIdOrThrowException(medicalProfessional.getId().toString());

                String phone = medicalProfessional.getPhone();
                String email = medicalProfessional.getEmail();

                PersonTelecom phoneTelecom = new PersonTelecom();
                phoneTelecom.setUpdatable(createPhoneUpdatable(phone));
                phoneTelecom.setSyncQualifier(SyncQualifiers.TELECOM_PHONE1);
                phoneTelecom.setPersonId(personId);
                phoneTelecom.setDatabaseId(database.getId());
                phoneTelecom.setLegacyTable(PersonType.MED_PROFESSIONALS.getTableName());
                phoneTelecom.setLegacyId(medicalProfessional.getId().toString());

                PersonTelecom emailTelecom = new PersonTelecom();
                emailTelecom.setUpdatable(createEmailUpdatable(email));
                emailTelecom.setSyncQualifier(SyncQualifiers.TELECOM_EMAIL);
                emailTelecom.setPersonId(personId);
                emailTelecom.setDatabaseId(database.getId());
                emailTelecom.setLegacyTable(PersonType.MED_PROFESSIONALS.getTableName());
                emailTelecom.setLegacyId(medicalProfessional.getId().toString());

                telecoms.add(phoneTelecom);
                telecoms.add(emailTelecom);
            }
            telecomDao.insert(telecoms);

            List<PersonAddress> addresses = new ArrayList<PersonAddress>();
            for (MedicalProfessionalData medicalProfessional : newEntities) {
                long personId = idMapping.getNewIdOrThrowException(medicalProfessional.getId().toString());

                PersonAddress address = new PersonAddress();
                address.setDatabaseId(database.getId());
                address.setLegacyTable(PersonType.MED_PROFESSIONALS.getTableName());
                address.setLegacyId(medicalProfessional.getId().toString());
                address.setUpdatable(createAddressUpdatable(medicalProfessional));
                address.setPersonId(personId);

                addresses.add(address);
            }
            addressDao.insert(addresses);

            List<MedicalProfessional> medicalProfessionalList = new ArrayList<MedicalProfessional>();
            for (MedicalProfessionalData sourceData : newEntities) {
                MedicalProfessionalForeignKeys foreignKeys = foreignKeysMap.get(sourceData);
                long personId = idMapping.getNewIdOrThrowException(sourceData.getId().toString());

                MedicalProfessional medicalProfessional = new MedicalProfessional();
                medicalProfessional.setDatabaseId(database.getId());
                medicalProfessional.setLegacyId(sourceData.getId());
                medicalProfessional.setUpdatable(createMedicalProfessionalUpdatable(sourceData, personId, foreignKeys));

                medicalProfessionalList.add(medicalProfessional);
            }
            medicalProfessionalDao.insert(medicalProfessionalList);
        }
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext,
                                    List<MedicalProfessionalData> medicalProfessionals,
                                    Map<MedicalProfessionalData, MedicalProfessionalForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        IdMapping<String> personsIdMapping = personDao.getIdMapping(syncContext.getDatabase(), PersonType.MED_PROFESSIONALS,
                Utils.getStringIds(medicalProfessionals));

        for (MedicalProfessionalData medicalProfessional : medicalProfessionals) {
            MedicalProfessionalForeignKeys foreignKeys = foreignKeysMap.get(medicalProfessional);
            long legacyMedProfessionalId = medicalProfessional.getId();
            long medicalProfessionalId = idMapping.getNewIdOrThrowException(legacyMedProfessionalId);

            long personId = personsIdMapping.getNewIdOrThrowException(medicalProfessional.getId().toString());

            PersonName.Updatable nameUpdate = createNameUpdatable(medicalProfessional);
            nameDao.update(nameUpdate, personId);

            String phone = medicalProfessional.getPhone();
            String email = medicalProfessional.getEmail();

            PersonTelecom.Updatable phoneUpdate = createPhoneUpdatable(phone);
            telecomDao.update(phoneUpdate, personId, SyncQualifiers.TELECOM_PHONE1);

            PersonTelecom.Updatable emailUpdate = createEmailUpdatable(email);
            telecomDao.update(emailUpdate, personId, SyncQualifiers.TELECOM_EMAIL);

            PersonAddress.Updatable addressUpdate = createAddressUpdatable(medicalProfessional);
            addressDao.update(addressUpdate, personId);

            MedicalProfessional.Updatable medicalProfessionalUpdatable = createMedicalProfessionalUpdatable(medicalProfessional, personId, foreignKeys);
            medicalProfessionalDao.update(medicalProfessionalUpdatable, medicalProfessionalId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        long legacyId = Long.valueOf(legacyIdString);
        DatabaseInfo database = syncContext.getDatabase();
        PersonType personType = PersonType.MED_PROFESSIONALS;

        nameDao.delete(database, personType, legacyIdString);
        addressDao.delete(database, personType, legacyIdString);
        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_PHONE1);
        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_EMAIL);
        authorDao.deleteMedicalProfessionals(database, legacyId);
        medicalProfessionalDao.delete(database, legacyId);
        personDao.delete(database, personType, legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        DatabaseInfo database = context.getDatabase();
        final IdMapping<Long> idAuthorMapping = authorDao.getMedicalProfessionalsIdMapping(database, idMappingLimit);
        context.putSharedObject(AuthorIdResolver.class, new AuthorIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idAuthorMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = authorDao.getMedicalProfessionalNewId(database, legacyId);
                }
                return newId;
            }
        });
        final IdMapping<Long> idMapping = medicalProfessionalDao.getIdMapping(context.getDatabase(), idMappingLimit);
        context.putSharedObject(MedicalProfessionalIdResolver.class, new MedicalProfessionalIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = medicalProfessionalDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private PersonName.Updatable createNameUpdatable(MedicalProfessionalData medicalProfessional) {
        String lastName = medicalProfessional.getLastName();
        String firstName = medicalProfessional.getFirstName();
        String middleName = medicalProfessional.getMiddleName();

        PersonName.Updatable updatable = new PersonName.Updatable();
        updatable.setFamily(lastName);
        updatable.setFamilyNormalized(namesNormalizer.normalizeName(lastName));
        updatable.setGiven(firstName);
        updatable.setGivenNormalized(namesNormalizer.normalizeName(firstName));
        updatable.setMiddle(middleName);
        updatable.setMiddleNormalized(namesNormalizer.normalizeName(middleName));
        updatable.setPrefix(medicalProfessional.getPrefixName());
        updatable.setSuffix(medicalProfessional.getSuffixName());
        updatable.setNameUse("L");
        return updatable;
    }

    private PersonTelecom.Updatable createPhoneUpdatable(String phone) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(phone);
        updatable.setValueNormalized(phonesNormalizer.normalizePhone(phone));
        updatable.setUseCode(TelecomUseCodes.WORK_PLACE.getValue());
        return updatable;
    }

    private MedicalProfessional.Updatable createMedicalProfessionalUpdatable(MedicalProfessionalData sourceData, long personId, MedicalProfessionalForeignKeys foreignKeys) {
        MedicalProfessional.Updatable updatable = new MedicalProfessional.Updatable();
        updatable.setPersonId(personId);
        updatable.setNpi(sourceData.getNpi());
        updatable.setInactive(sourceData.getInactive());
        updatable.setOrganizationName(sourceData.getOrganization());
        updatable.setSpeciality(sourceData.getSpeciality());
        updatable.setExtPharmacyId(sourceData.getExtPharmacyId());
        updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
        return updatable;
    }

    private PersonTelecom.Updatable createEmailUpdatable(String email) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(email);
        updatable.setValueNormalized(email);
        updatable.setUseCode(TelecomUseCodes.EMAIL.getValue());
        return updatable;
    }

    private PersonAddress.Updatable createAddressUpdatable(MedicalProfessionalData medicalProfessional) {
        PersonAddress.Updatable updatable = new PersonAddress.Updatable();
        updatable.setPostalAddressUse(null);
        updatable.setStreetAddress(medicalProfessional.getStreet());
        updatable.setCity(medicalProfessional.getCity());
        updatable.setState(medicalProfessional.getState());
        updatable.setPostalCode(medicalProfessional.getZip());
        updatable.setCountry("US");
        return updatable;
    }
}
