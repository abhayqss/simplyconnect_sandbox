package com.scnsoft.eldermark.dump.dao;


import com.scnsoft.eldermark.dump.entity.assessment.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentDao extends JpaRepository<Assessment, Long>, JpaSpecificationExecutor<Assessment> {

    Optional<Assessment> findByCode(String code);
}
