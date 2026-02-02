package com.scnsoft.eldermark.exchange.services.employees;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.SyncQualifiers;
import com.scnsoft.eldermark.exchange.dao.source.EmployeeSourceDao;
import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.fk.EmployeeForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.SecurityGroupData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ExchangeRoleCodeToCareTeamRoleIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.GroupIdResolver;
import com.scnsoft.eldermark.exchange.services.BaseSyncService;
import com.scnsoft.eldermark.exchange.services.GroupSyncService;
import com.scnsoft.eldermark.exchange.services.SystemSetupSyncService;
import com.scnsoft.eldermark.exchange.services.residents.PersonAddressAssembler;
import com.scnsoft.eldermark.exchange.services.residents.PersonNameAssembler;
import com.scnsoft.eldermark.exchange.services.residents.PersonTelecomAssembler;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EmployeeSyncService extends BaseSyncService<EmployeeData, String, EmployeeForeignKeys> {
    @Autowired
    private EmployeeSourceDao employeeSourceDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeGroupDao employeeGroupDao;

    @Autowired
    @Qualifier("personDao")
    private PersonDao personDao;

    @Autowired
    private PersonNameDao nameDao;

    @Autowired
    private PersonNameAssembler nameAssembler;

    @Autowired
    private PersonAddressDao addressDao;

    @Autowired
    private PersonAddressAssembler addressAssembler;

    @Autowired
    private PersonTelecomDao telecomDao;

    @Autowired
    private PersonTelecomAssembler telecomAssembler;

    @Autowired
    private SystemSetupDao systemSetupDao;

    @Autowired
    private GroupRoleDao groupRoleDao;

    @Autowired
    private PasswordProvider passwordProvider;

    @Value("${employees.idmapping.cache.size}")
    private int employeesIdMappingLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(GroupSyncService.class);
        dependencies.add(SystemSetupSyncService.class);
        return dependencies;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(EmployeeData.TABLE_NAME, EmployeeData.ID, EmployeeData.class);
    }

    @Override
    protected List<EmployeeData> getSourceEntities(DatabaseSyncContext syncContext,
                                                   SourceEntitiesFilter<String> filter) {
        final DatabaseInfo database = syncContext.getDatabase();
        final String password = passwordProvider.getPassword(database);
        if (!employeeSourceDao.isPasswordValid(syncContext.getSql4dOperations(), password)) {
            throw new RuntimeException("Decrypting password for employees is incorrect.");
        }
        return employeeSourceDao.getEmployees(syncContext.getSql4dOperations(), filter, password);
    }

    @Override
    protected String getSourceEntitiesMaxId(DatabaseSyncContext syncContext, MaxIdFilter<String> filter) {
        return employeeSourceDao.getMaxId(syncContext.getSql4dOperations(), filter);
    }

    @Override
    protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
        return employeeDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<EmployeeForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, EmployeeData entity) {
        GroupIdResolver groupIdResolver = syncContext.getSharedObject(GroupIdResolver.class);

        EmployeeForeignKeys foreignKeys = new EmployeeForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(SecurityGroupData.TABLE_NAME, entity.getId());

        String secGroupIds = entity.getSecGroupIds();
        Set<String> fkCandidateSet;
        try {
            fkCandidateSet = Sets.newHashSet(secGroupIds.split(Constants.COMMA_SEPARATOR));
        } catch (RuntimeException e) {
            fkCandidateSet = new HashSet<String>();
        }
        for(String fkCandidate : fkCandidateSet) {
            try {
                if(!StringUtils.isEmpty(fkCandidate)) {
                    foreignKeys.addGroupId(groupIdResolver.getId(Long.valueOf(fkCandidate), syncContext.getDatabase()));
                }
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Group.TABLE_NAME, fkCandidate));
            } catch (NumberFormatException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Group.TABLE_NAME, fkCandidate));
            }
        }

        return new FKResolveResult<EmployeeForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<EmployeeData> sourceEntities,
                                       Map<EmployeeData, EmployeeForeignKeys> foreignKeysMap) {
        IdMapping<String> personsIdMapping = insertPersons(syncContext.getDatabase(), sourceEntities);
        insertNamesAddressesAndTelecoms(syncContext.getDatabase(), sourceEntities, personsIdMapping);
        ExchangeRoleCodeToCareTeamRoleIdResolver exchangeRoleCodeToCareTeamRoleIdResolver = syncContext.getSharedObject(ExchangeRoleCodeToCareTeamRoleIdResolver.class);

        String companyCode = systemSetupDao.getCode(syncContext.getDatabaseId());

        List<Employee> employees = new ArrayList<Employee>();
        for (EmployeeData sourceEmployee : sourceEntities) {
            Employee employee = new Employee();
            employee.setDatabaseId(syncContext.getDatabaseId());
            employee.setLegacyId(sourceEmployee.getId());
            employee.setPersonId(personsIdMapping.getNewIdOrThrowException(sourceEmployee.getId()));
            employee.setModifiedTimestamp(sourceEmployee.getLastmodStamp() * 1000);
            Long careTeamRoleId = getCareTeamRoleId(foreignKeysMap, exchangeRoleCodeToCareTeamRoleIdResolver, sourceEmployee);
            employee.setUpdatable(createUpdatable(sourceEmployee, careTeamRoleId));
            /*if ((companyCode != null) && (!Utils.isEmpty(employee.getLoginName())))
                employee.setSecureEmail(String.format("%s.%s@direct.simplyhie.com", employee.getLoginName(), companyCode.toLowerCase()));*/

            employees.add(employee);
        }


        long lowerBound = employeeDao.getLastId();
        employeeDao.insert(employees);
        IdMapping<String> insertedIdMapping = employeeDao.getIdMapping(syncContext.getDatabase(), lowerBound);


        List<EmployeeGroup> employeeGroups = new ArrayList<EmployeeGroup>();

        for (EmployeeData sourceEntity : sourceEntities) {
            for (Long groupId : foreignKeysMap.get(sourceEntity).getGroupIds()) {
                EmployeeGroup employeeGroup = new EmployeeGroup();
                employeeGroup.setDatabaseId(syncContext.getDatabaseId());
                employeeGroup.setEmployeeId(insertedIdMapping.getNewIdOrThrowException(sourceEntity.getId()));
                employeeGroup.setGroupId(groupId);
                employeeGroup.setLegacyId(sourceEntity.getId());

                employeeGroups.add(employeeGroup);
            }
        }

        employeeGroupDao.insert(employeeGroups);
    }

    private Long getCareTeamRoleId(Map<EmployeeData, EmployeeForeignKeys> foreignKeysMap, ExchangeRoleCodeToCareTeamRoleIdResolver exchangeRoleCodeToCareTeamRoleIdResolver, EmployeeData sourceEmployee) {
        Set<Long> groupIds = foreignKeysMap.get(sourceEmployee).getGroupIds();
        Set<String> exchangeRoleNames = new HashSet<String>();
        if (!CollectionUtils.isEmpty(groupIds)) {
            for (Long groupId: groupIds) {
                exchangeRoleNames.addAll(groupRoleDao.getRoleNames(groupId));
            }
        }
        Pair<Long, Integer> careTeamRoleIdWithAssigningPriority = null;
        if (!CollectionUtils.isEmpty(exchangeRoleNames)) {
            for (String exchangeRoleName : exchangeRoleNames) {
                Pair<Long, Integer> idAndAssigningPriority = exchangeRoleCodeToCareTeamRoleIdResolver.getIdAndAssigningPriority(exchangeRoleName);
                if (idAndAssigningPriority != null) {
                    if (careTeamRoleIdWithAssigningPriority == null || careTeamRoleIdWithAssigningPriority.getValue() > idAndAssigningPriority.getValue()) {
                        careTeamRoleIdWithAssigningPriority = idAndAssigningPriority;
                    }
                }
            }
        }
        return careTeamRoleIdWithAssigningPriority != null ? careTeamRoleIdWithAssigningPriority.getKey() : null;
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<EmployeeData> employees,
                                    Map<EmployeeData, EmployeeForeignKeys> foreignKeysMap, IdMapping<String> idMapping) {
        List<String> legacyIds = Utils.getStringIds(employees);
        Map<String, Employee> legacyIdToEmployeeMapping = employeeDao.getLegacyIdToEmployeeMapping(syncContext.getDatabaseId(), legacyIds);
        IdMapping<String> personsIdMapping = personDao.getIdMapping(syncContext.getDatabase(), PersonType.EMPLOYEE, legacyIds);
        Map<String, PersonName> legacyIdToEmployeeNameMapping = nameDao.getLegacyIdToEmployeeNameMapping(syncContext.getDatabaseId(), legacyIds);
        Map<String, PersonAddress> legacyIdToEmployeeAddressMapping = addressDao.getLegacyIdToEmployeeAddressMapping(syncContext.getDatabaseId(), legacyIds);
        Map<String, PersonTelecom> legacyIdToEmployeeEmailMapping = telecomDao.getLegacyIdToEmployeeTelecomMapping(syncContext.getDatabaseId(), SyncQualifiers.TELECOM_EMAIL, legacyIds);
        Map<String, PersonTelecom> legacyIdToEmployeePhoneMapping = telecomDao.getLegacyIdToEmployeeTelecomMapping(syncContext.getDatabaseId(), SyncQualifiers.TELECOM_PHONE1, legacyIds);

        ExchangeRoleCodeToCareTeamRoleIdResolver exchangeRoleCodeToCareTeamRoleIdResolver = syncContext.getSharedObject(ExchangeRoleCodeToCareTeamRoleIdResolver.class);

        List<EmployeeData> employeesToInsertPersons = new ArrayList<EmployeeData>();
        for (EmployeeData sourceEntity : employees) {
            String legacyId = sourceEntity.getId();

            Long careTeamRoleId = getCareTeamRoleId(foreignKeysMap, exchangeRoleCodeToCareTeamRoleIdResolver, sourceEntity);
            Employee.Updatable updatable = createUpdatable(sourceEntity, careTeamRoleId);
            Employee existingEmployee = legacyIdToEmployeeMapping.get(legacyId);
            long newEmployeeId = existingEmployee.getId();

            if (!employeeEqualToExisting(sourceEntity.getPassword(), updatable, existingEmployee.getUpdatable())) {
                employeeDao.update(updatable, newEmployeeId);
            }

            // update raw FKs
            Set<Long> srcValues = Sets.newHashSet(employeeGroupDao.getGroupIds(newEmployeeId));
            Set<Long> destValues = foreignKeysMap.get(sourceEntity).getGroupIds();

            Set<Long> toAdd = Sets.difference(destValues, srcValues);
            Set<Long> toDelete = Sets.difference(srcValues, destValues);

            if(!toAdd.isEmpty() || !toDelete.isEmpty()) {
                List<EmployeeGroup> groupRoles = new ArrayList<EmployeeGroup>();

                for (Long groupId : toAdd) {
                    EmployeeGroup employeeGroup = new EmployeeGroup();
                    employeeGroup.setDatabaseId(syncContext.getDatabaseId());
                    employeeGroup.setLegacyId(sourceEntity.getId());
                    employeeGroup.setGroupId(groupId);
                    employeeGroup.setEmployeeId(newEmployeeId);

                    groupRoles.add(employeeGroup);
                }

                employeeGroupDao.insert(groupRoles);

                for (Long groupId : toDelete) {
                    employeeGroupDao.delete(syncContext.getDatabase(), newEmployeeId, groupId);
                }
            }

            Long personId = personsIdMapping.getNewId(legacyId);
            if (personId != null) { // remove check after full import and change to getNewIdOrThrowException
                PersonName.Updatable personNameUpdate = nameAssembler.createNameUpdatable(sourceEntity);
                PersonName existingPersonName = legacyIdToEmployeeNameMapping.get(legacyId);
                Long nameId = existingPersonName.getId();
                if (!nameEqualToExisting(personNameUpdate, existingPersonName.getUpdatable())) {
                    nameDao.updateById(personNameUpdate, nameId);
                }

                List<PersonTelecom> telecomsToAdd = new ArrayList<PersonTelecom>();
                PersonTelecom.Updatable emailUpdate = telecomAssembler.createEmailUpdatable(sourceEntity);
                PersonTelecom existingPersonEmail = legacyIdToEmployeeEmailMapping.get(legacyId);
                if (existingPersonEmail != null) {
                    Long emailId = existingPersonEmail.getId();
                    if (!telecomEqualToExisting(emailUpdate, existingPersonEmail.getUpdatable())) {
                        telecomDao.updateById(emailUpdate, emailId);
                    }
                } else {
                    telecomsToAdd.add(telecomAssembler.createEmailTelecom(sourceEntity, personId, syncContext.getDatabase()));
                }

                PersonTelecom.Updatable homePhoneUpdatable = telecomAssembler.createHomePhoneUpdatable(sourceEntity);
                PersonTelecom existingPersonPhone = legacyIdToEmployeePhoneMapping.get(legacyId);
                if (existingPersonPhone != null) {
                    Long phoneId = existingPersonPhone.getId();
                    if (!telecomEqualToExisting(homePhoneUpdatable, existingPersonPhone.getUpdatable())) {
                        telecomDao.updateById(homePhoneUpdatable, phoneId);
                    }
                } else {
                    telecomsToAdd.add(telecomAssembler.createHomePhoneTelecom(sourceEntity, personId, syncContext.getDatabase()));
                }

                if (!CollectionUtils.isEmpty(telecomsToAdd)) {
                    telecomDao.insert(telecomsToAdd);
                }

                List<PersonAddress> addressToAdd = new ArrayList<PersonAddress>();
                PersonAddress.Updatable personAddressUpdate = addressAssembler.createAddressUpdatable(sourceEntity);
                PersonAddress existingPersonAddress = legacyIdToEmployeeAddressMapping.get(legacyId);
                if (existingPersonAddress != null) {
                    Long addressId = existingPersonAddress.getId();
                    if (!addressEqualToExisting(personAddressUpdate, existingPersonAddress.getUpdatable())) {
                        addressDao.updateById(personAddressUpdate, addressId);
                    }
                } else {
                    addressToAdd.add(addressAssembler.createAddress(sourceEntity, personId, syncContext.getDatabase()));
                }

                if (!CollectionUtils.isEmpty(addressToAdd)) {
                    addressDao.insert(addressToAdd);
                }

            } else {
                employeesToInsertPersons.add(sourceEntity);
            }
        }

        // remove after full import
        if (!CollectionUtils.isEmpty(employeesToInsertPersons)) {
            IdMapping<String> insertedPersonsIdMapping = insertPersons(syncContext.getDatabase(), employeesToInsertPersons);
            insertNamesAddressesAndTelecoms(syncContext.getDatabase(), employeesToInsertPersons, insertedPersonsIdMapping);

            for(EmployeeData sourceEntity : employeesToInsertPersons) {
                String legacyId = sourceEntity.getId();
                Long newPersonId = insertedPersonsIdMapping.getNewIdOrThrowException(legacyId);
                long newEmployeeId = idMapping.getNewIdOrThrowException(legacyId);
                employeeDao.updatePersonId(newEmployeeId, newPersonId);
            }
        }
    }

    private boolean telecomEqualToExisting(PersonTelecom.Updatable telecomUpdate, PersonTelecom.Updatable existingUpdatable) {
        return ObjectUtils.nullSafeEquals(existingUpdatable.getValue(), telecomUpdate.getValue());
    }

    private boolean addressEqualToExisting(PersonAddress.Updatable personAddressUpdate, PersonAddress.Updatable existingUpdatable) {
        return ObjectUtils.nullSafeEquals(existingUpdatable.getStreetAddress(), personAddressUpdate.getStreetAddress())
                && ObjectUtils.nullSafeEquals(existingUpdatable.getCity(), personAddressUpdate.getCity())
                && ObjectUtils.nullSafeEquals(existingUpdatable.getState(), personAddressUpdate.getState())
                && ObjectUtils.nullSafeEquals(existingUpdatable.getPostalCode(), personAddressUpdate.getPostalCode());
    }

    private boolean nameEqualToExisting(PersonName.Updatable personNameUpdate, PersonName.Updatable existingUpdatable) {
        return ObjectUtils.nullSafeEquals(existingUpdatable.getGiven(), personNameUpdate.getGiven()) && ObjectUtils.nullSafeEquals(existingUpdatable.getFamily(), personNameUpdate.getFamily());
    }

    private boolean employeeEqualToExisting(String password, Employee.Updatable updatable, Employee.Updatable existingUpdatable) {
        boolean result = existingUpdatable.getFirstName().equals(updatable.getFirstName())
                && existingUpdatable.getLastName().equals(updatable.getLastName())
                && existingUpdatable.getLoginName().equals(updatable.getLoginName())
                && existingUpdatable.isInactive() == updatable.isInactive()
                && ObjectUtils.nullSafeEquals(existingUpdatable.getCareTeamRoleId(), updatable.getCareTeamRoleId());
        if (result) {
            PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
            result = result && passwordEncoder.matches(password, existingUpdatable.getPassword());
        }
        return result;
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext,
                                String legacyIdString) {
        //Employee deletion isn't supported. Employee that have ever had participated in the Exchange
        //can be only deactivated, but they cannot be deleted.
        employeeDao.delete(syncContext.getDatabase(), legacyIdString);

        employeeGroupDao.delete(syncContext.getDatabase(), legacyIdString);

        DatabaseInfo database = syncContext.getDatabase();

        addressDao.delete(database, PersonType.EMPLOYEE, legacyIdString);
        nameDao.delete(database, PersonType.EMPLOYEE, legacyIdString);
        telecomDao.delete(database, PersonType.EMPLOYEE, legacyIdString, SyncQualifiers.TELECOM_EMAIL);
        telecomDao.delete(database, PersonType.EMPLOYEE, legacyIdString, SyncQualifiers.TELECOM_PHONE1);

        personDao.delete(database, PersonType.EMPLOYEE, legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<String> employeeIdMapping = employeeDao.getIdMapping(context.getDatabase(), employeesIdMappingLimit);
        context.putSharedObject(EmployeeIdResolver.class, new EmployeeIdResolver() {
            @Override
            public long getId(String legacyId, DatabaseInfo database) {
                Long id = employeeIdMapping.getNewId(legacyId);
                if (id == null) {
                    id = employeeDao.getId(database, legacyId);
                }
                return id;
            }
        });
    }

    private Employee.Updatable createUpdatable(EmployeeData sourceEmployee, Long careTeamRoleId) {
        Employee.Updatable updatable = new Employee.Updatable();
        updatable.setFirstName(sourceEmployee.getFirstName());
        updatable.setLastName(sourceEmployee.getLastName());
        updatable.setLoginName(sourceEmployee.getLoginName());

        PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
        String password = passwordEncoder.encode(sourceEmployee.getPassword());

        updatable.setPassword(password);
        updatable.setInactive(sourceEmployee.isInactive());
        updatable.setIs4dContact(Boolean.TRUE);
        updatable.setCareTeamRoleId(careTeamRoleId);
        return updatable;
    }

    private IdMapping<String> insertPersons(DatabaseInfo database, List<EmployeeData> sourceEmployees) {
        List<Person> persons = new ArrayList<Person>();
        for (EmployeeData sourceEmployee : sourceEmployees) {
            Person person = new Person();
            person.setDatabaseId(database.getId());
            person.setLegacyTable(PersonType.EMPLOYEE.getTableName());
            person.setLegacyId(sourceEmployee.getId());
            person.setTypeCodeId(null);
            persons.add(person);
        }
        long personLastId = personDao.getLastId();
        personDao.insert(persons);
        return personDao.getIdMapping(database, PersonType.EMPLOYEE, personLastId);
    }

    private void insertNamesAddressesAndTelecoms(DatabaseInfo database, List<EmployeeData> sourceEmployees, IdMapping<String> personsIdMapping) {
        List<PersonName> names = new ArrayList<PersonName>();
        List<PersonAddress> addresses = new ArrayList<PersonAddress>();
        List<PersonTelecom> telecoms = new ArrayList<PersonTelecom>();

        for (EmployeeData sourceEmployee : sourceEmployees) {
            long personId = personsIdMapping.getNewIdOrThrowException(sourceEmployee.getId());

            names.add(nameAssembler.createName(sourceEmployee, personId, database));
            addresses.add(addressAssembler.createAddress(sourceEmployee, personId, database));
            telecoms.add(telecomAssembler.createEmailTelecom(sourceEmployee, personId, database));
            telecoms.add(telecomAssembler.createHomePhoneTelecom(sourceEmployee, personId, database));
        }
        nameDao.insert(names);
        addressDao.insert(addresses);
        telecomDao.insert(telecoms);
    }
}
