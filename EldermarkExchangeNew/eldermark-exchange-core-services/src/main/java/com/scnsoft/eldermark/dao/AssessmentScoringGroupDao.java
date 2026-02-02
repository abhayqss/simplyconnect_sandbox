package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.assessment.AssessmentScoringGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentScoringGroupDao extends JpaRepository<AssessmentScoringGroup, Long> {
    Optional<AssessmentScoringGroup> findTop1ByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqual(Long assessmentId, Long scoreLow, Long scoreHigh);
}
