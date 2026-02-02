package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.DataSyncLogReportDaoImpl;
import com.scnsoft.exchange.audit.dao.DataSyncReportDaoImpl;
import com.scnsoft.exchange.audit.model.DataSyncLogDto;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value = "dataSyncLogReportService")
public class DataSyncLogReportServiceImpl extends BaseReportService<DataSyncLogDto> implements DataSyncReportService<DataSyncLogDto> {

    private DataSyncLogReportDaoImpl dao;

    @Autowired
    public DataSyncLogReportServiceImpl(DataSyncLogReportDaoImpl dao) {
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
