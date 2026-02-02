package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.DataSyncReportDaoImpl;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "dataSyncReportService")
public class DataSyncReportServiceImpl extends BaseReportService<SyncReportEntry> implements DataSyncReportService<SyncReportEntry> {

    private DataSyncReportDaoImpl dao;

    @Autowired
    public DataSyncReportServiceImpl(DataSyncReportDaoImpl dao) {
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
