package com.scnsoft.eldermark.dump.specification;

import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.entity.Client_;
import com.scnsoft.eldermark.dump.entity.Community;
import com.scnsoft.eldermark.dump.specification.predicate.ClientPredicate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ClientSpecificationGenerator {

    public Specification<Client> byIds(DumpFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(root.get(Client_.ID)).value(CollectionUtils.emptyIfNull(filter.getResidentIds()));
    }

    public Specification<Client> byOrganizationId(Long organizationId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Client_.organizationId), organizationId);
   }

    public Specification<Client> byCommunity(Community community) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Client_.community), community);
    }

    public Specification<Client> isActive() {
        return (root, criteriaQuery, criteriaBuilder) ->
                ClientPredicate.isActive(root, criteriaBuilder);
    }
}
