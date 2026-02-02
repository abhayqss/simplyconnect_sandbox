package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.predicate.ClientCareTeamMemberPredicateGenerator;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CustomClientCareTeamMemberDaoImpl implements CustomClientCareTeamMemberDao {

    public static final String IS_CURRENT_ALIAS = "isCurrent";
    public static final String ID_ALIAS = "id";
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ClientCareTeamMemberPredicateGenerator clientCareTeamMemberPredicateGenerator;

    @Override
    public Map<Boolean, Set<Long>> calculateCurrentOnHoldCandidate(Specification<ClientCareTeamMember> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var criteriaQuery = cb.createTupleQuery();
        var root = criteriaQuery.from(ClientCareTeamMember.class);

        criteriaQuery
                .multiselect(
                        cb.selectCase()
                                .when(clientCareTeamMemberPredicateGenerator.onHoldCandidates(root, cb), false)
                                .otherwise(true)
                                .alias(IS_CURRENT_ALIAS),
                        root.get(ClientCareTeamMember_.id).alias(ID_ALIAS)
                )
                .where(specification.toPredicate(root, criteriaQuery, cb));

        var query = entityManager.createQuery(criteriaQuery);

        var resultList = query.getResultList();


        return resultList.stream().collect(Collectors.groupingBy(
                t -> t.get(IS_CURRENT_ALIAS, Boolean.class),
                Collectors.mapping(t -> t.get(ID_ALIAS, Long.class), Collectors.toSet())));
    }
}
