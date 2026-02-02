package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InNetworkInsurance_;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress_;
import com.scnsoft.eldermark.entity.Person_;
import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.history.ClientHistory;
import com.scnsoft.eldermark.entity.history.ClientHistory_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;

@Repository
public class CustomClientHistoryDaoImpl implements CustomClientHistoryDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomClientHistoryDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClientIntakesReportItem> findClientIntakesReportItems(Specification<ClientHistory> specification, Sort sort) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ClientIntakesReportItem.class);
        var root = crq.from(ClientHistory.class);
        var communityJoin = root.join(ClientHistory_.community);
        var genderJoin = root.join(ClientHistory_.gender, JoinType.LEFT);
        var raceJoin = root.join(ClientHistory_.race, JoinType.LEFT);
        var insuranceJoin = root.join(ClientHistory_.inNetworkInsurance, JoinType.LEFT);
        var addressesJoin = root.join(ClientHistory_.person, JoinType.LEFT).join(Person_.addresses, JoinType.LEFT);

        var subQuery = crq.subquery(Long.class);
        var subRoot = subQuery.from(Person.class);
        subQuery.select(cb.min(subRoot.join(Person_.addresses, JoinType.LEFT).get(PersonAddress_.id)));
        subQuery.where(cb.equal(subRoot.get(Person_.id), root.join(ClientHistory_.person, JoinType.LEFT).get(Person_.id)));

        crq.multiselect(root.get(ClientHistory_.clientId), root.get(ClientHistory_.firstName), root.get(ClientHistory_.lastName), communityJoin.get(Community_.id),
            communityJoin.get(Community_.name), root.get(ClientHistory_.intakeDate), root.get(ClientHistory_.modifiedDate), root.get(ClientHistory_.active),
            root.get(ClientHistory_.birthDate), root.get(ClientHistory_.dateCreated), genderJoin.get(CcdCode_.displayName),
            raceJoin.get(CcdCode_.displayName), addressesJoin.get(PersonAddress_.city), insuranceJoin.get(InNetworkInsurance_.displayName),
            root.get(ClientHistory_.insurancePlan), root.get(ClientHistory_.exitDate), root.get(ClientHistory_.activationDate),
            root.get(ClientHistory_.deactivationDate), root.get(ClientHistory_.comment), root.get(ClientHistory_.exitComment), root.get(ClientHistory_.deactivationReason));
        crq.where(specification.toPredicate(root, crq, cb), cb.or(cb.isNull(subQuery), cb.in(addressesJoin.get(PersonAddress_.id)).value(subQuery)));

        if (sort != null) {
            crq.orderBy(QueryUtils.toOrders(sort, root, cb));
        }

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}
