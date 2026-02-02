package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.MpiReportDaoImpl;
import com.scnsoft.exchange.audit.model.MpiReportEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "mpiReportService")
public class MPIReportServiceImpl extends BaseReportService<MpiReportEntry> implements ReportService<MpiReportEntry> {

    @Autowired
    public MPIReportServiceImpl(MpiReportDaoImpl dao) {
        super(dao);
    }
}
