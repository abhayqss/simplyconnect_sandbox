package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@Component
public class EmployeeSpecificationGenerator {

    @Autowired
    private CommunityPredicateGenerator communityPredicateGenerator;

    @Autowired
    private EmployeePredicateGenerator employeePredicateGenerator;

    @Autowired
    private CommunityCareTeamMemberPredicateGenerator communityCareTeamMemberPredicateGenerator;

    @Autowired
    private ClientAppointmentSpecificationGenerator clientAppointmentSpecificationGenerator;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCareTeamMemberPredicateGenerator;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCareTeamMemberSpecificationGenerator;

    public <T extends BaseEmployeeSecurityEntity> Specification<T> byFilter(ContactFilter filter, Class<T> entityClass) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getOrganizationId() != null)
                predicates.add(criteriaBuilder.equal(root.get(BaseEmployeeSecurityEntity_.organizationId), filter.getOrganizationId()));

            if (StringUtils.isNotBlank(filter.getFirstName()))
                predicates.add(
                        criteriaBuilder.like(root.get(BaseEmployeeSecurityEntity_.firstName), SpecificationUtils.wrapWithWildcards(filter.getFirstName())));

            if (StringUtils.isNotBlank(filter.getLastName()))
                predicates.add(
                        criteriaBuilder.like(root.get(BaseEmployeeSecurityEntity_.lastName), SpecificationUtils.wrapWithWildcards(filter.getLastName())));

            if (StringUtils.isNotBlank(filter.getEmail()))
                predicates.add(
                        criteriaBuilder.like(root.get(BaseEmployeeSecurityEntity_.loginName), SpecificationUtils.wrapWithWildcards(filter.getEmail())));

            var systemRoleIdPath = root.join(BaseEmployeeSecurityEntity_.careTeamRole, JoinType.LEFT).get(CareTeamRole_.id);

            if (CollectionUtils.isNotEmpty(filter.getSystemRoleIds())) {
                if (BooleanUtils.isTrue(filter.getIncludeWithoutSystemRole())) {
                    predicates.add(criteriaBuilder.or(
                            systemRoleIdPath.isNull(),
                            systemRoleIdPath.in(filter.getSystemRoleIds())
                    ));
                } else {
                    predicates.add(systemRoleIdPath.in(filter.getSystemRoleIds()));
                }
            } else {
                if (BooleanUtils.isNotTrue(filter.getIncludeWithoutSystemRole())) {
                    predicates.add(systemRoleIdPath.isNotNull());
                }
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(criteriaBuilder.in(root.get(BaseEmployeeSecurityEntity_.STATUS)).value(filter.getStatuses()));
            }

            predicates.add(criteriaBuilder.notEqual(root.get(BaseEmployeeSecurityEntity_.status), EmployeeStatus.DECLINED));

            var communityIdPath = root.get(BaseEmployeeSecurityEntity_.communityId);

            if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
                var byCommunityIds = communityIdPath.in(filter.getCommunityIds());

                if (BooleanUtils.isTrue(filter.getExcludeWithoutCommunity())) {
                    predicates.add(byCommunityIds);
                } else {
                    predicates.add(criteriaBuilder.or(
                            communityIdPath.isNull(),
                            byCommunityIds
                    ));
                }
            } else {
                if (BooleanUtils.isTrue(filter.getExcludeWithoutCommunity())) {
                    predicates.add(criteriaBuilder.and(
                            communityIdPath.isNotNull()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <T extends BaseEmployeeSecurityEntity> Specification<T> hasAccess(PermissionFilter permissionFilter, Class<T> entityClass) {
        return (root, criteriaQuery, criteriaBuilder) -> employeePredicateGenerator.hasAccess(permissionFilter, root, criteriaBuilder);
    }

    public Specification<Employee> byCommunityId(Long communityId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Employee_.communityId), communityId);
    }

    public Specification<Employee> byCommunityIds(Collection<Long> communityIds) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(Employee_.communityId).in(communityIds);
    }

    public Specification<Employee> bySystemRole(CareTeamRoleCode role) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.code), role);
    }

    public Specification<Employee> bySystemRoleIn(Collection<CareTeamRoleCode> roles) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.CODE)).value(roles);
    }

    public Specification<Employee> byId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Employee_.id), id);
    }

    public Specification<Employee> byIdIn(List<Long> ids) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(Employee_.id).in(ids);
    }

    public Specification<Employee> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Employee_.organizationId), organizationId);
    }

    public Specification<Employee> byOrganizationIds(Collection<Long> organizationIds) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(Employee_.organizationId).in(organizationIds);
    }

    public Specification<Employee> active() {
        return (root, criteriaQuery, criteriaBuilder) -> employeePredicateGenerator.isActive(root, criteriaBuilder);
    }

    public Specification<Employee> byNameLike(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.byNameLike(
                root.get(Employee_.firstName),
                null,
                root.get(Employee_.lastName),
                name,
                criteriaBuilder
        );
    }

    public Specification<Employee> isIncidentReportReviewer() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Employee_.isIncidentReportReviewer), true);
    }

    public Specification<Employee> chatAccessibleEmployeesByOrganizationIds(PermissionFilter permissionFilter, Long excludedEmployeeId, List<Long> accessibleOrganizationIds) {
        return (root, query, criteriaBuilder) -> employeePredicateGenerator.chatAccessibleEmployeesByOrganizationIds(permissionFilter,
                excludedEmployeeId, accessibleOrganizationIds, root, query, criteriaBuilder);
    }

    public Specification<Employee> videoCallAccessibleEmployeesByOrganizationIds(PermissionFilter permissionFilter, Long excludedEmployeeId, List<Long> accessibleOrganizationIds) {
        return (root, query, criteriaBuilder) -> employeePredicateGenerator.videoCallAccessibleEmployeesByOrganizationIds(
                permissionFilter,
                excludedEmployeeId,
                accessibleOrganizationIds,
                root,
                query,
                criteriaBuilder
        );
    }


    public Specification<Employee> chatAccessibleEmployees(PermissionFilter permissionFilter, Long excludedEmployeeId) {
        return (root, query, criteriaBuilder) -> employeePredicateGenerator.chatAccessibleEmployeesByOrganizationIds(
                permissionFilter, excludedEmployeeId, null, root, query, criteriaBuilder);
    }

    public Specification<Employee> chatAccessibleEmployeesByPermissionsOnly(PermissionFilter permissionFilter, Collection<Long> amongEmployeeIds) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        SpecificationUtils.in(criteriaBuilder, root.get(Employee_.id), amongEmployeeIds),
                        employeePredicateGenerator.hasChatAccessByPermissionsOnly(
                                permissionFilter, root, query, criteriaBuilder)

                );
    }

    public Specification<Employee> excludeParticipatingInOneToOneChatWithAny(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> employeePredicateGenerator.excludeParticipatingInOneToOneChatWithAny(
                employeeIds, root.get(Employee_.id), criteriaQuery, criteriaBuilder);
    }

    public Specification<Employee> byLegacyId(String legacyId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Employee_.legacyId), legacyId);
    }

    public Specification<Employee> byLoginCompanyId(String loginCompanyId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var organization = JpaUtils.getOrCreateJoin(root, Employee_.organization);
            var systemSetup = JpaUtils.getOrCreateJoin(organization, Organization_.systemSetup);
            return criteriaBuilder.equal(systemSetup.get(SystemSetup_.loginCompanyId), loginCompanyId);
        };
    }

    public Specification<Employee> byFullNameLike(String fullName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(fullName)) {
                return criteriaBuilder.and();
            } else {
                var searchName = SpecificationUtils.wrapWithWildcards(fullName);
                return criteriaBuilder.like(SpecificationUtils.employeeFullName(root, criteriaBuilder), searchName);
            }
        };
    }

    public Specification<Employee> isSuperAdmin() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.code),
                CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR
        );
    }

    public Specification<Employee> byCommunityDocumentViewAccess(Long communityId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get(Employee_.communityId), communityId),
                        communityCareTeamMemberPredicateGenerator.isCommunityCareTeamMember(criteriaBuilder, query, root, communityId, AffiliatedCareTeamType.REGULAR),
                        communityPredicateGenerator.isOrganizationAdminInCommunity(root, query, criteriaBuilder, communityId)
                ),
                criteriaBuilder.equal(root.get(Employee_.status), EmployeeStatus.ACTIVE),
                JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.code).in(
                        CareTeamRoleCode.ROLE_CARE_COORDINATOR,
                        CareTeamRoleCode.ROLE_CASE_MANAGER,
                        CareTeamRoleCode.ROLE_COMMUNITY_MEMBERS,
                        CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR,
                        CareTeamRoleCode.ROLE_ADMINISTRATOR
                )
        );
    }

    public Specification<Employee> isSignerOfAnySignatureRequest(Collection<Long> signatureRequestIds) {
        return (root, query, criteriaBuilder) -> {

            var subQuery = query.subquery(Long.class);
            var subRoot = subQuery.from(DocumentSignatureRequest.class);

            var requestedFromEmployeeId = subRoot.get(DocumentSignatureRequest_.requestedFromEmployeeId);
            subQuery.select(requestedFromEmployeeId);
            subQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.in(subRoot.get(DocumentSignatureRequest_.ID)).value(signatureRequestIds),
                            criteriaBuilder.equal(subRoot.get(DocumentSignatureRequest_.status), DocumentSignatureRequestStatus.SIGNED),
                            requestedFromEmployeeId.isNotNull()
                    )
            );

            return criteriaBuilder.in(root.get(Employee_.ID)).value(subQuery);
        };
    }

    public Specification<Employee> byClientViewAccess(Client client) {

        var viewIfAssociatedOrganizationRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(Permission.CLIENT_VIEW_IF_ASSOCIATED_ORGANIZATION);
        var viewIfAssociatedCommunityRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(Permission.CLIENT_VIEW_IF_ASSOCIATED_COMMUNITY);
        var viewIfClientCtmRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(Permission.CLIENT_VIEW_IF_CURRENT_RP_CLIENT_CTM);
        var viewIfCommunityCtmRoles = CareTeamRolePermissionMapping.findCareTeamRoleCodesWithPermission(Permission.CLIENT_VIEW_IF_CURRENT_RP_COMMUNITY_CTM);

        return (root, query, criteriaBuilder) -> {
            var roleCode = JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole).get(CareTeamRole_.CODE);
            return criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.in(roleCode).value(viewIfAssociatedOrganizationRoles),
                                    criteriaBuilder.equal(root.get(Employee_.organizationId), client.getOrganizationId())
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.in(roleCode).value(viewIfAssociatedCommunityRoles),
                                    criteriaBuilder.equal(root.get(Employee_.communityId), client.getCommunityId())
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.in(roleCode).value(viewIfClientCtmRoles),
                                    clientCareTeamMemberPredicateGenerator.isClientCareTeamMember(criteriaBuilder, query,
                                            root, client, AffiliatedCareTeamType.REGULAR,
                                            HieConsentCareTeamType.currentWithOptimizations(client)
                                    )
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.in(roleCode).value(viewIfCommunityCtmRoles),
                                    communityCareTeamMemberPredicateGenerator.isCommunityCareTeamMember(criteriaBuilder,
                                            query, root, client.getCommunityId(), AffiliatedCareTeamType.REGULAR,
                                            HieConsentCareTeamType.currentWithOptimizations(client)
                                    )
                            )
                    ),
                    criteriaBuilder.equal(root.get(Employee_.status), EmployeeStatus.ACTIVE)
            );
        };
    }

    public Specification<Employee> systemRoleNotIn(Collection<CareTeamRoleCode> systemRoleCodes) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var role = JpaUtils.getOrCreateJoin(root, Employee_.careTeamRole, JoinType.LEFT);

            return criteriaBuilder.or(
                    criteriaBuilder.isNull(role),
                    criteriaBuilder.not(role.get(CareTeamRole_.code).in(systemRoleCodes))
            );
        };
    }

    public Specification<Employee> inEligibleForDiscoveryCommunity() {
        return (root, criteriaQuery, criteriaBuilder) ->
                communityPredicateGenerator.eligibleForDiscovery(criteriaBuilder, JpaUtils.getOrCreateJoin(root, Employee_.community));
    }

    public Specification<Employee> byStatusNotIn(Collection<EmployeeStatus> statusesToExclude) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.not(root.get(Employee_.status).in(statusesToExclude));
    }

    public Specification<Employee> withoutCommunity() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(Employee_.community).isNull();
    }

    public Specification<Employee> withRole() {
        return (root, criteriaQuery, criteriaBuilder) ->
                root.get(Employee_.careTeamRole).isNotNull();
    }

    public Specification<Employee> byStatusIn(Collection<EmployeeStatus> statuses) {
        return (root, criteriaQuery, criteriaBuilder) -> root.get(Employee_.status).in(statuses);
    }

    public Specification<Employee> appointmentCreatorInOrganizationWithAccessToAppointment(PermissionFilter permissionFilter, Long organizationId) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var subqueryRoot = subquery.from(ClientAppointment.class);
            subquery.select(subqueryRoot.get(ClientAppointment_.id));
            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get(Employee_.id), subqueryRoot.get(ClientAppointment_.creatorId)),
                            clientAppointmentSpecificationGenerator.hasAccessAndPrivateAccess(permissionFilter).toPredicate(subqueryRoot, query, criteriaBuilder),
                            clientAppointmentSpecificationGenerator.isUnarchived().toPredicate(subqueryRoot, query, criteriaBuilder),
                            clientAppointmentSpecificationGenerator.byOrganizationId(organizationId).toPredicate(subqueryRoot, query, criteriaBuilder))
                    );
            return criteriaBuilder.exists(subquery);
        };
    }

    public Specification<Employee> appointmentServiceProviderWithAccessToAppointment(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var subqueryRoot = subquery.from(ClientAppointment.class);
            subquery.select(subqueryRoot.get(ClientAppointment_.id));
            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get(Employee_.id), JpaUtils.getOrCreateListJoin(subqueryRoot, ClientAppointment_.serviceProviders).get(Employee_.id)),
                            clientAppointmentSpecificationGenerator.hasAccessAndPrivateAccess(permissionFilter).toPredicate(subqueryRoot, query, criteriaBuilder),
                            clientAppointmentSpecificationGenerator.isUnarchived().toPredicate(subqueryRoot, query, criteriaBuilder))
            );
            return criteriaBuilder.exists(subquery);
        };
    }

    public Specification<Employee> isOnHoldCtmForClient(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            var subQuery = criteriaQuery.subquery(Long.class);
            var subqueryRoot = subQuery.from(ClientCareTeamMember.class);
            var subClient = subqueryRoot.get(ClientCareTeamMember_.clientId);

            subQuery.select(subqueryRoot.get(ClientCareTeamMember_.employeeId))
                    .where(criteriaBuilder.and(
                                    criteriaBuilder.equal(subClient, clientId),
                                    criteriaBuilder.equal(subqueryRoot.get(ClientCareTeamMember_.employeeId), root.get(Employee_.id)),
                                    criteriaBuilder.equal(subqueryRoot.get(ClientCareTeamMember_.onHold), true)
                            )
                    );

            return root.get(Employee_.id).in(subQuery);
        };
    }

    public Specification<Employee> canBeCtmForOptedOutClient(ClientSecurityAwareEntity client) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                bySystemRole(CareTeamRoleCode.ROLE_ADMINISTRATOR)
                                        .toPredicate(root, query, criteriaBuilder),
                                criteriaBuilder.equal(root.get(Employee_.organizationId), client.getOrganizationId())
                        ),
                        criteriaBuilder.and(
                                bySystemRole(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)
                                        .toPredicate(root, query, criteriaBuilder),
                                JpaUtils.getOrCreateListJoin(root, Employee_.associatedClients, JoinType.LEFT)
                                        .in(client.getId())
                        ),
                        criteriaBuilder.and(
                                bySystemRoleIn(
                                        EnumSet.complementOf(EnumSet.of(
                                                CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR,
                                                CareTeamRoleCode.ROLE_PARENT_GUARDIAN,
                                                CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES,
                                                CareTeamRoleCode.ROLE_ADMINISTRATOR
                                        )))
                                        .toPredicate(root, query, criteriaBuilder),
                                criteriaBuilder.equal(root.get(Employee_.communityId), client.getCommunityId())
                        )
                );
    }
}
