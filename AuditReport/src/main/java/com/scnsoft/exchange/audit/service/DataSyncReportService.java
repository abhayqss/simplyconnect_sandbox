package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface DataSyncReportService<T extends ReportDto> extends ReportService<T> {
    public Date getReportMinValidDate();
    public SyncRange getRange(ReportFilter filter);
}
