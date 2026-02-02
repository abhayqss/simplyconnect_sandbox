package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentComprehensiveAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentComprehensiveAssessmentJpaDao extends JpaRepository<ResidentComprehensiveAssessment, Long> {

}
