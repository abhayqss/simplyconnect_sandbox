package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomClientDaoImpl implements CustomClientDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomClientDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClientIntakesReportItem> findClientIntakesReportItems(Specification<Client> specification, Sort sort) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ClientIntakesReportItem.class);
        var root = crq.from(Client.class);
        var communityJoin = root.join(Client_.community);
        var genderJoin = root.join(Client_.gender, JoinType.LEFT);
        var raceJoin = root.join(Client_.race, JoinType.LEFT);
        var insuranceJoin = root.join(Client_.inNetworkInsurance, JoinType.LEFT);
        var addressesJoin = root.join(Client_.person, JoinType.LEFT).join(Person_.addresses, JoinType.LEFT);

        var subQuery = crq.subquery(Long.class);
        var subRoot = subQuery.from(Person.class);
        subQuery.select(cb.min(subRoot.join(Person_.addresses, JoinType.LEFT).get(PersonAddress_.id)));
        subQuery.where(cb.equal(subRoot.get(Person_.id), root.join(Client_.person, JoinType.LEFT).get(Person_.id)));

        crq.multiselect(root.get(Client_.id), root.get(Client_.firstName), root.get(Client_.lastName), communityJoin.get(Community_.id),
                communityJoin.get(Community_.name), root.get(Client_.intakeDate), root.get(Client_.lastUpdated), root.get(Client_.active),
                root.get(Client_.birthDate), root.get(Client_.createdDate), genderJoin.get(CcdCode_.displayName),
                raceJoin.get(CcdCode_.displayName), addressesJoin.get(PersonAddress_.city),
                insuranceJoin.get(InNetworkInsurance_.displayName), root.get(Client_.insurancePlan));
        crq.where(specification.toPredicate(root, crq, cb), cb.or(cb.isNull(subQuery), cb.in(addressesJoin.get(PersonAddress_.id)).value(subQuery)));

        if (sort != null) {
            crq.orderBy(QueryUtils.toOrders(sort, root, cb));
        }

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }

    @Override
    public List<IdCommunityIdAssociatedEmployeeIdsAware> findAllIdCommunityIdAssociatedEmployeeIdsAware(Specification<Client> spec) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(Tuple.class);
        var root = query.from(Client.class);

        query.multiselect(root.get(Client_.id), root.get(Client_.communityId), root.get(Client_.associatedEmployee).get(Employee_.id));
        query.where(spec.toPredicate(root, query, cb));

        return entityManager.createQuery(query)
                .getResultList()
                .stream()
                .map(tuple -> {
                    var id = tuple.get(0, Long.class);
                    var communityId = tuple.get(1, Long.class);
                    var associatedEmployeeId = tuple.get(2, Long.class);
                    return IdCommunityIdAssociatedEmployeeIdsAware.of(id, communityId, associatedEmployeeId);
                })
                .collect(Collectors.toList());
    }
}
