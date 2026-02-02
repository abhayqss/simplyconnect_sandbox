package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Problem")
public class AuditLogProblemRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "problem_observation_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProblemObservation problem;

    @Column(name = "problem_observation_id", nullable = false)
    private Long problemId;

    public ProblemObservation getProblem() {
        return problem;
    }

    public void setProblem(ProblemObservation problem) {
        this.problem = problem;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(problemId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.PROBLEM;
    }
}
