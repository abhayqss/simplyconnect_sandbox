package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderObservationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResearchOrderObservationResultDao extends JpaRepository<LabResearchOrderObservationResult, Long> {

    Page<LabResearchOrderObservationResult> findAllByLabOrderId(Long labOrderId, Pageable pageabless);
}
