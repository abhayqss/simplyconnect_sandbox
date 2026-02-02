package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.security.ChatSecurityServiceImpl;
import com.scnsoft.eldermark.service.security.VideoCallSecurityServiceImpl;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeePredicateGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private FullCareTeamPredicateGenerator fullCareTeamPredicateGenerator;

    @Autowired
    private OrganizationPredicateGenerator organizationPredicateGenerator;

    @Autowired
    private ChatPredicateGenerator chatPredicateGenerator;

    public <T extends BaseEmployeeSecurityEntity> Predicate hasAccess(PermissionFilter permissionFilter, From<?, T> root, CriteriaBuilder criteriaBuilder) {
        var communityJoin = JpaUtils.getOrCreateJoin(root, BaseEmployeeSecurityEntity_.community, JoinType.LEFT, false);
        var eligible = criteriaBuilder.or(
                communityJoin.isNull(),
                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin)
        );

        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return eligible;
        }

        List<Predicate> predicates = new ArrayList<>();
        if (permissionFilter.hasPermission(Permission.CONTACT_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.CONTACT_VIEW_IF_ASSOCIATED_ORGANIZATION);
            var employeeOrganizations = employees.stream().map(Employee::getOrganizationId)
                    .collect(Collectors.toList());

            predicates.add(criteriaBuilder.in(root.get(BaseEmployeeSecurityEntity_.ORGANIZATION_ID)).value(employeeOrganizations));
        }

        if (permissionFilter.hasPermission(Permission.CONTACT_VIEW_IF_CREATED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(Permission.CONTACT_VIEW_IF_CREATED_BY_SELF);
            predicates.add(criteriaBuilder.in(root.get(BaseEmployeeSecurityEntity_.CREATOR)).value(employees));
        }

        if (permissionFilter.hasPermission(Permission.CONTACT_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(Permission.CONTACT_VIEW_IF_SELF_RECORD);
            var employeeIds = CareCoordinationUtils.toIdsSet(employees);
            predicates.add(root.get(BaseEmployeeSecurityEntity_.id).in(employeeIds));
        }

        return criteriaBuilder.and(
                eligible,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }

    public Predicate hasChatAccess(PermissionFilter permissionFilter, From<?, Employee> root,
                                   AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                withEnabledChat(root, criteriaBuilder),
                hasConversationAccess(permissionFilter, root, query, criteriaBuilder,
                        Permission.CHAT_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                        Permission.CHAT_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT)
        );
    }

    public Predicate hasChatAccessByPermissionsOnly(PermissionFilter permissionFilter, From<?, Employee> root,
                                                    AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return hasConversationAccessByPermissionsOnly(permissionFilter, root, query, criteriaBuilder,
                Permission.CHAT_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_CLIENT_CONTACT, Permission.CHAT_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_CLIENT_CONTACT,
                Permission.CHAT_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT);
    }

    public Predicate hasVideoCallAccess(PermissionFilter permissionFilter, From<?, Employee> root,
                                        AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                withEnabledVideoCall(root, criteriaBuilder),
                hasConversationAccess(permissionFilter, root, query, criteriaBuilder,
                        Permission.VIDEO_CALL_ADD_CONTACT_ALL_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.VIDEO_CALL_ADD_CONTACT_IF_ASSOCIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT, Permission.VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_ORGANIZATION_EXCEPT_OPT_OUT_CLIENT_CONTACT, Permission.VIDEO_CALL_ADD_CONTACT_IF_FROM_PRIMARY_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.VIDEO_CALL_ADD_CONTACT_IF_FROM_AFFILIATED_COMMUNITY_EXCEPT_OPT_OUT_CLIENT_CONTACT, Permission.VIDEO_CALL_ADD_CONTACT_IF_CREATED_BY_SELF_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.VIDEO_CALL_ADD_CONTACT_IF_SHARE_CURRENT_RP_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT, Permission.VIDEO_CALL_ADD_CONTACT_IF_SELF_RECORD_CURRENT_RP_CLIENT_CTM_EXCEPT_OPT_OUT_CLIENT_CONTACT,
                        Permission.VIDEO_CALL_ADD_CONTACT_IF_ACCESSIBLE_CLIENT_ASSOCIATED_CONTACT)
        );
    }

    private Predicate hasConversationAccess(PermissionFilter permissionFilter, From<?, Employee> root,
                                            AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                            Permission conversationAddContactAllExceptOptOutClient,
                                            Permission conversationAddContactIfAssociatedOrganizationExceptOptOutClient,
                                            Permission conversationAddContactIfFromPrimaryOrganizationExceptOptOutClient,
                                            Permission conversationAddContactIfFromAffiliatedOrganizationExceptOptOutClient,
                                            Permission conversationAddContactIfFromPrimaryCommunityExceptOptOutClient,
                                            Permission conversationAddContactIfFromAffiliatedCommunityExceptOptOutClient,
                                            Permission conversationAddContactIfCreatedBySelfExceptOptOutClient,
                                            Permission conversationAddContactIfShareCurrentRpCtmExceptOptOutClient,
                                            Permission conversationAddContactIfSelfContactRpCurrentClientCtmExceptOptOutClient,
                                            Permission conversationAddContactIfAccessibleClientAssociatedContact) {
        var communityJoin = JpaUtils.getOrCreateJoin(root, Employee_.community, JoinType.LEFT);
        var eligible = criteriaBuilder.or(
                criteriaBuilder.isNull(communityJoin),
                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, communityJoin)
        );


        return criteriaBuilder.and(
                hasConversationAccessByPermissionsOnly(permissionFilter, root, query, criteriaBuilder,
                        conversationAddContactAllExceptOptOutClient,
                        conversationAddContactIfAssociatedOrganizationExceptOptOutClient,
                        conversationAddContactIfFromPrimaryOrganizationExceptOptOutClient,
                        conversationAddContactIfFromAffiliatedOrganizationExceptOptOutClient,
                        conversationAddContactIfFromPrimaryCommunityExceptOptOutClient,
                        conversationAddContactIfFromAffiliatedCommunityExceptOptOutClient,
                        conversationAddContactIfCreatedBySelfExceptOptOutClient,
                        conversationAddContactIfShareCurrentRpCtmExceptOptOutClient,
                        conversationAddContactIfSelfContactRpCurrentClientCtmExceptOptOutClient,
                        conversationAddContactIfAccessibleClientAssociatedContact),
                eligible
        );
    }

    private Predicate hasConversationAccessByPermissionsOnly(PermissionFilter permissionFilter, From<?, Employee> root,
                                                             AbstractQuery<?> query, CriteriaBuilder criteriaBuilder,
                                                             Permission conversationAddContactAllExceptOptOutClient,
                                                             Permission conversationAddContactIfAssociatedOrganizationExceptOptOutClient,
                                                             Permission conversationAddContactIfFromPrimaryOrganizationExceptOptOutClient,
                                                             Permission conversationAddContactIfFromAffiliatedOrganizationExceptOptOutClient,
                                                             Permission conversationAddContactIfFromPrimaryCommunityExceptOptOutClient,
                                                             Permission conversationAddContactIfFromAffiliatedCommunityExceptOptOutClient,
                                                             Permission conversationAddContactIfCreatedBySelfExceptOptOutClient,
                                                             Permission conversationAddContactIfShareCurrentRpCtmExceptOptOutClient,
                                                             Permission conversationAddContactIfSelfContactRpCurrentClientCtmExceptOptOutClient,
                                                             Permission conversationAddContactIfAccessibleClientAssociatedContact) {

        if (permissionFilter.hasPermission(conversationAddContactAllExceptOptOutClient)) {
            return allExceptOptOutClientContact(root.get(Employee_.id), query);
        }

        var exceptOutOutClientPredicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(conversationAddContactIfAssociatedOrganizationExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfAssociatedOrganizationExceptOptOutClient);
            var employeeOrganizations = CareCoordinationUtils.getOrganizationIdsSet(employees);

            exceptOutOutClientPredicates.add(SpecificationUtils.in(criteriaBuilder, root.get(Employee_.organizationId), employeeOrganizations));
        }

        var affiliatedSubQueryLazy = Lazy.of(() -> query.subquery(Long.class));
        var affiliatedRootLazy = Lazy.of(() -> affiliatedSubQueryLazy.get().from(AffiliatedRelationship.class));

        var subQueryPredicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(conversationAddContactIfFromPrimaryOrganizationExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfFromPrimaryOrganizationExceptOptOutClient);

            subQueryPredicates.add(securityPredicateGenerator.affiliatedCommunitiesOfOrganizations(
                    criteriaBuilder,
                    affiliatedRootLazy.get(),
                    employees
            ));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfFromAffiliatedOrganizationExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfFromAffiliatedOrganizationExceptOptOutClient);

            subQueryPredicates.add(securityPredicateGenerator.primaryCommunitiesOfOrganizations(
                    criteriaBuilder,
                    affiliatedRootLazy.get(),
                    employees
            ));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfFromPrimaryCommunityExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfFromPrimaryCommunityExceptOptOutClient);
            subQueryPredicates.add(securityPredicateGenerator.affiliatedCommunities(
                    criteriaBuilder,
                    affiliatedRootLazy.get(),
                    employees
            ));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfFromAffiliatedCommunityExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfFromAffiliatedCommunityExceptOptOutClient);
            subQueryPredicates.add(securityPredicateGenerator.primaryCommunities(
                    criteriaBuilder,
                    affiliatedRootLazy.get(),
                    employees
            ));
        }

        if (!subQueryPredicates.isEmpty()) {
            var affiliatedSubQuery = affiliatedSubQueryLazy.get();
            var affiliatedRoot = affiliatedRootLazy.get();

            affiliatedSubQuery.select(affiliatedRoot.get(AffiliatedRelationship_.affiliatedCommunityId));
            affiliatedSubQuery.where(criteriaBuilder.or(subQueryPredicates.toArray(Predicate[]::new)));

            exceptOutOutClientPredicates.add(criteriaBuilder.in(root.get(Employee_.communityId)).value(affiliatedSubQuery));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfCreatedBySelfExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfCreatedBySelfExceptOptOutClient);
            var ids = CareCoordinationUtils.toIdsSet(employees);
            exceptOutOutClientPredicates.add(SpecificationUtils.in(criteriaBuilder, root.get(Employee_.creatorId), ids));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfShareCurrentRpCtmExceptOptOutClient)) {
            var employeeIds = CareCoordinationUtils.toIdsSet(permissionFilter.getEmployees(conversationAddContactIfShareCurrentRpCtmExceptOptOutClient));
            exceptOutOutClientPredicates.add(fullCareTeamPredicateGenerator.fromSameCareTeam(employeeIds, root.get(Employee_.id),
                    query, criteriaBuilder, HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)));
        }

        if (permissionFilter.hasPermission(conversationAddContactIfSelfContactRpCurrentClientCtmExceptOptOutClient)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfSelfContactRpCurrentClientCtmExceptOptOutClient);
            exceptOutOutClientPredicates.add(securityPredicateGenerator.selfRecordClientCareTeamMember(root, query, criteriaBuilder,
                    employees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))
            );
        }

        var predicates = new ArrayList<Predicate>();
        predicates.add(criteriaBuilder.and(
                allExceptOptOutClientContact(root.get(Employee_.id), query),
                criteriaBuilder.or(exceptOutOutClientPredicates.toArray(new Predicate[0]))
        ));

        if (permissionFilter.hasPermission(conversationAddContactIfAccessibleClientAssociatedContact)) {
            var employees = permissionFilter.getEmployees(conversationAddContactIfAccessibleClientAssociatedContact);

            var associatedSubQuery = query.subquery(Long.class);
            var associatedClientRoot = associatedSubQuery.from(Client.class);
            associatedSubQuery = associatedSubQuery.select(associatedClientRoot.join(Client_.associatedEmployeeIds))
                    .where(clientPredicateGenerator.hasDetailsAccess(
                            PermissionFilterUtils.filterWithEmployeesOnly(permissionFilter, employees),
                            associatedClientRoot,
                            associatedSubQuery, criteriaBuilder));
            predicates.add(root.get(Employee_.id).in(associatedSubQuery));
        }

        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }


    public Predicate isActive(From<?, Employee> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(Employee_.status), EmployeeStatus.ACTIVE);
    }

    public Predicate excludeEmployeeId(Long excludedEmployeeId, From<?, Employee> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(root.get(Employee_.id), excludedEmployeeId);
    }

    public Predicate withEnabledChat(From<?, Employee> root, CriteriaBuilder criteriaBuilder) {
        var role = JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.code);
        var organization = JpaUtils.getOrCreateJoin(root, Employee_.organization);
        return criteriaBuilder.and(
                organizationPredicateGenerator.withEnabledChat(criteriaBuilder, organization, true),
                role.in(CareTeamRolePermissionMapping.findCareTeamRoleCodesWithAnyPermission(ChatSecurityServiceImpl.CHAT_PERMISSIONS))
        );
    }

    public Predicate withEnabledVideoCall(From<?, Employee> root, CriteriaBuilder criteriaBuilder) {
        var role = JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.code);
        var organization = JpaUtils.getOrCreateJoin(root, Employee_.organization);
        return criteriaBuilder.and(
                organizationPredicateGenerator.withEnabledVideoCall(criteriaBuilder, organization, true),
                role.in(CareTeamRolePermissionMapping.findCareTeamRoleCodesWithAnyPermission(VideoCallSecurityServiceImpl.VIDEO_CALL_ADD_CONTACT_PERMISSIONS))
        );
    }

    public Predicate chatAccessibleEmployeesByOrganizationIds(PermissionFilter permissionFilter, Long excludedEmployeeId,
                                                              List<Long> accessibleOrganizationIds, From<?, Employee> root,
                                                              AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                hasChatAccess(permissionFilter, root, query, criteriaBuilder),
                activeEmployeesByOrganizationIdsWithExcludedEmployeeId(excludedEmployeeId, accessibleOrganizationIds, root, criteriaBuilder)
        );
    }

    public Predicate videoCallAccessibleEmployeesByOrganizationIds(
            PermissionFilter permissionFilter,
            Long excludedEmployeeId,
            List<Long> accessibleOrganizationIds,
            From<?, Employee> root,
            AbstractQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        return criteriaBuilder.and(
                hasVideoCallAccess(permissionFilter, root, query, criteriaBuilder),
                activeEmployeesByOrganizationIdsWithExcludedEmployeeId(excludedEmployeeId, accessibleOrganizationIds, root, criteriaBuilder)
        );
    }

    private Predicate activeEmployeesByOrganizationIdsWithExcludedEmployeeId(
            Long excludedEmployeeId,
            List<Long> accessibleOrganizationIds,
            From<?, Employee> root,
            CriteriaBuilder criteriaBuilder
    ) {
        var isActive = isActive(root, criteriaBuilder);

        var byOrganizationIds = accessibleOrganizationIds == null
                ? criteriaBuilder.and()
                : byOrganizationIds(accessibleOrganizationIds, root, criteriaBuilder);

        var exclude = excludedEmployeeId == null
                ? criteriaBuilder.and()
                : excludeEmployeeId(excludedEmployeeId, root, criteriaBuilder);

        return criteriaBuilder.and(byOrganizationIds, isActive, exclude);
    }

    private Predicate byOrganizationIds(List<Long> organizationIds, From<?, Employee> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.in(root.get(Employee_.ORGANIZATION_ID)).value(organizationIds);
    }

    public Predicate excludeParticipatingInOneToOneChatWithAny(Collection<Long> employeeIds, Path<Long> employeeId,
                                                               AbstractQuery<?> query, CriteriaBuilder criteriaBuilder) {
        //todo - exclude only for current, not for all linked employees
        return criteriaBuilder.not(chatPredicateGenerator.usersParticipatingInPersonalWithAny(
                ConversationUtils.employeeIdsToIdentity(employeeIds),
                ConversationUtils.employeeIdToIdentity(employeeId, criteriaBuilder),
                query, criteriaBuilder
        ));
    }

    private Predicate allExceptOptOutClientContact(Path<Long> employeeIdPath,
                                                   AbstractQuery<?> query) {
        var sub = query.subquery(Long.class);
        var subEmployee = sub.from(EmployeeWithAssociatedAllOptOutClients.class);
        sub.select(subEmployee.get(EmployeeWithAssociatedAllOptOutClients_.employeeId));
        return employeeIdPath.in(sub).not();
    }
}
