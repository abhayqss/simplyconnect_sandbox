package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.sdoh.SdohReportRowData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SdohReportRowDataDao extends JpaRepository<SdohReportRowData, Long>, JpaSpecificationExecutor<SdohReportRowData> {

    void deleteAllBySdohReportLogId(Long sdohReportLogId);

}
