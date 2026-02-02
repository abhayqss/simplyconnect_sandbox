package com.scnsoft.eldermark.exchange.services;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.GroupDao;
import com.scnsoft.eldermark.exchange.dao.target.GroupRoleDao;
import com.scnsoft.eldermark.exchange.fk.GroupRoleForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.SecurityGroupData;
import com.scnsoft.eldermark.exchange.model.target.Group;
import com.scnsoft.eldermark.exchange.model.target.GroupRole;
import com.scnsoft.eldermark.exchange.model.target.Role;
import com.scnsoft.eldermark.exchange.resolvers.GroupIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.RoleIdResolver;
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
import java.util.Set;

@Service
public class GroupSyncService extends StandardSyncService<SecurityGroupData, Long, GroupRoleForeignKeys> {
    @Autowired
    @Qualifier("securityGroupSourceDao")
    private StandardSourceDao<SecurityGroupData, Long> sourceDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupRoleDao groupRoleDao;

    @Value("${groups.idmapping.cache.size}")
    private int groupsIdMappingLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(RoleSyncService.class);
        return dependencies;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(SecurityGroupData.TABLE_NAME, SecurityGroupData.ID, SecurityGroupData.class);
    }

    @Override
    protected StandardSourceDao<SecurityGroupData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected FKResolveResult<GroupRoleForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, SecurityGroupData entity) {
        RoleIdResolver roleIdResolver = syncContext.getSharedObject(RoleIdResolver.class);

        GroupRoleForeignKeys foreignKeys = new GroupRoleForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(SecurityGroupData.TABLE_NAME, entity.getId());
        Set<String> fkCandidateSet;
        String secProcessFeatures = entity.getSecProcessFeatures();
        try {
            fkCandidateSet = Sets.newHashSet(secProcessFeatures.split(Constants.CARRIAGE_RETURN_SEPARATOR));
        } catch (RuntimeException e) {
            fkCandidateSet = Sets.newHashSet();
        }
        for(String fkCandidate : fkCandidateSet) {
            try {
                foreignKeys.addRoleId(roleIdResolver.getId(fkCandidate));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(Role.TABLE_NAME, fkCandidate));
            }
        }

        return new FKResolveResult<GroupRoleForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return groupDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<SecurityGroupData> sourceEntities, Map<SecurityGroupData, GroupRoleForeignKeys> foreignKeysMap) {
        DatabaseInfo databaseInfo = syncContext.getDatabase();

        List<Group> groups = new ArrayList<Group>();
        for (SecurityGroupData sourceEntity : sourceEntities) {
            Group group = new Group();
            group.setDatabaseId(databaseInfo.getId());
            group.setLegacyId(sourceEntity.getId());

            groups.add(group);
        }

        long lastGroupIdBeforeInsert = groupDao.getLastId();
        groupDao.insert(groups);
        IdMapping<Long> insertedGroupsIdMapping = groupDao.getIdMapping(databaseInfo, lastGroupIdBeforeInsert);


        List<GroupRole> groupRoles = new ArrayList<GroupRole>();

        for (SecurityGroupData sourceEntity : sourceEntities) {
            GroupRoleForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            for (Long roleId : foreignKeys.getRoleIds()) {
                GroupRole groupRole = new GroupRole();
                groupRole.setDatabaseId(databaseInfo.getId());
                groupRole.setLegacyId(sourceEntity.getId());
                groupRole.setGroupId(insertedGroupsIdMapping.getNewIdOrThrowException(sourceEntity.getId()));
                groupRole.setRoleId(roleId);

                groupRoles.add(groupRole);
            }
        }

        groupRoleDao.insert(groupRoles);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<SecurityGroupData> sourceEntities, Map<SecurityGroupData, GroupRoleForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
        DatabaseInfo databaseInfo = syncContext.getDatabase();

        for (SecurityGroupData sourceEntity : sourceEntities) {
            Long legacyId = sourceEntity.getId();
            long groupId = idMapping.getNewIdOrThrowException(legacyId);

            // update raw FKs
            Set<Long> srcValues = Sets.newHashSet(groupRoleDao.getRoleIds(groupId));
            Set<Long> destValues = foreignKeysMap.get(sourceEntity).getRoleIds();

            Set<Long> toAdd = Sets.difference(destValues, srcValues);
            Set<Long> toDelete = Sets.difference(srcValues, destValues);

            if(!toAdd.isEmpty() || !toDelete.isEmpty()) {
                List<GroupRole> groupRoles = new ArrayList<GroupRole>();

                for (Long roleId : toAdd) {
                    GroupRole groupRole = new GroupRole();
                    groupRole.setDatabaseId(databaseInfo.getId());
                    groupRole.setLegacyId(sourceEntity.getId());
                    groupRole.setGroupId(groupId);
                    groupRole.setRoleId(roleId);

                    groupRoles.add(groupRole);
                }

                groupRoleDao.insert(groupRoles);

                for (Long roleId : toDelete) {
                    groupRoleDao.delete(databaseInfo, groupId, roleId);
                }
            }
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        Long groupLegacyId = Long.valueOf(legacyIdString);

        groupRoleDao.delete(syncContext.getDatabase(), groupLegacyId);
        groupDao.delete(syncContext.getDatabase(), groupLegacyId);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> groupIdMapping = groupDao.getIdMapping(context.getDatabase(), groupsIdMappingLimit);
        context.putSharedObject(GroupIdResolver.class, new GroupIdResolver() {
            @Override
            public long getId(Long legacyId, DatabaseInfo database) {
                Long id = groupIdMapping.getNewId(legacyId);
                if (id == null) {
                    id = groupDao.getId(database, legacyId);
                }
                return id;
            }
        });
    }
}
