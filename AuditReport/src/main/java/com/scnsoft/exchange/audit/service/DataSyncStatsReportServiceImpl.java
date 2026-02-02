package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.DataSyncStatsDaoImpl;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.DataSyncStatsDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "dataSyncStatsReportService")
public class DataSyncStatsReportServiceImpl extends BaseReportService<DataSyncStatsDto> implements DataSyncReportService<DataSyncStatsDto> {

    private DataSyncStatsDaoImpl dao;

    @Autowired
    public DataSyncStatsReportServiceImpl(DataSyncStatsDaoImpl dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public SyncRange getRange(ReportFilter filter) {
        return dao.getReportRange(filter);
    }

    @Override
    public Date getReportMinValidDate() {
        return dao.getMinDate();
    }
}
