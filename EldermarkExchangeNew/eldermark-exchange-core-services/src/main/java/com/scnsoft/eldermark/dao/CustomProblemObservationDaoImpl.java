package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation_;
import com.scnsoft.eldermark.entity.document.ccd.Problem_;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.Map;

public class CustomProblemObservationDaoImpl implements CustomProblemObservationDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomProblemObservationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, Long> countsByClientId(Specification<ProblemObservation> spec) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        var root = crq.from(ProblemObservation.class);
        var problemJoin = root.join(ProblemObservation_.problem);

        crq.where(spec.toPredicate(root, crq, cb));
        crq.groupBy(problemJoin.get(Problem_.clientId));

        crq.multiselect(problemJoin.get(Problem_.clientId), cb.count(root.get(ProblemObservation_.id)));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        var result = resultList.stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(
                        t -> t.get(0, Long.class),
                        t -> t.get(1, Long.class))
                );

        return result;
    }
}
