package com.scnsoft.eldermark.dump.dao;

import com.scnsoft.eldermark.dump.entity.assessment.ClientAssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAssessmentResultDao extends JpaRepository<ClientAssessmentResult, Long>, JpaSpecificationExecutor<ClientAssessmentResult> {
}
