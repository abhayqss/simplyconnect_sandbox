package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientProblemCount;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.List;

public class CustomClientProblemDaoImpl implements CustomClientProblemDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<ClientProblemCount> countGroupedByStatus(Specification<ClientProblem> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ClientProblemCount.class);
        var root = crq.from(ClientProblem.class);

        crq.multiselect(root.get(ClientProblem_.status), cb.count(root.get(ClientProblem_.id)));
        crq.where(specification.toPredicate(root, crq, cb));
        crq.groupBy(root.get(ClientProblem_.status));

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}
