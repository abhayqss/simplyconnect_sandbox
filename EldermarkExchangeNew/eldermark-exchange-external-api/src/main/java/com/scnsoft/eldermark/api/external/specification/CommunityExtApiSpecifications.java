package com.scnsoft.eldermark.api.external.specification;

import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CommunityExtApiSpecifications {

    @Autowired
    private CommunityExtApiPredicates communityExtApiPredicates;

    public Specification<Community> consanaSyncEnabled(boolean value) {
        return (root, query, criteriaBuilder) ->
                communityExtApiPredicates.isCommunityConsanaSyncEnabled(root, criteriaBuilder, value);
    }
}
