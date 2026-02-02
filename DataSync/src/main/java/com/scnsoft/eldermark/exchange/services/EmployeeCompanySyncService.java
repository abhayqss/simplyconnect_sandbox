package com.scnsoft.eldermark.exchange.services;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.EmployeeDao;
import com.scnsoft.eldermark.exchange.dao.target.EmployeeOrganizationDao;
import com.scnsoft.eldermark.exchange.dao.target.EmployeeOrganizationGroupDao;
import com.scnsoft.eldermark.exchange.dao.target.EmployeeOrganizationRoleDao;
import com.scnsoft.eldermark.exchange.fk.EmployeeCompanyForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.EmployeeCompanyData;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.GroupIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.RoleIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EmployeeCompanySyncService extends StandardSyncService<EmployeeCompanyData, Long, EmployeeCompanyForeignKeys> {
    @Autowired
    @Qualifier("employeeCompanySourceDao")
    private StandardSourceDao<EmployeeCompanyData, Long> sourceDao;

    @Autowired
    private EmployeeOrganizationDao employeeOrganizationDao;

    @Autowired
    private EmployeeOrganizationRoleDao employeeOrganizationRoleDao;

    @Autowired
    private EmployeeOrganizationGroupDao employeeOrganizationGroupDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(EmployeeSyncService.class);
        dependencies.add(RoleSyncService.class);
        dependencies.add(GroupSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<EmployeeCompanyData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(EmployeeCompanyData.TABLE_NAME, EmployeeCompanyData.UNIQUE_ID,
                EmployeeCompanyData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return employeeOrganizationDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<EmployeeCompanyForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                             EmployeeCompanyData entity) {
        DatabaseInfo database = syncContext.getDatabase();

        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        RoleIdResolver roleIdResolver = syncContext.getSharedObject(RoleIdResolver.class);
        GroupIdResolver groupIdResolver = syncContext.getSharedObject(GroupIdResolver.class);

        EmployeeCompanyForeignKeys foreignKeys = new EmployeeCompanyForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(EmployeeCompanyData.TABLE_NAME, entity.getId());

        String companyLegacyId = entity.getCompany();

        if (!Utils.isEmpty(companyLegacyId)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(companyLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, companyLegacyId));
            }
        }

        String employeeLegacyId = entity.getEmployeeId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setEmployeeId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }

        String secProcessFeatures = entity.getSecProcessFeatures();

        Set<String> fkRoleSet;
        try {
            fkRoleSet = Sets.newHashSet(secProcessFeatures.split(Constants.CARRIAGE_RETURN_SEPARATOR));
        } catch (RuntimeException e) {
            fkRoleSet = new HashSet<String>();
        }
        for(String fkCandidate : fkRoleSet) {
            try {
                foreignKeys.addEmployeeRoleId(roleIdResolver.getId(fkCandidate));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Role.TABLE_NAME, fkCandidate));
            }
        }

        String secGroupIds = entity.getSecGroupIds();

        Set<String> fkGroupsSet;
        try {
            fkGroupsSet = Sets.newHashSet(secGroupIds.split(Constants.COMMA_SEPARATOR));
        } catch (RuntimeException e) {
            fkGroupsSet = new HashSet<String>();
        }
        for(String fkCandidate : fkGroupsSet) {
            try {
                if(!StringUtils.isEmpty(fkCandidate)) {
                    foreignKeys.addEmployeeGroupId(groupIdResolver.getId(Long.valueOf(fkCandidate), syncContext.getDatabase()));
                }
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Group.TABLE_NAME, fkCandidate));
            } catch (NumberFormatException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Group.TABLE_NAME, fkCandidate));
            }
        }

        return new FKResolveResult<EmployeeCompanyForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<EmployeeCompanyData> sourceEntities,
                                       Map<EmployeeCompanyData, EmployeeCompanyForeignKeys> foreignKeysMap) {
        List<EmployeeOrganization> employeeOrganizations = new ArrayList<EmployeeOrganization>();

        for (EmployeeCompanyData sourceEntity : sourceEntities) {
            EmployeeCompanyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            EmployeeOrganization employeeOrganization = new EmployeeOrganization();
            employeeOrganization.setDatabaseId(syncContext.getDatabaseId());
            employeeOrganization.setLegacyId(sourceEntity.getId());
            employeeOrganization.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            employeeOrganizations.add(employeeOrganization);

            employeeDao.updateCcnCommunityId(foreignKeys.getOrganizationId(), foreignKeys.getEmployeeId());
        }

        long lastIdBeforeInsert = employeeOrganizationDao.getLastId();
        employeeOrganizationDao.insert(employeeOrganizations);
        IdMapping<Long> insertedEntitiesIdMapping = employeeOrganizationDao.getIdMapping(syncContext.getDatabase(), lastIdBeforeInsert);


        List<EmployeeOrganizationRole> employeeOrganizationRoles = new ArrayList<EmployeeOrganizationRole>();

        for (EmployeeCompanyData sourceEntity : sourceEntities) {
            EmployeeCompanyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            for (Long roleId : foreignKeys.getEmployeeRoleIds()) {
                EmployeeOrganizationRole employeeRole = new EmployeeOrganizationRole();
                employeeRole.setEmployeeOrganizationId(insertedEntitiesIdMapping.getNewIdOrThrowException(sourceEntity.getId()));
                employeeRole.setRoleId(roleId);
                employeeRole.setLegacyId(sourceEntity.getId());
                employeeRole.setDatabaseId(syncContext.getDatabaseId());

                employeeOrganizationRoles.add(employeeRole);
            }
        }

        employeeOrganizationRoleDao.insert(employeeOrganizationRoles);


        List<EmployeeOrganizationGroup> employeeGroups = new ArrayList<EmployeeOrganizationGroup>();

        for (EmployeeCompanyData sourceEntity : sourceEntities) {
            EmployeeCompanyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            for (Long groupId : foreignKeys.getEmployeeGroupIds()) {
                EmployeeOrganizationGroup employeeGroup = new EmployeeOrganizationGroup();
                employeeGroup.setDatabaseId(syncContext.getDatabaseId());
                employeeGroup.setEmployeeOrganizationId(insertedEntitiesIdMapping.getNewIdOrThrowException(sourceEntity.getId()));
                employeeGroup.setGroupId(groupId);
                employeeGroup.setLegacyId(sourceEntity.getId());

                employeeGroups.add(employeeGroup);
            }
        }

        employeeOrganizationGroupDao.insert(employeeGroups);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<EmployeeCompanyData> sourceEntities,
                                    Map<EmployeeCompanyData, EmployeeCompanyForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (EmployeeCompanyData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            EmployeeCompanyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            employeeDao.updateCcnCommunityId(foreignKeys.getOrganizationId(), foreignKeys.getEmployeeId());

            employeeOrganizationDao.update(createUpdatable(sourceEntity, foreignKeys), id);

            // update Roles raw FKs
            Set<Long> srcValues = Sets.newHashSet(employeeOrganizationRoleDao.getRoleIds(id));
            Set<Long> destValues = foreignKeys.getEmployeeRoleIds();

            Set<Long> toAdd = Sets.difference(destValues, srcValues);
            Set<Long> toDelete = Sets.difference(srcValues, destValues);

            if(!toAdd.isEmpty() || !toDelete.isEmpty()) {
                List<EmployeeOrganizationRole> employeeOrganizationRoles = new ArrayList<EmployeeOrganizationRole>();
                for (Long roleId : toAdd) {
                    EmployeeOrganizationRole employeeRole = new EmployeeOrganizationRole();
                    employeeRole.setEmployeeOrganizationId(id);
                    employeeRole.setRoleId(roleId);
                    employeeRole.setLegacyId(sourceEntity.getId());
                    employeeRole.setDatabaseId(syncContext.getDatabaseId());
                    employeeOrganizationRoles.add(employeeRole);
                }
                employeeOrganizationRoleDao.insert(employeeOrganizationRoles);

                for (Long roleId : toDelete) {
                    employeeOrganizationRoleDao.delete(syncContext.getDatabase(), id, roleId);
                }
            }

            // update Groups raw FKs
            Set<Long> srcGroupValues = Sets.newHashSet(employeeOrganizationGroupDao.getGroupIds(id));
            Set<Long> destGroupValues = foreignKeysMap.get(sourceEntity).getEmployeeGroupIds();

            Set<Long> groupsToAdd = Sets.difference(destGroupValues, srcGroupValues);
            Set<Long> groupsToDelete = Sets.difference(srcGroupValues, destGroupValues);

            if(!groupsToAdd.isEmpty() || !groupsToDelete.isEmpty()) {
                List<EmployeeOrganizationGroup> employeeGroups = new ArrayList<EmployeeOrganizationGroup>();

                for (Long groupId : groupsToAdd) {
                    EmployeeOrganizationGroup employeeGroup = new EmployeeOrganizationGroup();
                    employeeGroup.setDatabaseId(syncContext.getDatabaseId());
                    employeeGroup.setLegacyId(sourceEntity.getId());
                    employeeGroup.setGroupId(groupId);
                    employeeGroup.setEmployeeOrganizationId(id);

                    employeeGroups.add(employeeGroup);
                }

                employeeOrganizationGroupDao.insert(employeeGroups);

                for (Long groupId : groupsToDelete) {
                    employeeOrganizationGroupDao.delete(syncContext.getDatabase(), id, groupId);
                }
            }
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        employeeOrganizationRoleDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
        employeeOrganizationGroupDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
        employeeOrganizationDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private EmployeeOrganization.Updatable createUpdatable(EmployeeCompanyData sourceEntity,
                                                           EmployeeCompanyForeignKeys foreignKeys) {
        EmployeeOrganization.Updatable updatable = new EmployeeOrganization.Updatable();
        updatable.setAccessMarketing(sourceEntity.getAccessMarketing());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        updatable.setEmployeeId(foreignKeys.getEmployeeId());
        return updatable;
    }
}
