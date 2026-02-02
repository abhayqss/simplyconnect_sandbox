package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ClientAllergyFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import javax.persistence.criteria.Predicate;

@Component
public class ClientAllergySpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<ClientAllergy> byFilterAndNotEmptyAndHasAccessWithoutDuplicates(PermissionFilter permissionFilter, ClientAllergyFilter filter) {
        return excludeDuplicates(byFilter(filter).and(notEmpty().and(hasAccess(permissionFilter))));
    }

    private Specification<ClientAllergy> byFilter(ClientAllergyFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filter.getClientId() != null) {
                predicates.add(
                        clientPredicateGenerator.clientAndMergedClientsById(
                                criteriaBuilder,
                                root.get(ClientAllergy_.clientId),
                                criteriaQuery,
                                Collections.singletonList(filter.getClientId()))
                );
            }

            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                predicates.add(criteriaBuilder.in(root.get(ClientAllergy_.STATUS)).value(filter.getStatuses()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<ClientAllergy> excludeDuplicates(Specification<ClientAllergy> restriction) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var sub = criteriaQuery.subquery(Long.class);
            var subAllergy = sub.from(ClientAllergy.class);

            sub.select(criteriaBuilder.min(subAllergy.get(ClientAllergy_.id)));
            sub.where(restriction.toPredicate(subAllergy, criteriaQuery, criteriaBuilder));
            sub.groupBy(
                    subAllergy.get(ClientAllergy_.productText),
                    subAllergy.get(ClientAllergy_.status),
                    subAllergy.get(ClientAllergy_.combinedReactionTexts)
            );

            return root.in(sub);
        };
    }

    private Specification<ClientAllergy> notEmpty() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get(ClientAllergy_.productText)),
                criteriaBuilder.notEqual(root.get(ClientAllergy_.productText), StringUtils.EMPTY)
        );
    }

    public Specification<ClientAllergy> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) -> securityPredicateGenerator.hasAccessWithPHRFlagsAndMerged(
                JpaUtils.getOrCreateJoin(root, ClientAllergy_.client),
                criteriaQuery, criteriaBuilder, permissionFilter,
                Permission.ALLERGY_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
                Permission.ALLERGY_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
                Permission.ALLERGY_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
                Permission.ALLERGY_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                Permission.ALLERGY_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                Permission.ALLERGY_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
                Permission.ALLERGY_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
                Permission.ALLERGY_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                Permission.ALLERGY_VIEW_MERGED_IF_SELF_RECORD,
                null,
                Permission.ALLERGY_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                AccessRight.Code.MY_PHR);
    }
}
