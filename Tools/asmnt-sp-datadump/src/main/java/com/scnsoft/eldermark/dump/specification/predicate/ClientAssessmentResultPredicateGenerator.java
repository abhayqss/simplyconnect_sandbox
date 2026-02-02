package com.scnsoft.eldermark.dump.specification.predicate;

import com.scnsoft.eldermark.dump.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

@Component
public class ClientAssessmentResultPredicateGenerator {

    public Predicate inProgressTillDate(LocalDateTime end, Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                inProgress(path, criteriaBuilder),
                criteriaBuilder.lessThanOrEqualTo(path.get(ClientAssessmentResult_.dateAssigned), end)
        );
    }

    public Predicate completedWithinPeriod(LocalDateTime start, LocalDateTime end, Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                completed(path, criteriaBuilder),
                criteriaBuilder.between(path.get((ClientAssessmentResult_.dateCompleted)), start, end)
        );
    }

    public Predicate inProgress(Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return withStatus(AssessmentStatus.IN_PROCESS, path, criteriaBuilder);
    }

    public Predicate completed(Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return withStatus(AssessmentStatus.COMPLETED, path, criteriaBuilder);
    }

    private Predicate withStatus(AssessmentStatus status, Path<ClientAssessmentResult> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(ClientAssessmentResult_.assessmentStatus), status);
    }
}