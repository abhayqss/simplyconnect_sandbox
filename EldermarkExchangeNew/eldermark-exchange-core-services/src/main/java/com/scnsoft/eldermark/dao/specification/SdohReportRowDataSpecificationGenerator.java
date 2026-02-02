package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.predicate.ServicePlanPredicateGenerator;
import com.scnsoft.eldermark.entity.sdoh.SdohReportRowData;
import com.scnsoft.eldermark.entity.sdoh.SdohReportRowData_;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class SdohReportRowDataSpecificationGenerator {

    @Autowired
    private ServicePlanPredicateGenerator servicePlanPredicateGenerator;

    public Specification<SdohReportRowData> hasAccess(PermissionFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var accessibleServicePlans = SpecificationUtils.subquery(ServicePlan.class,
                    criteriaQuery,
                    spRoot -> servicePlanPredicateGenerator.hasAccess(spRoot, criteriaQuery, criteriaBuilder, filter));

            return root.get(SdohReportRowData_.servicePlanId).in(accessibleServicePlans);
        };
    }

    public Specification<SdohReportRowData> byReportLogId(Long reportLogId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(SdohReportRowData_.sdohReportLogId),
                reportLogId
        );
    }

}
