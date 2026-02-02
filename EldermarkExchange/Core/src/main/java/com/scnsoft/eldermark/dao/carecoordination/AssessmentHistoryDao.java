package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentHistoryDao extends JpaRepository<ResidentAssessmentResult, Long>, JpaSpecificationExecutor<ResidentAssessmentResult> {

    Page<ResidentAssessmentResult> getAllByIdOrChainId(Long id, Long chainId, final Pageable pageable);
}

