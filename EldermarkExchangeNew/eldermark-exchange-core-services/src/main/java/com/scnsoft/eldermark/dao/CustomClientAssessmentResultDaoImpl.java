package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CustomClientAssessmentResultDaoImpl implements CustomClientAssessmentResultDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomClientAssessmentResultDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClientAssessmentCount> countGroupedByStatus(Specification<ClientAssessmentResult> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ClientAssessmentCount.class);
        var root = crq.from(ClientAssessmentResult.class);

        crq.multiselect(root.get(ClientAssessmentResult_.assessmentStatus), cb.count(root.get(ClientAssessmentResult_.id)));
        crq.where(specification.toPredicate(root, crq, cb));
        crq.groupBy(root.get(ClientAssessmentResult_.assessmentStatus));

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}
