package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.SyncQualifiers;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.ContactsForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.ResContactData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.normalizers.PersonNamesNormalizer;
import com.scnsoft.eldermark.exchange.normalizers.PersonPhonesNormalizer;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactSyncService extends StandardSyncService<ResContactData, Long, ContactsForeignKeys> {
    private static final String ROLE_NOK = "NOK";
    private static final String ROLE_GUAR = "GUARD";
    private static final String ROLE_ECON = "ECON";
    private static final String ROLE_AGNT = "AGNT";
    @Autowired
    @Qualifier("resContactSourceDao")
    private StandardSourceDao<ResContactData, Long> sourceDao;

    @Autowired
    private ParticipantDao participantDao;

    @Autowired
    private GuardianDao guardianDao;

    @Autowired
    private PersonContactDao personDao;

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

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private CcdCodesValidator ccdCodesValidator;

    private final Map<String, CcdCode> roleCodesMap = new HashMap<String, CcdCode>();

    private Map<String, TelecomUseCodes> telecomPrefixesMap = new HashMap<String, TelecomUseCodes>();

    @PostConstruct
    public void init() {
        roleCodesMap.put(ROLE_AGNT, ccdCodeDao.getContactRoleByCode(ROLE_AGNT));
        roleCodesMap.put(ROLE_NOK, ccdCodeDao.getContactRoleByCode(ROLE_NOK));
        roleCodesMap.put(ROLE_GUAR, ccdCodeDao.getContactRoleByCode(ROLE_GUAR));
        roleCodesMap.put(ROLE_ECON, ccdCodeDao.getContactRoleByCode(ROLE_ECON));

        telecomPrefixesMap.put("work", TelecomUseCodes.WORK_PLACE);
        telecomPrefixesMap.put("work2", TelecomUseCodes.WORK_PLACE);
        telecomPrefixesMap.put("home", TelecomUseCodes.HOME_PHONE);
        telecomPrefixesMap.put("cell", TelecomUseCodes.MOBILE_CELL);
        telecomPrefixesMap.put("cell2", TelecomUseCodes.MOBILE_CELL);
    }

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(ResidentSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<ResContactData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ResContactData.TABLE_NAME, ResContactData.UNIQUE_ID, ResContactData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return personDao.getContactsIdMapping(syncContext.getDatabase(), PersonType.CONTACT, legacyIds);
    }

    @Override
    protected FKResolveResult<ContactsForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                      ResContactData contact) {

        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        ContactsForeignKeys foreignKeys = new ContactsForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResContactData.TABLE_NAME, contact.getId());

        Long residentLegacyId = contact.getResNumber();
        if (residentLegacyId != null) {
            try {
                long residentNewId = residentIdResolver.getId(residentLegacyId, database);
                foreignKeys.setResidentId(residentNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        String companyLegacyId = contact.getFacility();
        if (!Utils.isEmpty(companyLegacyId)) {
            try {
                long companyNewId = companyIdResolver.getId(companyLegacyId, database);
                foreignKeys.setFacilityOrganizationId(companyNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, companyLegacyId));
            }
        }

        CcdCode roleCcdCode = roleCodesMap.get(getRoleCode(contact));
        foreignKeys.setRoleCodeId(roleCcdCode.getId());

        Long relationshipCcdId = ExchangeUtils.replaceZeroByNull(contact.getRelationshipCcdId());
        if (relationshipCcdId != null) {
            if (ccdCodesValidator.validate(relationshipCcdId)) {
                foreignKeys.setRelationshipCodeId(relationshipCcdId);
            } else {
                errors.add(errorFactory.newInvalidCcdIdError(ResContactData.RELATIONSHIP_CCDID, relationshipCcdId));
            }
        }

        return new FKResolveResult<ContactsForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResContactData> contacts,
                                       Map<ResContactData, ContactsForeignKeys> foreignKeysMap) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        List<Person> persons = new ArrayList<Person>();
        for (ResContactData contact : contacts) {
            Person person = new Person();
            person.setDatabaseId(database.getId());
            person.setLegacyId(contact.getId().toString());
            person.setLegacyTable(PersonType.CONTACT.getTableName());

            persons.add(person);
        }
        long lastPersonId = personDao.getLastId();
        personDao.insert(persons);
        IdMapping<String> personsIdMapping = personDao.getIdMapping(database, PersonType.CONTACT, lastPersonId);

        // insert participants
        List<Participant> participants = new ArrayList<Participant>();
        for (ResContactData contact : contacts) {
            long personId = personsIdMapping.getNewIdOrThrowException(contact.getId().toString());
            ContactsForeignKeys foreignKeys = foreignKeysMap.get(contact);

            Participant participant = new Participant();
            participant.setLegacyId(contact.getId());
            participant.setLegacyTable(ParticipantType.CONTACT.getTableName());
            participant.setDatabaseId(database.getId());
            participant.setPersonId(personId);
            participant.setUpdatable(createParticipantUpdatable(contact, foreignKeys));

            participants.add(participant);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Participant mappedParticipant = new Participant();
                mappedParticipant.setLegacyId(contact.getId());
                mappedParticipant.setLegacyTable(ParticipantType.CONTACT.getTableName());
                mappedParticipant.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedParticipant.setPersonId(personId);
                mappedParticipant.setUpdatable(createMappedParticipantUpdatable(contact, foreignKeys, databaseIdWithMappedResidentId.getId()));
                participants.add(mappedParticipant);
            }
        }
        participantDao.insert(participants);

        // insert guardians
        List<Guardian> guardians = new ArrayList<Guardian>();
        for (ResContactData contact : contacts) {
            if (contact.isGuardian()) {
                long personId = personsIdMapping.getNewIdOrThrowException(contact.getId().toString());
                ContactsForeignKeys foreignKeys = foreignKeysMap.get(contact);

                Guardian guardian = new Guardian();
                guardian.setLegacyId(contact.getId());
                guardian.setDatabaseId(database.getId());
                guardian.setPersonId(personId);
                guardian.setUpdatable(createGuardianUpdatable(foreignKeys));

                guardians.add(guardian);

                if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                    DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                    Guardian mappedGuardian = new Guardian();
                    mappedGuardian.setLegacyId(contact.getId());
                    mappedGuardian.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                    mappedGuardian.setPersonId(personId);
                    mappedGuardian.setUpdatable(createMappedGuardianUpdatable(foreignKeys, databaseIdWithMappedResidentId.getId()));
                    guardians.add(mappedGuardian);
                }
            }
        }
        guardianDao.insert(guardians);

        List<PersonName> names = new ArrayList<PersonName>();
        List<PersonAddress> addresses = new ArrayList<PersonAddress>();
        List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();
        for (ResContactData contact : contacts) {
            String contactLegacyIdStr = contact.getId().toString();
            long personId = personsIdMapping.getNewIdOrThrowException(contactLegacyIdStr);

            PersonName name = new PersonName();
            name.setPersonId(personId);
            name.setDatabaseId(database.getId());
            name.setLegacyId(contactLegacyIdStr);
            name.setLegacyTable(PersonType.CONTACT.getTableName());
            name.setUpdatable(createNameUpdatable(contact));
            names.add(name);

            PersonAddress address = new PersonAddress();
            address.setPersonId(personId);
            address.setLegacyId(contactLegacyIdStr);
            address.setLegacyTable(PersonType.CONTACT.getTableName());
            address.setDatabaseId(database.getId());
            address.setUpdatable(createAddressUpdatable(contact));
            addresses.add(address);

            String phone1 = contact.getPhone1();
            String phone2 = contact.getPhone2();
            String phone3 = contact.getPhone3();
            String email = contact.getEmail();

            PersonTelecom telecom1 = new PersonTelecom();
            telecom1.setPersonId(personId);
            telecom1.setDatabaseId(database.getId());
            telecom1.setLegacyId(contactLegacyIdStr);
            telecom1.setLegacyTable(PersonType.CONTACT.getTableName());
            telecom1.setUpdatable(createPhoneUpdatable(phone1));
            telecom1.setSyncQualifier(SyncQualifiers.TELECOM_PHONE1);

            PersonTelecom telecom2 = new PersonTelecom();
            telecom2.setPersonId(personId);
            telecom2.setDatabaseId(database.getId());
            telecom2.setLegacyId(contactLegacyIdStr);
            telecom2.setLegacyTable(PersonType.CONTACT.getTableName());
            telecom2.setUpdatable(createPhoneUpdatable(phone2));
            telecom2.setSyncQualifier(SyncQualifiers.TELECOM_PHONE2);

            PersonTelecom telecom3 = new PersonTelecom();
            telecom3.setPersonId(personId);
            telecom3.setDatabaseId(database.getId());
            telecom3.setLegacyId(contactLegacyIdStr);
            telecom3.setLegacyTable(PersonType.CONTACT.getTableName());
            telecom3.setUpdatable(createPhoneUpdatable(phone3));
            telecom3.setSyncQualifier(SyncQualifiers.TELECOM_PHONE3);

            PersonTelecom telecom4 = new PersonTelecom();
            telecom4.setPersonId(personId);
            telecom4.setDatabaseId(database.getId());
            telecom4.setLegacyId(contactLegacyIdStr);
            telecom4.setLegacyTable(PersonType.CONTACT.getTableName());
            telecom4.setUpdatable(createEmailUpdatable(email));
            telecom4.setSyncQualifier(SyncQualifiers.TELECOM_EMAIL);

            telecoms.add(telecom1);
            telecoms.add(telecom2);
            telecoms.add(telecom3);
            telecoms.add(telecom4);
        }
        nameDao.insert(names);
        addressDao.insert(addresses);
        telecomDao.insert(telecoms);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResContactData> contacts,
                                    Map<ResContactData, ContactsForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {

        /* Sync Guardians */
        List<Long> legacyIds = Utils.getIds(contacts);
        List<String> stringLegacyIds = Utils.getStringIds(contacts);
        DatabaseInfo database = syncContext.getDatabase();
        IdMapping<Long> existingGuardiansIdMapping = guardianDao.getIdMapping(database, legacyIds);
        IdMapping<Long> existingParticipantsIdMapping = participantDao.getIdMapping(database, ParticipantType.CONTACT, legacyIds);
        IdMapping<String> personNamesIdMapping = nameDao.getIdMapping(syncContext.getDatabase(), PersonType.CONTACT, stringLegacyIds);
        IdMapping<String> personAddressesIdMapping = addressDao.getIdMapping(syncContext.getDatabase(), PersonType.CONTACT, stringLegacyIds);
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();

        final List<ResContactData> toAdd = new ArrayList<ResContactData>();
        final List<ResContactData> toDelete = new ArrayList<ResContactData>();
        final List<ResContactData> toUpdate = new ArrayList<ResContactData>();

        for (ResContactData sourceEntity : contacts) {
            if (existingGuardiansIdMapping.containsLegacyId(sourceEntity.getId())) {
                if (sourceEntity.isGuardian()) {
                    toUpdate.add(sourceEntity);
                } else {
                    toDelete.add(sourceEntity);
                }
            } else {
                if (sourceEntity.isGuardian()) {
                    toAdd.add(sourceEntity);
                }
            }
        }

        // insert contacts that became guardians
        List<Guardian> guardians = new ArrayList<Guardian>();
        for (ResContactData contact : toAdd) {
            long contactLegacyId = contact.getId();
            long personId = idMapping.getNewIdOrThrowException(contactLegacyId);
            ContactsForeignKeys foreignKeys = foreignKeysMap.get(contact);

            Guardian guardian = new Guardian();
            guardian.setLegacyId(contactLegacyId);
            guardian.setDatabaseId(syncContext.getDatabaseId());
            guardian.setPersonId(personId);
            guardian.setUpdatable(createGuardianUpdatable(foreignKeys));

            guardians.add(guardian);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Guardian mappedGuardian = new Guardian();
                mappedGuardian.setLegacyId(contact.getId());
                mappedGuardian.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedGuardian.setPersonId(personId);
                mappedGuardian.setUpdatable(createMappedGuardianUpdatable(foreignKeys, databaseIdWithMappedResidentId.getId()));
                guardians.add(mappedGuardian);
            }
        }
        guardianDao.insert(guardians);

        // update contacts that remain guardians
        for (ResContactData sourceContact : toUpdate) {
            long contactLegacyId = sourceContact.getId();
            long personId = idMapping.getNewIdOrThrowException(contactLegacyId);
            ContactsForeignKeys foreignKeys = foreignKeysMap.get(sourceContact);

            Guardian.Updatable guardianUpdatable = createGuardianUpdatable(foreignKeys);
            long guardianId = existingGuardiansIdMapping.getNewId(contactLegacyId);
            guardianDao.updateById(guardianUpdatable, guardianId);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Guardian.Updatable mappedGuardianUpdatable = createMappedGuardianUpdatable(foreignKeys, databaseIdWithMappedResidentId.getId());
                guardianDao.update(mappedGuardianUpdatable, personId, databaseIdWithMappedResidentId.getDatabaseId());
            }
        }

        // delete contacts which are no longer guardians
        for (ResContactData sourceContact : toDelete) {
            ContactsForeignKeys foreignKeys = foreignKeysMap.get(sourceContact);
            Long legacyId = Long.valueOf(sourceContact.getId());
            long guardianId = existingGuardiansIdMapping.getNewId(legacyId);
            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                guardianDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
            }
            guardianDao.delete(guardianId);
        }

        /* Sync Participants */

        for (ResContactData sourceContact : contacts) {
            long contactLegacyId = sourceContact.getId();
            long personId = idMapping.getNewIdOrThrowException(contactLegacyId);
            ContactsForeignKeys foreignKeys = foreignKeysMap.get(sourceContact);
            long participantId = existingParticipantsIdMapping.getNewId(contactLegacyId);

            Participant.Updatable participantUpdate = createParticipantUpdatable(sourceContact, foreignKeys);
            participantDao.updateById(participantUpdate, participantId);
            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Participant.Updatable participantMappedUpdate = createMappedParticipantUpdatable(sourceContact, foreignKeys, databaseIdWithMappedResidentId.getId());
                participantDao.update(participantMappedUpdate, ParticipantType.CONTACT, personId, databaseIdWithMappedResidentId.getDatabaseId());
            }

            PersonName.Updatable nameUpdate = createNameUpdatable(sourceContact);
            Long nameId = personNamesIdMapping.getNewId(String.valueOf(contactLegacyId));
            nameDao.updateById(nameUpdate, nameId);

            PersonAddress.Updatable addressUpdate = createAddressUpdatable(sourceContact);
            Long addressId = personAddressesIdMapping.getNewId(String.valueOf(contactLegacyId));
            addressDao.updateById(addressUpdate, addressId);

            PersonTelecom.Updatable phone1Update = createPhoneUpdatable(sourceContact.getPhone1());
            telecomDao.update(phone1Update, personId, SyncQualifiers.TELECOM_PHONE1);

            PersonTelecom.Updatable phone2Update = createPhoneUpdatable(sourceContact.getPhone2());
            telecomDao.update(phone2Update, personId, SyncQualifiers.TELECOM_PHONE2);

            PersonTelecom.Updatable phone3Update = createPhoneUpdatable(sourceContact.getPhone3());
            telecomDao.update(phone3Update, personId, SyncQualifiers.TELECOM_PHONE3);

            PersonTelecom.Updatable emailUpdate = createEmailUpdatable(sourceContact.getEmail());
            telecomDao.update(emailUpdate, personId, SyncQualifiers.TELECOM_EMAIL);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        PersonType personType = PersonType.CONTACT;
        long legacyId = Long.valueOf(legacyIdString);

        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_PHONE1);
        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_PHONE2);
        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_PHONE3);
        telecomDao.delete(database, personType, legacyIdString, SyncQualifiers.TELECOM_EMAIL);

        addressDao.delete(database, personType, legacyIdString);

        nameDao.delete(database, personType, legacyIdString);

        Long residentId = participantDao.getResidentId(database, legacyId, ParticipantType.CONTACT);
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            participantDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), ParticipantType.CONTACT, legacyId, databaseIdWithMappedResidentId.getId());
            guardianDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
        }

        participantDao.delete(database, ParticipantType.CONTACT, legacyId);
        guardianDao.delete(database, legacyId);

        personDao.delete(database, personType, legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private Participant.Updatable createParticipantUpdatable(ResContactData contact, ContactsForeignKeys foreignKeys) {
        return createParticipantUpdatable(contact, foreignKeys, null);
    }

    private Participant.Updatable createMappedParticipantUpdatable(ResContactData contact, ContactsForeignKeys foreignKeys, Long mappedResidentId) {
        return createParticipantUpdatable(contact, foreignKeys, mappedResidentId);
    }

    private Participant.Updatable createParticipantUpdatable(ResContactData contact, ContactsForeignKeys foreignKeys, Long residentId) {
        Participant.Updatable updatable = new Participant.Updatable();
        updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
        updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
        updatable.setRelationshipCodeId(foreignKeys.getRelationshipCodeId());
        updatable.setRoleCodeId(foreignKeys.getRoleCodeId());
        updatable.setPriority(contact.getPriority());
        updatable.setResponsibleParty(contact.getResponsibleParty());
        return updatable;
    }

    private Guardian.Updatable createGuardianUpdatable(ContactsForeignKeys foreignKeys) {
        return createGuardianUpdatable(foreignKeys, null);
    }

    private Guardian.Updatable createMappedGuardianUpdatable(ContactsForeignKeys foreignKeys, Long mappedResidentId) {
        return createGuardianUpdatable(foreignKeys, mappedResidentId);
    }

    private Guardian.Updatable createGuardianUpdatable(ContactsForeignKeys foreignKeys, Long residentId) {
        Guardian.Updatable updatable = new Guardian.Updatable();
        updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
        updatable.setRelationshipCodeId(foreignKeys.getRelationshipCodeId());
        return updatable;
    }

    private PersonName.Updatable createNameUpdatable(ResContactData contact) {
        String lastName = contact.getLastName();
        String firstName = contact.getFirstName();

        PersonName.Updatable updatable = new PersonName.Updatable();
        updatable.setFamily(lastName);
        updatable.setFamilyNormalized(namesNormalizer.normalizeName(lastName));
        updatable.setGiven(firstName);
        updatable.setGivenNormalized(namesNormalizer.normalizeName(firstName));
        updatable.setPrefix(contact.getSalutation());

        return updatable;
    }

    private PersonAddress.Updatable createAddressUpdatable(ResContactData contact) {
        PersonAddress.Updatable updatable = new PersonAddress.Updatable();
        updatable.setPostalAddressUse("HP");
        updatable.setCountry("US");
        if (contact.isUseAltAddress()) {
            updatable.setStreetAddress(contact.getAltStreetAddress());
            updatable.setCity(contact.getAltCity());
            updatable.setState(contact.getAltState());
            updatable.setPostalCode(contact.getAltZip());
        } else {
            updatable.setStreetAddress(contact.getStreetAddress());
            updatable.setCity(contact.getCity());
            updatable.setState(contact.getState());
            updatable.setPostalCode(contact.getZip());
        }
        return updatable;
    }

    private PersonTelecom.Updatable createPhoneUpdatable(String phone) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(phone);
        updatable.setValueNormalized(phonesNormalizer.normalizePhone(phone));

        if (phone != null) {
            for(Map.Entry<String, TelecomUseCodes> entry : telecomPrefixesMap.entrySet()) {
                if(phone.toLowerCase().contains(entry.getKey()))
                    updatable.setUseCode(entry.getValue().getValue());
            }
        }
        return updatable;
    }

    private PersonTelecom.Updatable createEmailUpdatable(String email) {
        PersonTelecom.Updatable updatable = new PersonTelecom.Updatable();
        updatable.setValue(email);
        updatable.setValueNormalized(email);
        updatable.setUseCode(TelecomUseCodes.EMAIL.getValue());
        return updatable;
    }

    private String getRoleCode(ResContactData contact) {
        String roleCode = ROLE_AGNT;
        int countRoles = 0;
        if (contact.isNearestRelative()) {
            roleCode = ROLE_NOK;
            countRoles++;
        }
        if (contact.isResponsibleParty()) {
            roleCode = ROLE_GUAR;
            countRoles++;
        }
        if (contact.isEmergency()) {
            roleCode = ROLE_ECON;
            countRoles++;
        }
        if (contact.isGuardian()) {
            countRoles++;
        }
        if (contact.isDesignatedPerson()) {
            countRoles++;
        }
        if (contact.isDFPOA()) {
            countRoles++;
        }
        if (contact.isDHPOA()) {
            countRoles++;
        }
        if (contact.isHealthCareProxy()) {
            countRoles++;
        }
        if (contact.isMHPOA()) {
            countRoles++;
        }
        if (contact.isPowerOfAttorney()) {
            countRoles++;
        }
        if (contact.isRepresentative()) {
            countRoles++;
        }
        if (countRoles != 1) {
            roleCode = ROLE_AGNT;
        }

        return roleCode;
    }
}