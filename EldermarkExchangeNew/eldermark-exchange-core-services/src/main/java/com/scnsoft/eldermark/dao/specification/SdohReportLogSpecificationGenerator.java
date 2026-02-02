package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;

@Component
public class SdohReportLogSpecificationGenerator {

    public Specification<SdohReportLog> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(SdohReportLog_.organization).get(Organization_.id), organizationId
        );
    }

    public Specification<SdohReportLog> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> hasAccessByOrganization(permissionFilter, root.get(SdohReportLog_.organization), criteriaBuilder);
    }

    public Specification<Organization> canViewInOrganization(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> hasAccessByOrganization(permissionFilter, root, criteriaBuilder);

    }

    private Predicate hasAccessByOrganization(PermissionFilter permissionFilter, Path<Organization> organizationPath, CriteriaBuilder criteriaBuilder) {
        var predicates = new ArrayList<Predicate>();

        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            predicates.add(criteriaBuilder.and());
        }

        if (permissionFilter.hasPermission(Permission.SDOH_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(Permission.SDOH_VIEW_IF_ASSOCIATED_ORGANIZATION);

            predicates.add(
                    criteriaBuilder.in(organizationPath.get(Organization_.ID))
                            .value(SpecificationUtils.employeesOrganizationIds(employees))
            );
        }

        var enabledSDoH = criteriaBuilder.isTrue(organizationPath.get(Organization_.sdohReportsEnabled));

        return criteriaBuilder.and(
                enabledSDoH,
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );
    }

}
