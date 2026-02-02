package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.CareTeamRoleDao;
import com.scnsoft.eldermark.exchange.dao.target.RoleDao;
import com.scnsoft.eldermark.exchange.model.target.CareTeamRoleWithAssigningPriority;
import com.scnsoft.eldermark.exchange.model.target.Role;
import com.scnsoft.eldermark.exchange.resolvers.ExchangeRoleCodeToCareTeamRoleIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.RoleIdResolver;
import com.scnsoft.eldermark.framework.IdMapping;
import com.scnsoft.eldermark.framework.Pair;
import com.scnsoft.eldermark.framework.PerformanceStatisticsHolder;
import com.scnsoft.eldermark.framework.SyncService;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleSyncService implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(RoleSyncService.class);

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    private static Map<String, String> roles4DExchange;

    private static Map<String, CareTeamRoleWithAssigningPriority> rolesExchangeSimplyConnect;

    public RoleSyncService() {
        roles4DExchange = new HashMap<String, String>(2);
        roles4DExchange.put("SimplyConnect system", Role.ROLE_ELDERMARK_USER);
        roles4DExchange.put("SimplyConnect system Audit Report", Role.ROLE_MANAGER);
        roles4DExchange.put("SimplyConnect system Direct Manager", Role.ROLE_DIRECT_MANAGER);
        roles4DExchange.put("SimplyConnect system Super Manager", Role.ROLE_SUPER_MANAGER);
        roles4DExchange.put("Cloud Storage", Role.ROLE_CLOUD_STORAGE_USER);
        roles4DExchange.put("Cloud Storage Category Manager", Role.ROLE_CLOUD_STORAGE_CATEGORY_MANAGER);
        roles4DExchange.put("FilEHR", Role.ROLE_CLOUD_STORAGE_USER);
        roles4DExchange.put("Cloud Storage Super Admin", Role.ROLE_CLOUD_STORAGE_SUPER_ADMIN);
        roles4DExchange.put("Simply Connect Administrator", Role.ROLE_SC_ADMINISTRATOR);
        roles4DExchange.put("Simply Connect Behavioral Health", Role.ROLE_SC_BEHAVIORAL_HEALTH);
        roles4DExchange.put("Simply Connect Care Coordinator", Role.ROLE_SC_CARE_COORDINATOR);
        roles4DExchange.put("Simply Connect Case Manager", Role.ROLE_SC_CASE_MANAGER);
        roles4DExchange.put("Simply Connect Community Administrator", Role.ROLE_SC_COMMUNITY_ADMINISTRATOR);
        roles4DExchange.put("Simply Connect Community Members", Role.ROLE_SC_COMMUNITY_MEMBERS);
        roles4DExchange.put("Simply Connect Primary Physician", Role.ROLE_SC_PRIMARY_PHYSICIAN);
        roles4DExchange.put("Simply Connect Service Providers", Role.ROLE_SC_SERVICE_PROVIDERS);
        roles4DExchange.put("Simply Connect Super Administrator", Role.ROLE_SC_SUPER_ADMINISTRATOR);

        rolesExchangeSimplyConnect = new HashMap<String, CareTeamRoleWithAssigningPriority>();
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_ADMINISTRATOR, CareTeamRoleWithAssigningPriority.ROLE_SC_ADMINISTRATOR);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_BEHAVIORAL_HEALTH, CareTeamRoleWithAssigningPriority.ROLE_SC_BEHAVIORAL_HEALTH);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_CARE_COORDINATOR, CareTeamRoleWithAssigningPriority.ROLE_SC_CARE_COORDINATOR);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_CASE_MANAGER, CareTeamRoleWithAssigningPriority.ROLE_SC_CASE_MANAGER);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_COMMUNITY_ADMINISTRATOR, CareTeamRoleWithAssigningPriority.ROLE_SC_COMMUNITY_ADMINISTRATOR);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_COMMUNITY_MEMBERS, CareTeamRoleWithAssigningPriority.ROLE_SC_COMMUNITY_MEMBERS);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_PRIMARY_PHYSICIAN, CareTeamRoleWithAssigningPriority.ROLE_SC_PRIMARY_PHYSICIAN);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_SERVICE_PROVIDERS, CareTeamRoleWithAssigningPriority.ROLE_SC_SERVICE_PROVIDERS);
        rolesExchangeSimplyConnect.put(Role.ROLE_SC_SUPER_ADMINISTRATOR, CareTeamRoleWithAssigningPriority.ROLE_SC_ADMINISTRATOR);
    }

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<String> roleIdMapping = roleDao.getIdMapping();
        context.putSharedObject(RoleIdResolver.class, new RoleIdResolver() {
            @Override
            public Long getId(String role4D) {
                if (!roles4DExchange.containsKey(role4D))
                    return null;

                String roleExchange = roles4DExchange.get(role4D);
                return roleIdMapping.getNewIdOrThrowException(roleExchange);
            }
        });
        final IdMapping<String> careTeamRoleIdMapping = careTeamRoleDao.getIdMapping();
        context.putSharedObject(ExchangeRoleCodeToCareTeamRoleIdResolver.class, new ExchangeRoleCodeToCareTeamRoleIdResolver() {
            @Override
            public Pair<Long, Integer> getIdAndAssigningPriority(String roleExchange) {
                if (!rolesExchangeSimplyConnect.containsKey(roleExchange))
                    return null;

                CareTeamRoleWithAssigningPriority careTeamRoleWithAssigningPriority = rolesExchangeSimplyConnect.get(roleExchange);
                String roleSc = careTeamRoleWithAssigningPriority.getCode();
                return new Pair<Long, Integer>(careTeamRoleIdMapping.getNewIdOrThrowException(roleSc), careTeamRoleWithAssigningPriority.getAssigningPriority());
            }
        });
    }

    @Override
    public void syncNewAndUpdated(DatabaseSyncContext syncContext,
                                  final PerformanceStatisticsHolder performanceStatisticsHolder) {
        // Roles are dictionary table at Exchange
    }

    @Override
    public DeletionRelatedOperations getDeletionRelatedOperations() {
        // Roles are dictionary table at Exchange
        return null;
    }
}
