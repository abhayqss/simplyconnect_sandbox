package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.CareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

public abstract class CareTeamMemberSpecificationGenerator<T extends CareTeamMember> {

    public Specification<T> byId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(CareTeamMember_.id), id);
    }

    public Specification<T> byIdNot(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(CareTeamMember_.id), id);
    }

    public Specification<T> byEmployeeNameLike(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(name)) {
                return criteriaBuilder.and();
            }

            var searchName = SpecificationUtils.wrapWithWildcards(name);
            return criteriaBuilder.like(
                    SpecificationUtils.employeeFullName(root.join(CareTeamMember_.employee), criteriaBuilder), searchName);
        };
    }

    public Specification<T> employeeIn(Collection<Employee> employees) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get(CareTeamMember_.EMPLOYEE)).value(employees);
    }

    public Specification<T> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CareTeamMember_.employeeId), employeeId);
    }

    public Specification<T> byEmployeeLogin(String login) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee).get(Employee_.loginName), login);
    }

    public Specification<T> byEmployeeIdNot(Long employeeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get(CareTeamMember_.employeeId), employeeId);
    }

    public Specification<T> byEmployeeIdIn(Collection<Long> employeeIds) {
        return (root, query, criteriaBuilder) ->
                root.get(CareTeamMember_.employeeId).in(employeeIds);
    }

    public Specification<T> isEmployeeActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee).get(Employee_.status), EmployeeStatus.ACTIVE);
    }

    public Specification<T> byCareTeamRoleCodeIn(List<CareTeamRoleCode> codes) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(JpaUtils.getOrCreateJoin(root, CareTeamMember_.careTeamRole).get(CareTeamRole_.CODE)).value(codes);
    }

    public Specification<T> ofAffiliationType(AffiliatedCareTeamType type) {
        return (root, query, criteriaBuilder) -> getPredicateGenerator().ofAffiliationType(root, criteriaBuilder, type);
    }

    protected abstract CareTeamMemberPredicateGenerator<T> getPredicateGenerator();

    public Specification<T> hasAccess(PermissionFilter permissionFilter) {
        return (root, query, criteriaBuilder) -> getPredicateGenerator().hasAccess(permissionFilter, root, query, criteriaBuilder);
    }

    public Specification<T> chatAccessible(PermissionFilter permissionFilter, Long excludedEmployeeId) {
        return (root, query, criteriaBuilder) -> getPredicateGenerator().chatAccessible(permissionFilter,
                excludedEmployeeId, root, query, criteriaBuilder);
    }

    public Specification<T> videoCallAccessible(PermissionFilter permissionFilter, Long excludedEmployeeId) {
        return (root, query, criteriaBuilder) -> getPredicateGenerator().videoCallAccessible(permissionFilter,
                excludedEmployeeId, root, query, criteriaBuilder);
    }

    public Specification<T> byEmployeeOrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee).get(Employee_.organizationId),
                organizationId
        );
    }

    public Specification<T> byEmployeeSystemRoleIn(Collection<CareTeamRoleCode> roles) {
        return (root, query, criteriaBuilder) -> {
            var employeeJoin = JpaUtils.getOrCreateJoin(root, CareTeamMember_.employee);
            var systemRoleJoin = JpaUtils.getOrCreateJoin(employeeJoin, Employee_.careTeamRole);

            return systemRoleJoin.get(CareTeamRole_.code).in(roles);
        };
    }

    public Specification<T> byEmployeeSystemRoleNotIn(Collection<CareTeamRoleCode> roles) {
        return Specification.not(byEmployeeSystemRoleIn(roles));
    }

    public Specification<T> byEmployeeCommunityId(Long communityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CareTeamMember_.employee).get(Employee_.communityId), communityId);
    }
}
