package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.Assessment;
import com.scnsoft.eldermark.entity.AssessmentScoringValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentScoringValueDao extends JpaRepository<AssessmentScoringValue, Long>, JpaSpecificationExecutor<AssessmentScoringValue> {
    List<AssessmentScoringValue> getAllByAssessment(Assessment assessment);
}
