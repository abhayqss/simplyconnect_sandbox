package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.Instant;

@Component
public class ClientAssessmentResultPredicateGenerator {

    public Predicate inProgressTillDate(Instant end, Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                inProgress(path, criteriaBuilder),
                criteriaBuilder.lessThanOrEqualTo(path.get(ClientAssessmentResult_.dateStarted), end)
        );
    }

    public Predicate completedWithinPeriod(Instant start, Instant end, Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                completed(path, criteriaBuilder),
                criteriaBuilder.between(path.get(ClientAssessmentResult_.dateCompleted), start, end)
        );
    }

    public Predicate completedFromDate(Instant date, Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
            completed(path, criteriaBuilder),
            criteriaBuilder.greaterThanOrEqualTo(path.get(ClientAssessmentResult_.dateCompleted), date)
        );
    }

    public Predicate inProgress(Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return withStatus(AssessmentStatus.IN_PROCESS, path, criteriaBuilder);
    }

    public Predicate completed(Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return withStatus(AssessmentStatus.COMPLETED, path, criteriaBuilder);
    }

    public Predicate hidden(Path<ClientAssessmentResult> path, CriteriaBuilder criteriaBuilder) {
        return withStatus(AssessmentStatus.HIDDEN, path, criteriaBuilder);
    }

    private Predicate withStatus(AssessmentStatus status, Path<ClientAssessmentResult> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(ClientAssessmentResult_.assessmentStatus), status);
    }
}
