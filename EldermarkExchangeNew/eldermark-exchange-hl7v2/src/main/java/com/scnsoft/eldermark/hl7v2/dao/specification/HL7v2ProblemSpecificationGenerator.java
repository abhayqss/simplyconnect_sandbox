package com.scnsoft.eldermark.hl7v2.dao.specification;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation_;
import com.scnsoft.eldermark.entity.document.ccd.Problem_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class HL7v2ProblemSpecificationGenerator {

    public Specification<Problem> isExists(Problem problem) {
        return (root, query, criteriaBuilder) -> {
            var firstProblemObservation = problem.getProblemObservations().iterator().next();
            var problemObservationJoin = JpaUtils.getOrCreateListJoin(root, Problem_.problemObservations);

            var sameClient = criteriaBuilder.equal(root.get(Problem_.clientId), problem.getClientId());

            var sameDiagnosisCode = HL7v2SpecificationUtils.compareCcdCode(
                    problemObservationJoin.get(ProblemObservation_.problemCode),
                    firstProblemObservation.getProblemCode(),
                    criteriaBuilder
            );

            var sameDiagnosisText = HL7v2SpecificationUtils.compareString(
                    problemObservationJoin.get(ProblemObservation_.problemName),
                    firstProblemObservation.getProblemName(),
                    criteriaBuilder
            );

            return criteriaBuilder.and(
                    sameClient,
                    sameDiagnosisCode,
                    sameDiagnosisText
            );
        };
    }
}
