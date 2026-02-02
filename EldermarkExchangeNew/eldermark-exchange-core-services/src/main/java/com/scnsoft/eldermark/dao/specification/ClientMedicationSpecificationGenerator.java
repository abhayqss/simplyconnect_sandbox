package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.predicate.SecurityPredicateGenerator;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.document.ccd.Indication_;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.ClientMedication_;
import com.scnsoft.eldermark.entity.medication.MedicationInformation_;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ClientMedicationSpecificationGenerator {

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    @Autowired
    private SecurityPredicateGenerator securityPredicateGenerator;

    public Specification<ClientMedication> byFilterAndHasAccessWithoutDuplicates(PermissionFilter permissionFilter, ClientMedicationFilter filter) {
        return excludeDuplicates(byFilter(filter).and(hasAccess(permissionFilter)));
    }

    public Specification<ClientMedication> byFilter(ClientMedicationFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filter.getClientId() != null) {
                predicates.add(
                        clientPredicateGenerator.clientAndMergedClientsById(
                                criteriaBuilder,
                                root.get(ClientMedication_.clientId),
                                criteriaQuery,
                                Collections.singletonList(filter.getClientId()))
                );
            }

            if (filter.getName() != null) {

                var productNameText = JpaUtils.getOrCreateJoin(root, ClientMedication_.medicationInformation)
                        .get(MedicationInformation_.productNameText);

                predicates.add(
                        criteriaBuilder.like(productNameText, SpecificationUtils.wrapWithWildcards(filter.getName()))
                );
            }

            if (ObjectUtils.anyNotNull(filter.getIncludeActive(), filter.getIncludeInactive(), filter.getIncludeUnknown())) {
                Set<ClientMedicationStatus> statuses = new HashSet<>();
                if (BooleanUtils.isTrue(filter.getIncludeActive())) {
                    statuses.add(ClientMedicationStatus.ACTIVE);
                }
                if (BooleanUtils.isTrue(filter.getIncludeInactive())) {
                    statuses.add(ClientMedicationStatus.COMPLETED);
                }
                if (BooleanUtils.isTrue(filter.getIncludeUnknown())) {
                    statuses.add(ClientMedicationStatus.UNKNOWN);
                }
                predicates.add(criteriaBuilder.in(root.get(ClientMedication_.STATUS)).value(statuses));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<ClientMedication> excludeDuplicates(Specification<ClientMedication> restriction) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var sub = criteriaQuery.subquery(Long.class);
            var subMedication = sub.from(ClientMedication.class);

            sub.select(criteriaBuilder.min(subMedication.get(ClientMedication_.id)));
            sub.where(restriction.toPredicate(subMedication, criteriaQuery, criteriaBuilder));
            sub.groupBy(
                    subMedication.get(ClientMedication_.medicationStarted),
                    subMedication.get(ClientMedication_.medicationStopped),
                    subMedication.get(ClientMedication_.freeTextSig),
                    subMedication.join(ClientMedication_.medicationInformation, JoinType.LEFT).get(MedicationInformation_.productNameText),
                    subMedication.join(ClientMedication_.indications, JoinType.LEFT).get(Indication_.value),
                    subMedication.get(ClientMedication_.status),
                    criteriaBuilder.selectCase()
                            .when(
                                    criteriaBuilder.isTrue(subMedication.get(ClientMedication_.isManuallyCreated)),
                                    subMedication.get(ClientMedication_.creationDatetime)
                            )
                            .otherwise(criteriaBuilder.nullLiteral(Instant.class))
            );

            return root.in(sub);
        };
    }

    public Specification<ClientMedication> hasAccess(PermissionFilter permissionFilter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                securityPredicateGenerator.hasAccessWithPHRFlagsAndMerged(
                        JpaUtils.getOrCreateJoin(root, ClientMedication_.client),
                        criteriaQuery, criteriaBuilder, permissionFilter,
                        Permission.MEDICATION_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
                        Permission.MEDICATION_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
                        Permission.MEDICATION_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
                        Permission.MEDICATION_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                        Permission.MEDICATION_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                        Permission.MEDICATION_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
                        Permission.MEDICATION_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
                        Permission.MEDICATION_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                        Permission.MEDICATION_VIEW_MERGED_IF_SELF_RECORD,
                        null,
                        Permission.MEDICATION_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                        AccessRight.Code.MY_PHR, AccessRight.Code.MEDICATIONS_LIST);
    }

    public Specification<ClientMedication> byOrganizationId(Long organizationId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ClientMedication_.organizationId), organizationId);
    }

    public Specification<ClientMedication> byCommunityId(Long communityId) {
        return (root, query, criteriaBuilder) -> {
            var communityIdPath = JpaUtils.getOrCreateJoin(root, ClientMedication_.client).get(Client_.communityId);

            return criteriaBuilder.equal(communityIdPath, communityId);
        };
    }
}
