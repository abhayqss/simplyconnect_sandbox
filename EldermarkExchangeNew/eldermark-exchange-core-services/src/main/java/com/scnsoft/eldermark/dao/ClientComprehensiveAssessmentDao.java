package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ClientComprehensiveAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientComprehensiveAssessmentDao extends JpaRepository<ClientComprehensiveAssessment, Long> {

    Optional<ClientComprehensiveAssessment> findByClientAssessmentResult_Id(Long clientAssessmentResultId);
}
