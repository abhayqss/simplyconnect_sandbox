package com.scnsoft.eldermark.dao.report;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.report.ReportConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportConfigurationDao extends AppJpaRepository<ReportConfiguration, ReportType> {
}
