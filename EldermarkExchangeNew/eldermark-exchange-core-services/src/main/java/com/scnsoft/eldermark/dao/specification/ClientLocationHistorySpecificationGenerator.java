package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.ClientLocationHistory;
import com.scnsoft.eldermark.entity.ClientLocationHistory_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ClientLocationHistorySpecificationGenerator {

    public Specification<ClientLocationHistory> byClientId(Long clientId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ClientLocationHistory_.clientId), clientId);
    }
}
