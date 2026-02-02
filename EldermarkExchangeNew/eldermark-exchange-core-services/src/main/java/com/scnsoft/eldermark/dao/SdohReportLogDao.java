package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SdohReportLogDao extends JpaRepository<SdohReportLog, Long>, JpaSpecificationExecutor<SdohReportLog>, IdProjectionRepository<Long> {

    Optional<SdohReportLog> findTopByOrganizationIdOrderByPeriodEndDesc(Long organizationId);
}
