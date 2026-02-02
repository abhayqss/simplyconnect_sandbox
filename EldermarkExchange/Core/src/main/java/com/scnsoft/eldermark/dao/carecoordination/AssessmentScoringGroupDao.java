package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.AssessmentScoringGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentScoringGroupDao extends JpaRepository<AssessmentScoringGroup, Long>, JpaSpecificationExecutor<AssessmentScoringGroup> {

    AssessmentScoringGroup findFirstByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqual(Long assessmentId, Long scoreLow, Long scoreHigh);

    AssessmentScoringGroup findFirstByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqualAndPassedHighEducation(Long assessmentId, Long scoreLow, Long scoreHigh, boolean passedHighSchool);

    Page<AssessmentScoringGroup> getAllByAssessment_IdOrderByScoreLowAsc(Long assessmentId, Pageable pageable);
}
