package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentDao extends JpaRepository<Assessment, Long>, JpaSpecificationExecutor<Assessment> {
}
