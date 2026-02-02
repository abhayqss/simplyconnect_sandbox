package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ServicePlanCount;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.serviceplan.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CustomServicePlanDaoImpl implements CustomServicePlanDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomServicePlanDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ServicePlanCount> countGroupedByStatus(Specification<ServicePlan> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ServicePlanCount.class);
        var servicePlanRoot = crq.from(ServicePlan.class);

        crq.multiselect(servicePlanRoot.get(ServicePlan_.servicePlanStatus), cb.count(servicePlanRoot.get(ServicePlan_.id)));
        crq.where(specification.toPredicate(servicePlanRoot, crq, cb));
        crq.groupBy(servicePlanRoot.get(ServicePlan_.servicePlanStatus));

        var typed = entityManager.createQuery(crq);

        return typed.getResultList();

    }

    @Override
    public List<ClientServicePlanScoringAware> findAllClientAndEmployeeServicePlanScoring(Specification<ServicePlan> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var q = cb.createQuery(ClientServicePlanScoringAware.class);
        var root = q.from(ServicePlan.class);

        var scoringPath = root.join(ServicePlan_.scoring);
        q.multiselect(
            root.get(ServicePlan_.clientId),
            root.join(ServicePlan_.needs).get(ServicePlanNeed_.domain),
            scoringPath.get(ServicePlanScoring_.behavioralScore),
            scoringPath.get(ServicePlanScoring_.supportScore),
            scoringPath.get(ServicePlanScoring_.healthStatusScore),
            scoringPath.get(ServicePlanScoring_.housingScore),
            scoringPath.get(ServicePlanScoring_.nutritionSecurityScore),
            scoringPath.get(ServicePlanScoring_.transportationScore),
            scoringPath.get(ServicePlanScoring_.otherScore),
            scoringPath.get(ServicePlanScoring_.housingOnlyScore),
            scoringPath.get(ServicePlanScoring_.socialWellnessScore),
            scoringPath.get(ServicePlanScoring_.employmentScore),
            scoringPath.get(ServicePlanScoring_.mentalWellnessScore),
            scoringPath.get(ServicePlanScoring_.physicalWellnessScore),
            scoringPath.get(ServicePlanScoring_.legalScore),
            scoringPath.get(ServicePlanScoring_.financesScore),
            scoringPath.get(ServicePlanScoring_.medicalOtherSupplyScore),
            scoringPath.get(ServicePlanScoring_.medicationMgmtAssistanceScore),
            scoringPath.get(ServicePlanScoring_.homeHealthScore)
        );
        q.where(specification.toPredicate(root, q, cb));

        return entityManager.createQuery(q)
            .getResultList();
    }
}
