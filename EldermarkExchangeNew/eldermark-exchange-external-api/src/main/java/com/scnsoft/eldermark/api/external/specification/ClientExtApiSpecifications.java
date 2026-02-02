package com.scnsoft.eldermark.api.external.specification;

import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.dao.predicate.ClientPredicateGenerator;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ClientExtApiSpecifications {

    @Autowired
    private CommunityExtApiPredicates communityExtApiPredicates;

    @Autowired
    private ClientPredicateGenerator clientPredicateGenerator;

    public Specification<Client> isVisible() {
        return isVisible(false);
    }

    public Specification<Client> isVisible(boolean allowInactive) {
        return (root, query, criteriaBuilder) -> {
            //    String RESIDENT_IS_VISIBLE = "(f.isInactive <> 1 AND r.isOptOut <> 1 AND r.active = 1)";
            var communityInactive = root.join(Client_.community).get(Community_.inactive);
            return criteriaBuilder.and(
                    SpecificationUtils.isNotTrue(criteriaBuilder, communityInactive),
                    SpecificationUtils.isNotTrue(criteriaBuilder, root.get(Client_.isOptOut)),
                    clientPredicateGenerator.isOptedIn(root, criteriaBuilder),
                    allowInactive ? criteriaBuilder.and() : criteriaBuilder.isTrue(root.get(Client_.active))
            );
        };
    }

    public Specification<Client> byConsanaXrefDto(ConsanaXrefPatientIdDto dto) {
        return (root, query, criteriaBuilder) -> {
            if (isEmpty(dto)) {
                criteriaBuilder.or();
            }
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(Client_.consanaXrefId), dto.getIdentifier()),
                    criteriaBuilder.equal(root.get(Client_.organization).get(Organization_.consanaXOwningId), dto.getOrganizationOID()),
                    criteriaBuilder.equal(root.get(Client_.community).get(Community_.consanaOrgId), dto.getCommunityOID())
            );
        };
    }

    public Specification<Client> clientCommunityConsanaSyncEnabled(boolean value) {
        return (root, query, criteriaBuilder) ->
                communityExtApiPredicates.isCommunityConsanaSyncEnabled(root.get(Client_.community), criteriaBuilder, value);
    }

    private boolean isEmpty(ConsanaXrefPatientIdDto consanaXrefPatientDto) {
        return consanaXrefPatientDto == null
                || StringUtils.isBlank(consanaXrefPatientDto.getIdentifier())
                || StringUtils.isBlank(consanaXrefPatientDto.getOrganizationOID())
                || StringUtils.isBlank(consanaXrefPatientDto.getCommunityOID());
    }
}
