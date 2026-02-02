package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Assessment")
public class AuditLogAssessmentRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClientAssessmentResult assessmentResult;

    @Column(name = "assessment_result_id", nullable = false)
    private Long assessmentResultId;

    public ClientAssessmentResult getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(ClientAssessmentResult assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public Long getAssessmentResultId() {
        return assessmentResultId;
    }

    public void setAssessmentResultId(Long assessmentResultId) {
        this.assessmentResultId = assessmentResultId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(assessmentResultId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.ASSESSMENT;
    }
}
