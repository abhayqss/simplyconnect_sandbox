package com.scnsoft.eldermark.api.external.specification;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Component
public class ClientCareTeamExtApiSpecifications {

    public Specification<ClientCareTeamMember> familyClientCtm(Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                ofClient(criteriaBuilder, root, clientId),
                criteriaBuilder.equal(root.get(ClientCareTeamMember_.careTeamRole).get(CareTeamRole_.code), CareTeamRoleCode.ROLE_PARENT_GUARDIAN)
        );
    }

    public Specification<ClientCareTeamMember> careProviderClientCtm(Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                ofClient(criteriaBuilder, root, clientId),
                criteriaBuilder.notEqual(root.get(ClientCareTeamMember_.careTeamRole).get(CareTeamRole_.code), CareTeamRoleCode.ROLE_PARENT_GUARDIAN)
        );
    }

    public Specification<ClientCareTeamMember> clientCtm(Long clientId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                ofClient(criteriaBuilder, root, clientId)
        );
    }

    private Predicate ofClient(CriteriaBuilder cb, Path<ClientCareTeamMember> clientCtm, Long clientId) {
        return cb.equal(clientCtm.get(ClientCareTeamMember_.clientId), clientId);
    }

}
