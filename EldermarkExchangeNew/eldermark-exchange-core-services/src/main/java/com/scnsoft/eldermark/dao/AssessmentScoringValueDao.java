package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentScoringValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentScoringValueDao extends JpaRepository<AssessmentScoringValue, Long>, JpaSpecificationExecutor<AssessmentScoringValue> {
    List<AssessmentScoringValue> getAllByAssessment(Assessment assessment);

    List<AssessmentScoringValue> getAllByAssessmentIdIn(List<Long> assessmentIds);
}
