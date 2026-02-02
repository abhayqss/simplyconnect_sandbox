package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ClientCareTeamInvitationFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.BaseEmployeeSecurityEntity;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitationStatus;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ClientCareTeamInvitationSpecificationGenerator {

    public Specification<ClientCareTeamInvitation> byFilter(ClientCareTeamInvitationFilter filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (filter.getTargetEmployeeId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get(ClientCareTeamInvitation_.targetEmployeeId),
                        filter.getTargetEmployeeId()
                ));
            }

            if (filter.getClientId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get(ClientCareTeamInvitation_.clientId),
                        filter.getClientId())
                );
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(SpecificationUtils.in(criteriaBuilder, root.get(ClientCareTeamInvitation_.status), filter.getStatuses()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public Specification<ClientCareTeamInvitation> isNotHidden() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(ClientCareTeamInvitation_.isHidden));
    }

    public Specification<ClientCareTeamInvitation> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> {
            if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                return criteriaBuilder.conjunction();
            }

            var predicates = new ArrayList<Predicate>();

            if (permissionFilter.hasPermission(Permission.CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
                var employeeOrganizationIds = permissionFilter.getEmployees(Permission.CLIENT_CARE_TEAM_INVITATION_VIEW_IF_ASSOCIATED_ORGANIZATION).stream()
                        .map(BasicEntity::getOrganizationId)
                        .collect(Collectors.toSet());

                var client = JpaUtils.getOrCreateJoin(root, ClientCareTeamInvitation_.client);

                predicates.add(client.get(Client_.organizationId).in(employeeOrganizationIds));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_CARE_TEAM_INVITATION_ADD_IF_ASSOCIATED_COMMUNITY)) {
                var employeeCommunityIds = permissionFilter.getEmployees(Permission.CLIENT_CARE_TEAM_INVITATION_ADD_IF_ASSOCIATED_COMMUNITY).stream()
                        .map(BaseEmployeeSecurityEntity::getCommunityId)
                        .collect(Collectors.toSet());

                var client = JpaUtils.getOrCreateJoin(root, ClientCareTeamInvitation_.client);

                predicates.add(client.get(Client_.communityId).in(employeeCommunityIds));
            }

            if (permissionFilter.hasPermission(Permission.CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_CLIENT_RECORD)) {
                var associatedClientIds = permissionFilter.getEmployees(Permission.CLIENT_CARE_TEAM_INVITATION_VIEW_IF_SELF_CLIENT_RECORD).stream()
                        .map(Employee::getAssociatedClientIds)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

                predicates.add(root.get(ClientCareTeamInvitation_.clientId).in(associatedClientIds));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public Specification<ClientCareTeamInvitation> createdBeforeOrEqualDate(Instant date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(ClientCareTeamInvitation_.createdAt), date);
    }
    public Specification<ClientCareTeamInvitation> createdAfterDate(Instant date) {
        return (root, query, cb) -> cb.greaterThan(root.get(ClientCareTeamInvitation_.createdAt), date);
    }

    public Specification<ClientCareTeamInvitation> byType(ClientCareTeamInvitationStatus status) {
        return (root, query, cb) -> cb.equal(root.get(ClientCareTeamInvitation_.status), status);
    }

    public Specification<ClientCareTeamInvitation> byTargetEmployeeId(Long employeeId) {
        return (root, query, cb) -> cb.equal(root.get(ClientCareTeamInvitation_.targetEmployeeId), employeeId);
    }

    public Specification<ClientCareTeamInvitation> byTargetEmployeeIdIn(Collection<Long> employeeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.in(
                criteriaBuilder,
                root.get(ClientCareTeamInvitation_.targetEmployeeId),
                employeeIds
        );
    }

    public Specification<ClientCareTeamInvitation> byClientId(Long clientId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ClientCareTeamInvitation_.clientId), clientId);
    }

    public Specification<ClientCareTeamInvitation> byClientIdIn(Collection<Long> clientIds) {
        return (root, criteriaQuery, criteriaBuilder) -> SpecificationUtils.in(
                criteriaBuilder,
                root.get(ClientCareTeamInvitation_.clientId),
                clientIds
        );
    }
}
