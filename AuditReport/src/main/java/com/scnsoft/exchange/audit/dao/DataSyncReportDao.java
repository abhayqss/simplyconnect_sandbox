package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;

import java.util.Date;
import java.util.List;

public interface DataSyncReportDao<T extends ReportDto> extends ReportDao<T> {
    public Date getMinDate();
    public SyncRange getReportRange(ReportFilter filter);
}